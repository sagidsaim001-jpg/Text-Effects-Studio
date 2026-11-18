@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.saji.texteffects

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.audio.DefaultGainProvider
import androidx.media3.common.audio.GainProcessor
import androidx.media3.common.audio.SpeedProvider
import androidx.media3.effect.BitmapOverlay
import androidx.media3.effect.Brightness
import androidx.media3.effect.Contrast
import androidx.media3.effect.HslAdjustment
import androidx.media3.effect.OverlayEffect
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
    val saturation: Float = 0f,
    val filter: String = "Original",
    val aspectRatio: String = "Original",
    val resolutionShortSide: Int = 1080,
    val frameRate: Int = 30,
    val trimStartMs: Long = 0L,
    val trimEndMs: Long? = null,
    val removeAudio: Boolean = false,
    val audioVolume: Float = 1f,
    val musicVolume: Float = 0.8f,
    val fadeInMs: Long = 0L,
    val fadeOutMs: Long = 0L,
    val overlayText: String = "",
    val overlaySticker: String = "",
    val overlayPosition: String = "Bottom",
    val overlayTextSize: Float = 68f,
    val overlayTextColor: Int = android.graphics.Color.WHITE
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

        val overlays = mutableListOf<BitmapOverlay>()
        val effects = buildEffects(context, source, settings, overlays)
        val editedMediaItem = EditedMediaItem.Builder(mediaItem)
            .setEffects(effects)
            .setFrameRate(settings.frameRate.coerceIn(24, 60))
            .setRemoveAudio(settings.removeAudio)
            .apply {
                val speed = settings.speed.coerceIn(0.25f, 4f)
                if (speed != 1f) {
                    setSpeed(object : SpeedProvider {
                        override fun getSpeed(timeUs: Long): Float = speed
                        override fun getNextSpeedChangeTimeUs(timeUs: Long): Long = C.TIME_UNSET
                    })
                }
            }
            .build()

        val outputFile = File(context.cacheDir, "saji_export_${System.currentTimeMillis()}.mp4")
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: androidx.media3.transformer.Composition, result: ExportResult) {
                releaseOverlays(overlays)
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
                releaseOverlays(overlays)
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

    fun exportVideos(
        context: Context,
        sources: List<Uri>,
        settings: VideoRenderSettings,
        backgroundAudioUri: Uri? = null,
        onExportComplete: (Uri) -> Unit,
        onExportError: (String) -> Unit
    ): Transformer {
        val selectedSources = sources.distinct().take(20)
        require(selectedSources.isNotEmpty()) { "Import at least one video clip." }

        val overlays = mutableListOf<BitmapOverlay>()
        val editedItems = selectedSources.map { source ->
            val clipBuilder = MediaItem.ClippingConfiguration.Builder()
                .setStartPositionMs(settings.trimStartMs.coerceAtLeast(0L))
            settings.trimEndMs?.takeIf { it > settings.trimStartMs }?.let {
                clipBuilder.setEndPositionMs(it)
            }
            val mediaItem = MediaItem.Builder()
                .setUri(source)
                .setClippingConfiguration(clipBuilder.build())
                .build()

            val builder = EditedMediaItem.Builder(mediaItem)
                .setEffects(buildEffects(context, source, settings, overlays))
                .setFrameRate(settings.frameRate.coerceIn(24, 60))
                .setRemoveAudio(settings.removeAudio)
            val speed = settings.speed.coerceIn(0.25f, 4f)
            if (speed != 1f) {
                builder.setSpeed(object : SpeedProvider {
                    override fun getSpeed(timeUs: Long): Float = speed
                    override fun getNextSpeedChangeTimeUs(timeUs: Long): Long = C.TIME_UNSET
                })
            }
            builder.build()
        }

        val videoSequence = androidx.media3.transformer.EditedMediaItemSequence.withAudioAndVideoFrom(editedItems)
        val sequences = mutableListOf(videoSequence)
        if (backgroundAudioUri != null) {
            val musicItem = EditedMediaItem.Builder(MediaItem.fromUri(backgroundAudioUri))
                .setRemoveVideo(true)
                .setEffects(
                    Effects(
                        listOf(
                            GainProcessor(
                                DefaultGainProvider.Builder(settings.musicVolume.coerceIn(0f, 1f)).build()
                            )
                        ),
                        emptyList()
                    )
                )
                .build()
            val musicSequence = androidx.media3.transformer.EditedMediaItemSequence
                .withAudioFrom(listOf(musicItem))
                .buildUpon()
                .setIsLooping(true)
                .build()
            sequences += musicSequence
        }
        val composition = androidx.media3.transformer.Composition.Builder(sequences).build()
        val outputFile = File(context.cacheDir, "saji_autocut_${System.currentTimeMillis()}.mp4")
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: androidx.media3.transformer.Composition, result: ExportResult) {
                releaseOverlays(overlays)
                try {
                    val savedUri = publishVideo(context, outputFile)
                    outputFile.delete()
                    onExportComplete(savedUri)
                } catch (error: Exception) {
                    outputFile.delete()
                    onExportError(error.message ?: "AutoCut finished but saving failed.")
                }
            }

            override fun onError(
                composition: androidx.media3.transformer.Composition,
                result: ExportResult,
                exception: ExportException
            ) {
                releaseOverlays(overlays)
                outputFile.delete()
                onExportError(exception.message ?: "Multi-clip export failed. Try clips with compatible formats.")
            }
        }
        val transformer = Transformer.Builder(context)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(listener)
            .build()
        transformer.start(composition, outputFile.absolutePath)
        return transformer
    }

    private fun buildEffects(
        context: Context,
        source: Uri,
        settings: VideoRenderSettings,
        overlays: MutableList<BitmapOverlay>
    ): Effects {
        val videoEffects = mutableListOf<androidx.media3.common.Effect>()
        if (settings.rotationDegrees % 360f != 0f) {
            videoEffects += ScaleAndRotateTransformation.Builder()
                .setRotationDegrees(settings.rotationDegrees % 360f).build()
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
            "Warm" -> videoEffects += HslAdjustment.Builder().adjustHue(10f).adjustSaturation(12f).build()
            "Cool" -> videoEffects += HslAdjustment.Builder().adjustHue(-10f).adjustSaturation(8f).build()
            "Vintage" -> videoEffects += HslAdjustment.Builder().adjustHue(7f).adjustSaturation(-24f).adjustLightness(3f).build()
            "Fade" -> videoEffects += HslAdjustment.Builder().adjustSaturation(-18f).adjustLightness(8f).build()
        }
        if (settings.saturation != 0f) {
            videoEffects += HslAdjustment.Builder()
                .adjustSaturation(settings.saturation.coerceIn(-100f, 100f)).build()
        }

        val targetSide = settings.resolutionShortSide.coerceIn(360, 2160)
        if (targetSide != 1080) videoEffects += Presentation.createForShortSide(targetSide)
        when (settings.aspectRatio) {
            "9:16" -> videoEffects += Presentation.createForAspectRatio(9f / 16f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "16:9" -> videoEffects += Presentation.createForAspectRatio(16f / 9f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "1:1" -> videoEffects += Presentation.createForAspectRatio(1f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "4:5" -> videoEffects += Presentation.createForAspectRatio(4f / 5f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
            "3:4" -> videoEffects += Presentation.createForAspectRatio(3f / 4f, Presentation.LAYOUT_SCALE_TO_FIT_WITH_CROP)
        }

        createOverlay(context, settings)?.let { overlay ->
            overlays += overlay
            videoEffects += OverlayEffect(listOf(overlay))
        }
        return Effects(buildAudioProcessors(context, source, settings), videoEffects)
    }

    private fun buildAudioProcessors(
        context: Context,
        source: Uri,
        settings: VideoRenderSettings
    ): List<androidx.media3.common.audio.AudioProcessor> {
        if (settings.removeAudio) return emptyList()
        val volume = settings.audioVolume.coerceIn(0f, 1f)
        val fadeIn = settings.fadeInMs.coerceIn(0L, 30_000L)
        val fadeOut = settings.fadeOutMs.coerceIn(0L, 30_000L)
        if (volume >= 0.999f && fadeIn == 0L && fadeOut == 0L) return emptyList()

        val gainBuilder = DefaultGainProvider.Builder(volume)
        if (fadeIn > 0L) {
            gainBuilder.addFadeAt(0L, fadeIn * 1_000L, DefaultGainProvider.FADE_IN_LINEAR)
        }
        if (fadeOut > 0L) {
            val durationMs = readDurationMs(context, source)
            if (durationMs > 0L) {
                val fadeStartUs = (durationMs - fadeOut).coerceAtLeast(0L) * 1_000L
                gainBuilder.addFadeAt(fadeStartUs, fadeOut * 1_000L, DefaultGainProvider.FADE_OUT_LINEAR)
            }
        }
        return listOf(GainProcessor(gainBuilder.build()))
    }

    private fun readDurationMs(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
        } catch (_: Exception) {
            0L
        } finally {
            try { retriever.release() } catch (_: Exception) { }
        }
    }

    private fun createOverlay(context: Context, settings: VideoRenderSettings): BitmapOverlay? {
        val text = settings.overlayText.trim().take(120)
        val sticker = settings.overlaySticker.trim()
        if (text.isEmpty() && sticker.isEmpty()) return null

        val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val centerX = 540f
        val anchorY = when (settings.overlayPosition) {
            "Top" -> 300f
            "Center" -> 960f
            else -> 1580f
        }
        if (sticker.isNotEmpty()) {
            val stickerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.WHITE
                textAlign = Paint.Align.CENTER
                textSize = 132f
                typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                setShadowLayer(12f, 0f, 4f, android.graphics.Color.BLACK)
            }
            canvas.drawText(sticker, centerX, anchorY - if (text.isNotEmpty()) 110f else 0f, stickerPaint)
        }
        if (text.isNotEmpty()) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = settings.overlayTextColor
                textAlign = Paint.Align.CENTER
                textSize = settings.overlayTextSize.coerceIn(28f, 112f)
                typeface = Typeface.create("sans-serif", Typeface.BOLD)
                setShadowLayer(8f, 0f, 3f, android.graphics.Color.BLACK)
            }
            val maxWidth = 930f
            val lines = mutableListOf<String>()
            var current = StringBuilder()
            text.replace("\n", " ").split(" ").forEach { word ->
                val candidate = if (current.isEmpty()) word else "${current} ${word}"
                if (paint.measureText(candidate) > maxWidth && current.isNotEmpty()) {
                    lines += current.toString()
                    current = StringBuilder(word)
                } else {
                    if (current.isNotEmpty()) current.append(' ')
                    current.append(word)
                }
            }
            if (current.isNotEmpty()) lines += current.toString()
            val lineHeight = paint.textSize * 1.18f
            val startY = anchorY + (lines.size - 1) * lineHeight / 2f
            lines.take(3).forEachIndexed { index, line ->
                canvas.drawText(line, centerX, startY + index * lineHeight, paint)
            }
        }
        return BitmapOverlay.createStaticBitmapOverlay(bitmap)
    }

    private fun releaseOverlays(overlays: List<BitmapOverlay>) {
        overlays.forEach { overlay ->
            try { overlay.release() } catch (_: Exception) { }
        }
    }

    fun extractAudio(
        context: Context,
        source: Uri,
        onExportComplete: (Uri) -> Unit,
        onExportError: (String) -> Unit
    ): Transformer {
        val audioOnlyItem = EditedMediaItem.Builder(MediaItem.fromUri(source))
            .setRemoveVideo(true)
            .build()
        val outputFile = File(context.cacheDir, "saji_audio_${System.currentTimeMillis()}.m4a")
        val listener = object : Transformer.Listener {
            override fun onCompleted(composition: androidx.media3.transformer.Composition, result: ExportResult) {
                try {
                    val savedUri = publishAudio(context, outputFile)
                    outputFile.delete()
                    onExportComplete(savedUri)
                } catch (error: Exception) {
                    outputFile.delete()
                    onExportError(error.message ?: "Audio extracted but saving failed.")
                }
            }

            override fun onError(
                composition: androidx.media3.transformer.Composition,
                result: ExportResult,
                exception: ExportException
            ) {
                outputFile.delete()
                onExportError(exception.message ?: "Audio extraction failed. The selected video may not contain an audio track.")
            }
        }
        val transformer = Transformer.Builder(context)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(listener)
            .build()
        transformer.start(audioOnlyItem, outputFile.absolutePath)
        return transformer
    }

    private fun publishAudio(context: Context, sourceFile: File): Uri {
        if (!sourceFile.exists() || sourceFile.length() == 0L) {
            throw IllegalStateException("The extracted audio file is empty.")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, "SAJI_Extracted_Audio_${System.currentTimeMillis()}.m4a")
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
                put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC + "/SAJI CapCut 2")
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Gallery could not create an audio file.")
            try {
                resolver.openOutputStream(uri)?.use { output ->
                    FileInputStream(sourceFile).use { input -> input.copyTo(output) }
                } ?: throw IllegalStateException("Music output stream is unavailable.")
                resolver.update(uri, ContentValues().apply {
                    put(MediaStore.Audio.Media.IS_PENDING, 0)
                }, null, null)
                return uri
            } catch (error: Exception) {
                resolver.delete(uri, null, null)
                throw error
            }
        }
        val folder = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir
        val destination = File(folder, "SAJI_Extracted_Audio_${System.currentTimeMillis()}.m4a")
        sourceFile.copyTo(destination, overwrite = true)
        return Uri.fromFile(destination)
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
