package com.saji.texteffects

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private val Bg = Color(0xFF090B12)
private val Panel = Color(0xFF151925)
private val Accent = Color(0xFF9B7BFF)
private val Muted = Color(0xFF9299AA)

private data class TextEffect(val name: String, val hint: String, val color: Color)

private val effects = listOf(
    TextEffect("Neon", "Glow", Color(0xFF63E7FF)),
    TextEffect("3D", "Depth", Color(0xFFFFB86B)),
    TextEffect("Chrome", "Metal", Color(0xFFE5EAF4)),
    TextEffect("Glitch", "RGB", Color(0xFFFF668F)),
    TextEffect("Fire", "Flame", Color(0xFFFF874D)),
    TextEffect("Outline", "Stroke", Color(0xFFB9A4FF)),
    TextEffect("Shadow", "Soft", Color(0xFF9DA8C4)),
    TextEffect("Retro", "Vibe", Color(0xFFFFD36E)),
    TextEffect("Pulse", "Beat", Color(0xFF70F0D0)),
    TextEffect("Bounce", "Motion", Color(0xFFFFB86B)),
    TextEffect("Shake", "Jitter", Color(0xFFFF668F)),
    TextEffect("Typewriter", "Reveal", Color(0xFFB9A4FF))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StudioApp() }
    }
}

@Composable
private fun StudioApp() {
    var text by remember { mutableStateOf("MAKE IT\nSTAND OUT") }
    var selected by remember { mutableStateOf("Neon") }
    var intensity by remember { mutableFloatStateOf(0.75f) }
    var fontSize by remember { mutableFloatStateOf(42f) }
    var animationEnabled by remember { mutableStateOf(true) }
    var clipPosition by remember { mutableFloatStateOf(2f) }
    var clipDuration by remember { mutableFloatStateOf(12f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var scale by remember { mutableFloatStateOf(1f) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> videoUri = uri }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg,
        surface = Panel,
        primary = Accent,
        onBackground = Color.White,
        onSurface = Color.White
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SAJI", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
                        Text("Text Effects Studio", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("BETA 0.1", color = Muted, fontSize = 10.sp,
                        modifier = Modifier.background(Panel, CircleShape).padding(horizontal = 12.dp, vertical = 7.dp))
                }

                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { videoPicker.launch("video/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(if (videoUri == null) "＋  Add Video" else "Change Video", fontWeight = FontWeight.Bold) }
                    Text(if (videoUri == null) "Start with a text preview" else "Video loaded",
                        color = Muted, fontSize = 12.sp)
                }

                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(9f / 12f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF25213B), Color(0xFF10131E), Color(0xFF25152D)))),
                    contentAlignment = Alignment.Center
                ) {
                    if (videoUri != null) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                VideoView(context).apply {
                                    setVideoURI(videoUri)
                                    setOnPreparedListener { it.isLooping = true; start() }
                                }
                            },
                            update = { view ->
                                if (view.tag != videoUri) {
                                    view.tag = videoUri
                                    view.setVideoURI(videoUri)
                                    view.setOnPreparedListener { it.isLooping = true; it.start() }
                                }
                            }
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PREVIEW", color = Color.White.copy(alpha = 0.35f), fontSize = 10.sp, letterSpacing = 4.sp)
                            Spacer(Modifier.height(26.dp))
                            EffectPreview(text, selected, fontSize, intensity, animationEnabled, rotation, scale)
                            Spacer(Modifier.height(26.dp))
                            Text("Your text preview appears here", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                        }
                    }
                    if (videoUri != null) {
                        Box(Modifier.align(Alignment.Center).padding(12.dp)) {
                            EffectPreview(text, selected, fontSize, intensity)
                        }
                    }
                    Text("9:16", color = Color.White.copy(alpha = 0.55f), fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp))
                }

                Spacer(Modifier.height(18.dp))
                SectionTitle("YOUR TEXT", "Edit the words")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                    shape = RoundedCornerShape(14.dp),
                    placeholder = { Text("Type something…", color = Muted) }
                )

                Spacer(Modifier.height(18.dp))
                SectionTitle("TEXT EFFECTS", "Tap to preview")
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    effects.forEach { effect ->
                        val active = selected == effect.name
                        Column(
                            modifier = Modifier.width(82.dp).clip(RoundedCornerShape(14.dp))
                                .background(if (active) Color(0xFF29233F) else Panel)
                                .border(if (active) 1.dp else 0.dp, if (active) Accent else Color.Transparent, RoundedCornerShape(14.dp))
                                .clickable { selected = effect.name }.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(Modifier.size(42.dp).clip(RoundedCornerShape(11.dp)).background(Color(0xFF242A39)), contentAlignment = Alignment.Center) {
                                Text("Aa", color = effect.color, fontSize = 19.sp, fontWeight = FontWeight.Black,
                                    style = TextStyle(shadow = Shadow(effect.color.copy(alpha = 0.8f), blurRadius = 12f)))
                            }
                            Spacer(Modifier.height(7.dp))
                            Text(effect.name, color = if (active) Color.White else Muted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Text(effect.hint, color = Muted, fontSize = 9.sp)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                SectionTitle("ANIMATION TIMELINE", "Set timing and keyframes")
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { animationEnabled = !animationEnabled }, colors = ButtonDefaults.buttonColors(containerColor = if (animationEnabled) Accent else Panel), shape = RoundedCornerShape(10.dp)) { Text(if (animationEnabled) "Ⅱ Pause" else "▶ Play") }
                    Text("Duration: " + clipDuration.toInt() + "s", color = Muted, fontSize = 11.sp)
                }
                Slider(value = clipPosition, onValueChange = { clipPosition = it }, valueRange = 0f..clipDuration)
                Text("Keyframes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Start", "Middle", "End").forEachIndexed { index, label ->
                        val value = when (index) { 0 -> 0f; 1 -> clipDuration / 2f; else -> clipDuration }
                        Button(onClick = { clipPosition = value; scale = if (index == 1) 1.18f else 1f; rotation = if (index == 1) -4f else 0f }, colors = ButtonDefaults.buttonColors(containerColor = if (kotlin.math.abs(clipPosition - value) < 0.2f) Accent else Panel), shape = RoundedCornerShape(9.dp)) { Text(label, fontSize = 11.sp) }
                    }
                }
                Spacer(Modifier.height(18.dp))
                SectionTitle("MOTION CONTROLS", "Transform your text")
                Text("Scale: " + scale.toString() + "x", color = Color.White, fontSize = 12.sp)
                Slider(value = scale, onValueChange = { scale = it }, valueRange = 0.5f..1.8f)
                Text("Rotation: " + rotation.toInt() + " degrees", color = Color.White, fontSize = 12.sp)
                Slider(value = rotation, onValueChange = { rotation = it }, valueRange = -30f..30f)
                Spacer(Modifier.height(18.dp))
                SectionTitle("ADJUST", "Fine-tune your style")
                Text("Effect intensity", color = Color.White, fontSize = 13.sp)
                Slider(value = intensity, onValueChange = { intensity = it }, modifier = Modifier.fillMaxWidth())
                Text("Text size", color = Color.White, fontSize = 13.sp)
                Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 22f..64f, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = { text = "MAKE IT\nSTAND OUT"; selected = "Neon"; intensity = 0.75f; fontSize = 42f; animationEnabled = true; clipPosition = 2f; clipDuration = 12f; rotation = 0f; scale = 1f },
                        colors = ButtonDefaults.buttonColors(containerColor = Panel), shape = RoundedCornerShape(12.dp)) {
                        Text("Reset")
                    }
                    Button(onClick = { /* Export pipeline will be added in the next milestone. */ },
                        enabled = false,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                        Text("Export (coming next)")
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text("Phase 2 preview • Motion effects + timeline controls • MP4 export is still in development",
                    color = Muted, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(Modifier.weight(1f))
        Text(subtitle, color = Muted, fontSize = 10.sp)
    }
}

