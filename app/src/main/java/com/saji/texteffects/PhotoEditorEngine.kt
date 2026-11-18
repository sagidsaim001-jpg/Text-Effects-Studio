package com.saji.texteffects

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream

data class PhotoEditSettings(
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val saturation: Float = 1f,
    val warmth: Float = 0f,
    val vignette: Float = 0f,
    val filter: String = "Original",
    val rotationDegrees: Float = 0f,
    val aspectRatio: String = "Original"
)

object PhotoEditorEngine {
    fun loadBitmap(context: Context, uri: Uri, maxPixels: Int = 4_000_000): Bitmap? {
        return try {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, bounds)
            }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

            var sample = 1
            while ((bounds.outWidth / sample) * (bounds.outHeight / sample) > maxPixels) {
                sample *= 2
            }
            val options = BitmapFactory.Options().apply {
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }
        } catch (_: Exception) {
            null
        }
    }

    fun render(source: Bitmap, settings: PhotoEditSettings): Bitmap {
        var working = cropToRatio(source, settings.aspectRatio)
        if (settings.rotationDegrees % 360f != 0f) {
            val matrix = Matrix().apply { postRotate(settings.rotationDegrees % 360f) }
            working = Bitmap.createBitmap(working, 0, 0, working.width, working.height, matrix, true)
        }

        val matrix = ColorMatrix().apply {
            setSaturation(settings.saturation.coerceIn(0f, 2f))
        }
        if (settings.filter == "B&W" || settings.filter == "Mono") {
            matrix.setSaturation(0f)
        }
        if (settings.filter == "Vintage") {
            matrix.postConcat(ColorMatrix(floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f, 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )))
        }
        if (settings.filter == "Cool") {
            matrix.postConcat(channelMatrix(0.94f, 1.02f, 1.12f))
        } else if (settings.filter == "Warm") {
            matrix.postConcat(channelMatrix(1.12f, 1.02f, 0.9f))
        }
        if (settings.warmth != 0f) {
            val warm = settings.warmth.coerceIn(-1f, 1f)
            matrix.postConcat(channelMatrix(1f + warm * 0.16f, 1f, 1f - warm * 0.16f))
        }
        if (settings.filter == "Invert") {
            matrix.postConcat(ColorMatrix(floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            )))
        }

        val contrast = settings.contrast.coerceIn(0.25f, 2f)
        matrix.postConcat(ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, 128f * (1f - contrast) + settings.brightness.coerceIn(-1f, 1f) * 255f,
            0f, contrast, 0f, 0f, 128f * (1f - contrast) + settings.brightness.coerceIn(-1f, 1f) * 255f,
            0f, 0f, contrast, 0f, 128f * (1f - contrast) + settings.brightness.coerceIn(-1f, 1f) * 255f,
            0f, 0f, 0f, 1f, 0f
        )))

        val output = Bitmap.createBitmap(working.width, working.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }
        canvas.drawBitmap(working, 0f, 0f, paint)

        if (settings.vignette > 0f) {
            val radius = maxOf(output.width, output.height) * 0.78f
            val alpha = (settings.vignette.coerceIn(0f, 1f) * 190).toInt()
            val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = RadialGradient(
                    output.width / 2f,
                    output.height / 2f,
                    radius,
                    intArrayOf(Color.TRANSPARENT, Color.argb(alpha, 0, 0, 0)),
                    floatArrayOf(0.42f, 1f),
                    Shader.TileMode.CLAMP
                )
            }
            canvas.drawRect(0f, 0f, output.width.toFloat(), output.height.toFloat(), vignettePaint)
        }
        if (working !== source) working.recycle()
        return output
    }

    private fun channelMatrix(red: Float, green: Float, blue: Float): ColorMatrix =
        ColorMatrix(floatArrayOf(
            red, 0f, 0f, 0f, 0f,
            0f, green, 0f, 0f, 0f,
            0f, 0f, blue, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))

    private fun cropToRatio(source: Bitmap, ratioName: String): Bitmap {
        val ratio = when (ratioName) {
            "9:16" -> 9f / 16f
            "16:9" -> 16f / 9f
            "1:1" -> 1f
            "4:5" -> 4f / 5f
            "3:4" -> 3f / 4f
            else -> return source
        }
        val current = source.width.toFloat() / source.height
        if (kotlin.math.abs(current - ratio) < 0.005f) return source
        val cropWidth: Int
        val cropHeight: Int
        if (current > ratio) {
            cropHeight = source.height
            cropWidth = (source.height * ratio).toInt().coerceAtLeast(1)
        } else {
            cropWidth = source.width
            cropHeight = (source.width / ratio).toInt().coerceAtLeast(1)
        }
        val left = (source.width - cropWidth) / 2
        val top = (source.height - cropHeight) / 2
        return Bitmap.createBitmap(source, left, top, cropWidth, cropHeight)
    }

    fun saveToGallery(context: Context, bitmap: Bitmap): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "SAJI_Photo_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SAJI CapCut 2")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IllegalStateException("Gallery could not create the photo.")
            try {
                resolver.openOutputStream(uri)?.use { output ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 94, output)) {
                        throw IllegalStateException("Photo encoding failed.")
                    }
                } ?: throw IllegalStateException("Gallery output stream is unavailable.")
                resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
                return uri
            } catch (error: Exception) {
                resolver.delete(uri, null, null)
                throw error
            }
        }

        val folder = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ?: context.filesDir
        val file = File(folder, "SAJI_Photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output -> bitmap.compress(Bitmap.CompressFormat.JPEG, 94, output) }
        return Uri.fromFile(file)
    }
}
