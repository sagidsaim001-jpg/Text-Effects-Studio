@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.saji.texteffects

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.audio.SpeedProvider
import androidx.media3.effect.Brightness
import androidx.media3.effect.Contrast
import androidx.media3.transformer.Effects
import androidx.media3.effect.Presentation
import androidx.media3.effect.RgbFilter
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File
import java.io.FileInputStream

data class VideoRenderSettings(
    val speed: Float = 1f,
    val rotationDegrees: Float = 0f,
    val brightness: Float = 0f,
    val contrast: Float = 0f,
    val filter: String = "Original",
    val aspectRatio: String = "Original",
    val resolutionShortSide: Int = 1080,
    val frameRate: Int = 30,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long? = null,
    val removeAudio: Boolean = false
)

object Media3EditorEngine {
    fun exportVideo(
        context: Context,
        source: Uri,
        settings: VideoRenderSettings,
        onExportComplete: (Uri) -> Unit,
        onExportError: (String) -> Unit
    ): Transformer {
        val clipBuilder = MediaItem.ClippingConfiguration.Builder()
            .setStartPositionMs(settings.trimStartMs.coerceAtLeast(0L))
        settings.trimEndMs?.takeIf { it > settings.trimStartMs }?.let {
            clipBuilder.setEndPositionMs(it)
        }
        val mediaItem = MediaItem.Builder()
            .setUri(source)
            .setClippingConfiguration(clipBuilder.build())
            .build()

        val videoEffects = mutableListOf<androidx.media3.common.Effect>()
        if (settings.rotationDegrees % 360f != 0f) {
            videoEffects += ScaleAndRotateTransformation.Builder()
                .setRotationDegrees(settings.rotationDegrees % 360f)
                .build()
        }
        if (settings.brightness != 0f) {
            videoEffects += Brightness(settings.brightness.coerceIn(-1f, 1f))
        }
        if (settings.contrast != 0f) {
            videoEffects += Contrast(settings.contrast.coerceIn(-1f, 1f))
        }
        when (settings.filter) {
            "B&W", "Mono" -> videoEffects += RgbFilter.createGrayscaleFilter()
            "Invert" -> videoEffects += RgbFilter.createInvertedFilter()
        }

        val targetSide = settings.resolutionShortSide.coerceIn(360, 2160)
        if (targetSide != 1080) {
            videoEffects += Presentation.createForShortSide(targetSide)
        }
        when (settings.aspectRatio) {
            "9:16" -> videoEffects += Presentation.createForAspectRatio(9f / 16f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "16:9" -> videoEffects += Presentation.createForAspectRatio(16f / 9f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "1:1" -> videoEffects += Presentation.createForAspectRatio(1f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "4:5" -> videoEffects += Presentation.createForAspectRatio(4f / 5f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
        }

        val editedBuilder = EditedMediaItem.Builder(mediaItem)
            .setEffects(Effects(emptyList(), videoEffects))
            .setFrameRate(settings.frameRate.coerceIn(24, 60))
            .setRemoveAudio(settings.removeAudio)
        val speed = settings.speed.coerceIn(0.25f, 4f)
        if (speed != 1f) {
            editedBuilder.setSpeed(object : SpeedProvider {
                override fun getSpeed(timeUs: Long): Float = speed
                override fun getNextSpeedChangeTimeUs(timeUs: Long): Long = C.TIME_UNSET
            })
        }
        val editedMediaItem = editedBuilder.build()

        val outputFile = File(context.cacheDir, "saji_export_${System.currentTimeMillis()}.mp4")
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: androidx.media3.transformer.Composition, result: ExportResult) {
                try {
                    val savedUri = publishVideo(context, outputFile)
                    outputFile.delete()
                    onExportComplete(savedUri)
                } catch (error: Exception) {
                    outputFile.delete()
                    onExportError(error.message ?: "Export finished but saving to Gallery failed.")
                }
            }

            override fun onError(
                composition: androidx.media3.transformer.Composition,
                result: ExportResult,
                exception: ExportException
            ) {
                outputFile.delete()
                onExportError(exception.message ?: "Video export failed. Please try a shorter clip or lower resolution.")
            }
        }

        val transformer = Transformer.Builder(context)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(listener)
            .build()
        transformer.start(editedMediaItem, outputFile.absolutePath)
        return transformer
    }

    private fun publishVideo(context: Context, sourceFile: File): Uri {
        if (!sourceFile.exists() || sourceFile.length() == 0L) {
            throw IllegalStateException("The export file is empty.")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val name = "SAJI_CapCut_2_${System.currentTimeMillis()}.mp4"
            val values = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, name)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/SAJI CapCut 2")
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Gallery could not create an output file.")
            try {
                resolver.openOutputStream(uri)?.use { output ->
                    FileInputStream(sourceFile).use { input -> input.copyTo(output) }
                } ?: throw IllegalStateException("Gallery output stream is unavailable.")
                val ready = ContentValues().apply { put(MediaStore.Video.Media.IS_PENDING, 0) }
                resolver.update(uri, ready, null, null)
                return uri
            } catch (error: Exception) {
                resolver.delete(uri, null, null)
                throw error
            }
        }

        val folder = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            ?: context.filesDir
        val destination = File(folder, "SAJI_CapCut_2_${System.currentTimeMillis()}.mp4")
        sourceFile.copyTo(destination, overwrite = true)
        return Uri.fromFile(destination)
    }
}