@Composable
private fun EffectPreview(text: String, effect: String, size: Float, intensity: Float, animationEnabled: Boolean = true, rotation: Float = 0f, scale: Float = 1f) {
    val transition = rememberInfiniteTransition(label = "textEffect")
    val motion by transition.animateFloat(initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(animation = tween(900), repeatMode = RepeatMode.Reverse), label = "motion")
    val glow = 8f + intensity * 28f
    val base = when (effect) {
        "Pulse" -> Color(0xFF70F0D0)
        "Bounce" -> Color(0xFFFFB86B)
        "Shake" -> Color(0xFFFF668F)
        "Typewriter" -> Color(0xFFB9A4FF)
        "Neon" -> Color(0xFF78E9FF)
        "3D" -> Color(0xFFFFC078)
        "Chrome" -> Color(0xFFE8EDF7)
        "Glitch" -> Color(0xFFFF6D9C)
        "Fire" -> Color(0xFFFF9B50)
        "Outline" -> Color(0xFFC4B0FF)
        "Shadow" -> Color.White
        else -> Color(0xFFFFD66E)
    }
    Box(contentAlignment = Alignment.Center) {
        if (effect == "3D") {
            Text(text, color = Color(0xFF7550C9), fontSize = size.sp, fontWeight = FontWeight.Black,
                lineHeight = (size * 1.05f).sp, textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 4.dp, top = 5.dp))
        }
        if (effect == "Glitch") {
            Text(text, color = Color(0xFF39DDF2).copy(alpha = 0.7f), fontSize = size.sp,
                fontWeight = FontWeight.Black, lineHeight = (size * 1.05f).sp,
                textAlign = TextAlign.Center, modifier = Modifier.padding(end = 4.dp))
        }
        val displayText = if (effect == "Typewriter" && animationEnabled) text.take((text.length * motion).toInt().coerceIn(1, text.length.coerceAtLeast(1))) else text.ifBlank { "YOUR TEXT" }
        Text(
            text = displayText,
            fontSize = size.sp,
            lineHeight = (size * 1.05f).sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = if (effect == "Chrome") Color.White else base,
            modifier = Modifier.graphicsLayer {
                val pulse = if (animationEnabled && effect == "Pulse") 0.94f + motion * 0.12f else 1f
                scaleX = scale * pulse
                scaleY = scale * pulse
                rotationZ = rotation
                translationY = if (animationEnabled && effect == "Bounce") -motion * 14f else 0f
                translationX = if (animationEnabled && effect == "Shake") (motion - 0.5f) * 12f else 0f
                alpha = if (animationEnabled && effect == "Glitch") 0.78f + motion * 0.22f else 1f
            },
            style = when (effect) {
                "Neon", "Fire" -> TextStyle(shadow = Shadow(base.copy(alpha = intensity), blurRadius = glow))
                "Outline" -> TextStyle(drawStyle = Stroke(width = 2.5f), shadow = Shadow(base, blurRadius = 5f))
                "Shadow", "3D" -> TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.9f), blurRadius = 8f))
                "Chrome" -> TextStyle(brush = Brush.verticalGradient(listOf(Color.White, Color(0xFF8795AA), Color.White)))
                "Retro" -> TextStyle(shadow = Shadow(Color(0xFFFF4FA3), blurRadius = 5f))
                else -> TextStyle(shadow = Shadow(base.copy(alpha = intensity), blurRadius = glow / 2))
            }
        )
    }
}
