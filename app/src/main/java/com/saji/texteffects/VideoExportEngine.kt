package com.saji.texteffects

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.CanvasOverlay
import androidx.media3.effect.OverlayEffect
import androidx.media3.transformer.Composition
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File
import java.io.FileInputStream

data class TextExportStyle(
    val text: String,
    val effect: String,
    val fontSize: Float,
    val intensity: Float,
    val scale: Float,
    val rotation: Float,
    val animationDurationSeconds: Float
)

@OptIn(UnstableApi::class)
object VideoExportEngine {

    fun export(
        context: Context,
        inputUri: Uri,
        style: TextExportStyle,
        onStarted: () -> Unit,
        onCompleted: (String) -> Unit,
        onError: (String) -> Unit
    ): Transformer {
        val outputFile = File(context.cacheDir, "saji_export_${System.currentTimeMillis()}.mp4")
        val overlay = SajiTextCanvasOverlay(style)
        val effects = Effects(emptyList(), listOf(OverlayEffect(listOf(overlay))))
        val editedItem = EditedMediaItem.Builder(MediaItem.fromUri(inputUri))
            .setEffects(effects)
            .build()

        val transformer = Transformer.Builder(context)
            .addListener(object : Transformer.Listener {
                override fun onCompleted(composition: Composition, exportResult: ExportResult) {
                    try {
                        val savedUri = copyToGallery(context, outputFile)
                        outputFile.delete()
                        onCompleted(savedUri.toString())
                    } catch (error: Exception) {
                        onError("Export complete hua, lekin Gallery mein save nahi ho saka: ${error.localizedMessage ?: "unknown error"}")
                    }
                }

                override fun onError(
                    composition: Composition,
                    exportResult: ExportResult,
                    exportException: ExportException
                ) {
                    outputFile.delete()
                    onError("Export failed: ${exportException.message ?: "device video encoder error"}")
                }
            })
            .build()

        onStarted()
        transformer.start(editedItem, outputFile.absolutePath)
        return transformer
    }

    private fun copyToGallery(context: Context, source: File): Uri {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "SAJI_Text_Effect_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/SAJI Text Effects Studio")
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val destination = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            ?: error("Gallery storage entry create nahi ho saki")
        try {
            resolver.openOutputStream(destination)?.use { output ->
                FileInputStream(source).use { input -> input.copyTo(output) }
            } ?: error("Gallery output stream open nahi hua")
            val publish = ContentValues().apply {
                put(MediaStore.Video.Media.IS_PENDING, 0)
            }
            resolver.update(destination, publish, null, null)
            return destination
        } catch (error: Exception) {
            resolver.delete(destination, null, null)
            throw error
        }
    }
}

@OptIn(UnstableApi::class)
private class SajiTextCanvasOverlay(
    private val style: TextExportStyle
) : CanvasOverlay(true) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.create("sans-serif-black", Typeface.BOLD)
        textAlign = Paint.Align.CENTER
        strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas, presentationTimeUs: Long) {
        canvas.drawColor(AndroidColor.TRANSPARENT, PorterDuff.Mode.CLEAR)
        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()
        if (width <= 0f || height <= 0f || style.text.isBlank()) return

        val seconds = presentationTimeUs / 1_000_000f
        val duration = style.animationDurationSeconds.coerceAtLeast(0.5f)
        val phase = ((seconds % duration) / duration).coerceIn(0f, 1f)
        val pulse = if (style.effect == "Pulse") 0.94f + (0.06f * (1f + kotlin.math.sin(seconds * 5f))) else 1f
        val bounce = if (style.effect == "Bounce") kotlin.math.abs(kotlin.math.sin(seconds * 4f)) * height * 0.018f else 0f
        val shake = if (style.effect == "Shake") kotlin.math.sin(seconds * 28f) * width * 0.008f else 0f
        val visibleText = if (style.effect == "Typewriter") {
            val count = (style.text.length * phase).toInt().coerceIn(1, style.text.length)
            style.text.take(count)
        } else style.text

        canvas.save()
        canvas.translate(width / 2f + shake, height / 2f - bounce)
        canvas.rotate(style.rotation)
        canvas.scale(style.scale * pulse, style.scale * pulse)

        paint.textSize = (height * style.fontSize / 1000f).coerceIn(28f, 220f)
        val lines = visibleText.split("\n")
        val lineHeight = paint.textSize * 1.18f
        val startY = -((lines.size - 1) * lineHeight) / 2f
        val baseColor = when (style.effect) {
            "Fire" -> AndroidColor.rgb(255, 132, 54)
            "Chrome" -> AndroidColor.rgb(235, 240, 250)
            "Glitch" -> AndroidColor.rgb(255, 92, 142)
            "Outline" -> AndroidColor.rgb(190, 160, 255)
            "Pulse" -> AndroidColor.rgb(112, 240, 208)
            "Bounce" -> AndroidColor.rgb(255, 184, 107)
            "Shake" -> AndroidColor.rgb(255, 102, 143)
            "Typewriter" -> AndroidColor.rgb(185, 164, 255)
            else -> AndroidColor.rgb(99, 231, 255)
        }
        val glow = 8f + style.intensity.coerceIn(0f, 1f) * 28f

        lines.forEachIndexed { index, line ->
            val y = startY + index * lineHeight
            when (style.effect) {
                "Outline" -> {
                    paint.clearShadowLayer()
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = (paint.textSize * 0.055f).coerceAtLeast(2f)
                    paint.color = baseColor
                    canvas.drawText(line, 0f, y, paint)
                    paint.style = Paint.Style.FILL
                    paint.color = AndroidColor.WHITE
                    canvas.drawText(line, 0f, y, paint)
                }
                "3D" -> {
                    paint.clearShadowLayer()
                    paint.style = Paint.Style.FILL
                    paint.color = AndroidColor.rgb(92, 66, 150)
                    for (depth in 8 downTo 1) canvas.drawText(line, depth * 2.2f, y + depth * 2.2f, paint)
                    paint.color = AndroidColor.WHITE
                    canvas.drawText(line, 0f, y, paint)
                }
                "Glitch" -> {
                    paint.clearShadowLayer()
                    paint.color = AndroidColor.CYAN
                    canvas.drawText(line, -5f, y, paint)
                    paint.color = AndroidColor.MAGENTA
                    canvas.drawText(line, 5f, y, paint)
                    paint.color = AndroidColor.WHITE
                    canvas.drawText(line, 0f, y, paint)
                }
                "Fire" -> {
                    paint.clearShadowLayer()
                    paint.shader = LinearGradient(0f, y - paint.textSize, 0f, y, AndroidColor.YELLOW, AndroidColor.RED, Shader.TileMode.CLAMP)
                    canvas.drawText(line, 0f, y, paint)
                    paint.shader = null
                }
                "Neon" -> {
                    paint.color = baseColor
                    paint.style = Paint.Style.FILL
                    paint.setShadowLayer(glow, 0f, 0f, baseColor)
                    canvas.drawText(line, 0f, y, paint)
                    paint.clearShadowLayer()
                }
                else -> {
                    paint.color = if (style.effect == "Chrome") AndroidColor.WHITE else baseColor
                    paint.style = Paint.Style.FILL
                    paint.setShadowLayer(if (style.effect == "Shadow") glow else 0f, 3f, 4f, AndroidColor.BLACK)
                    canvas.drawText(line, 0f, y, paint)
                    paint.clearShadowLayer()
                }
            }
        }
        canvas.restore()
    }
}
