package com.saji.texteffects

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DetailCard = Color(0xFF242832)
private val DetailLine = Color(0xFF363C48)
private val DetailAccent = Color(0xFF25E0C0)
private val DetailWhite = Color(0xFFF6F7FA)
private val DetailMuted = Color(0xFF9299A6)

@Composable
internal fun ToolDetailControls(tool: String, detail: String) {
    when {
        detail == "Speed" -> SpeedDetailScreen()
        detail == "Animation" -> AnimationDetailScreen()
        tool == "Text" || detail in listOf("Text editor", "Add text", "Text templates", "Fonts", "Style", "Bubbles", "Tracking") -> TextDetailScreen(detail)
        tool == "Audio" || detail in listOf("Sounds", "Sound effects", "Extract audio", "Voiceover", "Text to speech", "Beats", "Fade", "Voice effects", "Noise reduction") -> AudioDetailScreen(detail)
        detail == "Adjust" -> AdjustDetailScreen()
        tool == "Transitions" || detail == "Transition" -> TransitionDetailScreen()
        tool == "Captions" || detail.contains("caption", ignoreCase = true) -> CaptionsDetailScreen(detail)
        detail in listOf("Mask", "Chroma key", "Keyframe", "Stabilize", "Cutout", "Remove background") -> AdvancedDetailScreen(detail)
        tool == "Effects" || tool == "Filters" || detail.contains("effect", ignoreCase = true) || detail == "Filters" -> AssetDetailScreen(tool, detail)
        tool == "Format" || tool == "Canvas" || detail == "Ratio" -> FormatDetailScreen()
        else -> GenericDetailScreen(tool, detail)
    }
}

@Composable
private fun SpeedDetailScreen() {
    var mode by remember { mutableStateOf("Normal") }
    var speed by remember { mutableFloatStateOf(1f) }
    var selectedCurve by remember { mutableStateOf("Montage") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("Normal", "Curve"), mode) { mode = it }
        Spacer(Modifier.height(8.dp))
        if (mode == "Normal") {
            Text("Speed  •  " + String.format("%.2f", speed) + "x", color = DetailWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Slider(value = speed / 10f, onValueChange = { speed = it * 10f }, valueRange = 0.01f..1f)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0.25f, 0.5f, 1f, 2f, 4f, 8f).forEach { value -> DetailChip(value.toString() + "x", speed == value) { speed = value } }
            }
            Spacer(Modifier.height(6.dp))
            DetailInfo("Pitch adjustment", "Keep original audio pitch when changing speed")
        } else {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("Montage", "Hero", "Bullet", "Jump cut", "Flash in", "Flash out", "Custom").forEach {
                    DetailChip(it, selectedCurve == it) { selectedCurve = it }
                }
            }
            Spacer(Modifier.height(8.dp))
            SpeedCurveGraph(selectedCurve)
            Text("Curve points", color = DetailWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("−", "＋", "Delete point", "Smooth").forEach { DetailChip(it, false) { } }
            }
            DetailInfo("Smooth slow motion", "Frame interpolation controls will be connected in the editing phase")
        }
    }
}

@Composable
private fun SpeedCurveGraph(curve: String) {
    val points = when (curve) {
        "Hero" -> listOf(0.08f, 0.18f, 0.88f, 0.38f, 0.08f)
        "Bullet" -> listOf(0.75f, 0.72f, 0.1f, 0.12f, 0.78f)
        "Jump cut" -> listOf(0.25f, 0.25f, 0.85f, 0.85f, 0.3f)
        else -> listOf(0.25f, 0.55f, 0.82f, 0.55f, 0.28f)
    }
    Box(Modifier.fillMaxWidth().height(70.dp).clipRoundedBackground(DetailCard).padding(8.dp)) {
        Canvas(Modifier.fillMaxWidth().height(54.dp)) {
            val w = size.width
            val h = size.height
            for (i in 0..4) {
                val y = h * i / 4f
                drawLine(DetailLine, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
            }
            val path = Path()
            points.forEachIndexed { index, value ->
                val x = w * index / (points.size - 1f)
                val y = h * (1f - value)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(DetailAccent, radius = 4.5f, center = Offset(x, y))
            }
            drawPath(path, DetailAccent, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f, cap = StrokeCap.Round))
        }
    }
}

@Composable
private fun AnimationDetailScreen() {
    var category by remember { mutableStateOf("In") }
    var preset by remember { mutableStateOf("Fade in") }
    var duration by remember { mutableFloatStateOf(0.45f) }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("In", "Out", "Combo", "Loop"), category) { category = it }
        Spacer(Modifier.height(8.dp))
        Text("Animation presets", color = DetailWhite, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            val presets = when (category) {
                "Out" -> listOf("Fade out", "Slide out", "Zoom out", "Spin out", "Bounce out", "Blur out")
                "Combo" -> listOf("Swing", "Bounce", "Rock", "Wobble", "Zoom shake", "Flash")
                "Loop" -> listOf("Pulse", "Flicker", "Pendulum", "Heartbeat", "Float", "Rotate")
                else -> listOf("Fade in", "Slide up", "Zoom in", "Pop up", "Typewriter", "Bounce in")
            }
            presets.forEach { DetailAsset(it, preset == it) { preset = it } }
        }
        Spacer(Modifier.height(6.dp))
        DetailSlider("Duration", duration, { duration = it }, "0.1s", "3.0s")
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DetailChip("Curve", false) { }
            DetailChip("Combo settings", false) { }
            DetailChip("Apply to all", false) { }
        }
    }
}

@Composable
private fun TextDetailScreen(detail: String) {
    var activeTab by remember { mutableStateOf(when (detail) {
        "Fonts" -> "Font"
        "Style" -> "Style"
        "Effects" -> "Effects"
        "Animation" -> "Animation"
        "Tracking" -> "Spacing"
        else -> "Text"
    }) }
    var sample by remember { mutableStateOf("Your text") }
    var size by remember { mutableFloatStateOf(0.5f) }
    var spacing by remember { mutableFloatStateOf(0.5f) }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("Text", "Font", "Style", "Effects", "Animation", "Spacing"), activeTab) { activeTab = it }
        Spacer(Modifier.height(8.dp))
        if (activeTab == "Text") {
            TextField(
                value = sample,
                onValueChange = { sample = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Enter your text") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DetailCard,
                    unfocusedContainerColor = DetailCard,
                    focusedTextColor = DetailWhite,
                    unfocusedTextColor = DetailWhite,
                    cursorColor = DetailAccent
                )
            )
            Spacer(Modifier.height(7.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                DetailChip("＋ Add line", false) { sample += " New line" }
                DetailChip("Align", false) { }
                DetailChip("Case", false) { }
            }
        } else {
            val options = when (activeTab) {
                "Font" -> listOf("Classic", "Bold", "Modern", "Serif", "Handwriting", "Typewriter", "Display", "Rounded")
                "Style" -> listOf("Fill", "Stroke", "Shadow", "Background", "Glow", "3D", "Gradient", "Curve")
                "Effects" -> listOf("Neon", "Glitch", "Retro", "Chrome", "Fire", "Outline", "Blur", "Type")
                "Animation" -> listOf("In", "Out", "Loop", "Combo", "Typewriter", "Bounce", "Fade", "Slide")
                else -> listOf("Letter spacing", "Line spacing", "Paragraph", "Indent", "Alignment")
            }
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                options.forEachIndexed { index, item -> DetailAsset(item, index == 0) { } }
            }
        }
        Spacer(Modifier.height(6.dp))
        DetailSlider("Text size", size, { size = it })
        DetailSlider("Spacing", spacing, { spacing = it })
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf(Color.White, DetailAccent, Color(0xFFFFC45C), Color(0xFFFF658A), Color(0xFF8B79FF), Color(0xFF62B9FF), Color.Black).forEach { swatch ->
                Box(Modifier.size(23.dp).clip(CircleShape).background(swatch).border(1.dp, DetailLine, CircleShape).clickable { })
            }
            DetailChip("＋ Custom", false) { }
        }
    }
}

@Composable
private fun AudioDetailScreen(detail: String) {
    var tab by remember { mutableStateOf(when (detail) {
        "Sounds" -> "Sounds"
        "Sound effects" -> "Effects"
        "Voiceover" -> "Record"
        "Text to speech" -> "Text to speech"
        else -> "Sounds"
    }) }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var fadeIn by remember { mutableFloatStateOf(0.1f) }
    var fadeOut by remember { mutableFloatStateOf(0.2f) }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("Sounds", "Effects", "Extract", "Record", "Text to speech"), tab) { tab = it }
        Spacer(Modifier.height(7.dp))
        if (tab == "Sounds" || tab == "Effects") {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("For you", "Trending", "Vlog", "Beat", "Cinematic", "Mood", "Saved").forEachIndexed { i, item -> DetailChip(item, i == 0) { } }
            }
            Spacer(Modifier.height(6.dp))
            repeat(2) { index -> AudioTrackRow(if (tab == "Sounds") "Creator track " + (index + 1) else "Sound effect " + (index + 1), "00:18  •  " + if (index == 0) "Upbeat" else "Ambient") }
        } else if (tab == "Extract") {
            DetailInfo("Extract audio from video", "Select a source clip to create an audio track")
            DetailChip("Choose video", true) { }
        } else if (tab == "Record") {
            Box(Modifier.fillMaxWidth().height(44.dp).clipRoundedBackground(DetailCard), contentAlignment = Alignment.Center) {
                Text("●   Tap to start voice recording", color = DetailWhite, fontSize = 12.sp)
            }
        } else {
            DetailInfo("Text to speech", "Enter a line, choose a voice, then preview")
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("Male", "Female", "Narrator", "Friendly", "Story").forEachIndexed { i, item -> DetailChip(item, i == 0) { } }
            }
        }
        Spacer(Modifier.height(6.dp))
        DetailSlider("Volume", volume, { volume = it }, "0%", "200%")
        DetailSlider("Fade in", fadeIn, { fadeIn = it }, "0s", "5s")
        DetailSlider("Fade out", fadeOut, { fadeOut = it }, "0s", "5s")
    }
}

@Composable
private fun AdjustDetailScreen() {
    var tab by remember { mutableStateOf("Basic") }
    var brightness by remember { mutableFloatStateOf(0.5f) }
    var contrast by remember { mutableFloatStateOf(0.5f) }
    var saturation by remember { mutableFloatStateOf(0.5f) }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("Basic", "HSL", "Curves", "Sharpen", "Highlights"), tab) { tab = it }
        Spacer(Modifier.height(8.dp))
        if (tab == "HSL") {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Purple").forEachIndexed { i, name -> DetailChip(name, i == 0) { } }
            }
            DetailSlider("Hue", brightness, { brightness = it }, "-100", "+100")
            DetailSlider("Saturation", saturation, { saturation = it }, "-100", "+100")
            DetailSlider("Luminance", contrast, { contrast = it }, "-100", "+100")
        } else if (tab == "Curves") {
            SpeedCurveGraph("Custom")
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("RGB", "Red", "Green", "Blue").forEachIndexed { i, item -> DetailChip(item, i == 0) { } }
            }
        } else {
            DetailSlider("Brightness", brightness, { brightness = it }, "-100", "+100")
            DetailSlider("Contrast", contrast, { contrast = it }, "-100", "+100")
            DetailSlider("Saturation", saturation, { saturation = it }, "-100", "+100")
            DetailSlider("Temperature", 0.5f, { }, "Cool", "Warm")
            DetailSlider("Tint", 0.5f, { }, "Green", "Magenta")
            DetailSlider("Sharpen", 0.2f, { }, "0", "100")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DetailChip("Reset", false) { }
            DetailChip("Apply to all", false) { }
        }
    }
}

@Composable
private fun TransitionDetailScreen() {
    var category by remember { mutableStateOf("Basic") }
    var duration by remember { mutableFloatStateOf(0.35f) }
    var selected by remember { mutableStateOf("Mix") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("Basic", "Camera", "MG", "Blur", "Slide", "Mask", "Split", "Light", "3D"), category) { category = it }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            val names = when (category) {
                "Camera" -> listOf("Zoom in", "Zoom out", "Pan", "Shake", "Rotate")
                "Blur" -> listOf("Blur", "Motion blur", "Spin blur", "Directional")
                "Slide" -> listOf("Slide left", "Slide right", "Slide up", "Slide down")
                "Mask" -> listOf("Circle", "Wipe", "Stripe", "Diagonal")
                else -> listOf("Mix", "Fade", "Pull in", "Pull out", "Flash", "Push")
            }
            names.forEach { DetailAsset(it, selected == it) { selected = it } }
        }
        Spacer(Modifier.height(7.dp))
        DetailSlider("Duration", duration, { duration = it }, "0.1s", "3.0s")
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DetailChip("Apply to all", false) { }
            DetailChip("Reverse", false) { }
            DetailChip("Smooth", false) { }
        }
    }
}

@Composable
private fun CaptionsDetailScreen(detail: String) {
    var mode by remember { mutableStateOf("Auto") }
    var style by remember { mutableStateOf("Classic") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailTabs(listOf("Auto", "Manual", "Bilingual", "Templates"), mode) { mode = it }
        Spacer(Modifier.height(7.dp))
        if (mode == "Auto") {
            DetailInfo("Recognize speech", "Generate editable captions from your video audio")
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                DetailChip("Original sound", true) { }
                DetailChip("Language ▾", false) { }
            }
            Spacer(Modifier.height(7.dp))
            DetailChip("Generate captions", true) { }
        } else if (mode == "Manual") {
            DetailInfo("Add caption segment", "Create subtitle text and set its start and end time")
            DetailChip("＋ Add subtitle", true) { }
        } else if (mode == "Bilingual") {
            DetailInfo("Two-language captions", "Choose a source language and translation language")
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                DetailChip("Source language ▾", false) { }
                DetailChip("Translate to ▾", false) { }
            }
        } else {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                listOf("Classic", "Karaoke", "Bold pop", "Highlight", "Minimal", "Typewriter").forEach { DetailAsset(it, style == it) { style = it } }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text("Caption preview", color = DetailMuted, fontSize = 10.sp)
        CaptionPreviewRow("00:01.20", "This is a sample subtitle")
        CaptionPreviewRow("00:03.10", "Edit each caption segment")
        CaptionPreviewRow("00:05.40", "Choose your caption style")
    }
}

@Composable
private fun AdvancedDetailScreen(detail: String) {
    var amount by remember { mutableFloatStateOf(0.55f) }
    var selected by remember { mutableStateOf("Rectangle") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        when (detail) {
            "Mask" -> {
                DetailTabs(listOf("Shape", "Split", "Circle", "Mirror", "Filmstrip"), selected) { selected = it }
                Spacer(Modifier.height(7.dp))
                DetailSlider("Feather", amount, { amount = it })
                DetailSlider("Rotation", 0.5f, { }, "-180°", "180°")
                DetailSlider("Position", 0.5f, { })
                DetailSlider("Size", 0.75f, { })
            }
            "Chroma key" -> {
                DetailInfo("Remove a color", "Choose a color in the preview, then refine the key")
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    DetailChip("Pick color", true) { }
                    DetailChip("Green", false) { }
                    DetailChip("Blue", false) { }
                }
                DetailSlider("Intensity", amount, { amount = it })
                DetailSlider("Shadow", 0.2f, { })
                DetailSlider("Edge feather", 0.25f, { })
            }
            "Keyframe" -> {
                DetailInfo("Animate properties", "Add keyframes at different points in the clip")
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    listOf("Position", "Scale", "Rotation", "Opacity").forEachIndexed { i, item -> DetailChip(item, i == 0) { } }
                }
                Spacer(Modifier.height(7.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) {
                    DetailChip("◇ Add keyframe", true) { }
                    DetailChip("◆ Previous", false) { }
                    DetailChip("◆ Next", false) { }
                }
            }
            "Stabilize" -> {
                DetailInfo("Stabilize video", "Reduce unwanted camera movement")
                DetailTabs(listOf("Minimum crop", "Recommended", "Most stable"), "Recommended") { }
                DetailSlider("Stabilization", amount, { amount = it })
            }
            else -> {
                DetailInfo(detail, "Adjust the selected tool before applying it to the clip")
                DetailSlider("Strength", amount, { amount = it })
                DetailSlider("Edge", 0.35f, { })
                DetailSlider("Feather", 0.2f, { })
            }
        }
    }
}

@Composable
private fun AssetDetailScreen(tool: String, detail: String) {
    var category by remember { mutableStateOf("For you") }
    var selected by remember { mutableStateOf("01") }
    var strength by remember { mutableFloatStateOf(0.7f) }
    val categories = if (tool == "Filters") {
        listOf("For you", "Portrait", "Movie", "Retro", "Food", "Night", "Mono")
    } else {
        listOf("For you", "Trending", "Pro", "Basic", "Lens", "Retro", "Distortion", "Body")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            categories.forEach { DetailChip(it, category == it) { category = it } }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            (1..6).forEach { index ->
                val value = index.toString().padStart(2, '0')
                DetailAsset(value, selected == value) { selected = value }
            }
        }
        Spacer(Modifier.height(7.dp))
        DetailSlider("Intensity", strength, { strength = it })
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DetailChip("Favorite ☆", false) { }
            DetailChip("Adjust", false) { }
            DetailChip("Apply to all", false) { }
        }
    }
}

@Composable
private fun FormatDetailScreen() {
    var ratio by remember { mutableStateOf("9:16") }
    var background by remember { mutableStateOf("Blur") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("Aspect ratio", color = DetailWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Original", "9:16", "16:9", "1:1", "4:5", "3:4", "2:1").forEach { DetailChip(it, ratio == it) { ratio = it } }
        }
        Spacer(Modifier.height(9.dp))
        Text("Canvas background", color = DetailWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        DetailTabs(listOf("Blur", "Color", "Image"), background) { background = it }
        Spacer(Modifier.height(7.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Color.Black, Color.White, DetailAccent, Color(0xFF805CFF), Color(0xFFFF9E5C), Color(0xFF314A78)).forEach {
                Box(Modifier.size(24.dp).clip(CircleShape).background(it).border(1.dp, DetailLine, CircleShape).clickable { background = "Color" })
            }
        }
    }
}

@Composable
private fun GenericDetailScreen(tool: String, detail: String) {
    var amount by remember { mutableFloatStateOf(0.55f) }
    var selected by remember { mutableStateOf("Default") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        DetailInfo(detail, tool + " controls and presets")
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Default", "Basic", "Popular", "Trending", "Pro", "Custom").forEach { DetailChip(it, selected == it) { selected = it } }
        }
        Spacer(Modifier.height(7.dp))
        DetailSlider("Strength", amount, { amount = it })
        DetailSlider("Duration", 0.5f, { })
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            DetailChip("Reset", false) { amount = 0.55f }
            DetailChip("Apply to all", false) { }
            DetailChip("✓ Apply", true) { }
        }
    }
}

@Composable
private fun DetailTabs(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        items.forEach { DetailChip(it, selected == it) { onSelect(it) } }
    }
}

@Composable
private fun DetailChip(label: String, selected: Boolean, onClick: () -> Unit = {}) {
    Box(
        Modifier.clipRoundedBackground(if (selected) Color(0xFF17453F) else DetailCard)
            .border(if (selected) 1.dp else 0.dp, if (selected) DetailAccent.copy(alpha = 0.65f) else Color.Transparent, RoundedCornerShape(9.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = if (selected) DetailAccent else DetailWhite, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun DetailAsset(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.width(62.dp).clip(RoundedCornerShape(9.dp))
            .background(if (selected) Color(0xFF17453F) else DetailCard)
            .border(if (selected) 1.dp else 0.dp, if (selected) DetailAccent else Color.Transparent, RoundedCornerShape(9.dp))
            .clickable { onClick() }.padding(7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().height(34.dp).clip(RoundedCornerShape(6.dp)).background(
            androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF385C67), Color(0xFF754E70), Color(0xFF26394D)))
        ), contentAlignment = Alignment.Center) {
            Text(if (label.length <= 3) "Aa" else "✦", color = DetailWhite, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = if (selected) DetailAccent else DetailWhite, fontSize = 8.sp, textAlign = TextAlign.Center, maxLines = 1)
    }
}

@Composable
private fun DetailSlider(label: String, value: Float, onChange: (Float) -> Unit, left: String = "0", right: String = "100") {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = DetailWhite, fontSize = 9.sp, modifier = Modifier.width(64.dp))
        Column(Modifier.weight(1f)) {
            Slider(value = value.coerceIn(0f, 1f), onValueChange = onChange)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(left, color = DetailMuted, fontSize = 8.sp)
                Text(right, color = DetailMuted, fontSize = 8.sp)
            }
        }
    }
}

@Composable
private fun DetailInfo(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().clipRoundedBackground(DetailCard).padding(10.dp)) {
        Text(title, color = DetailWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = DetailMuted, fontSize = 9.sp)
    }
}

@Composable
private fun AudioTrackRow(title: String, subtitle: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(8.dp)).background(DetailCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(29.dp).clip(RoundedCornerShape(7.dp)).background(Color(0xFF1C6358)), contentAlignment = Alignment.Center) {
            Text("♫", color = DetailAccent, fontSize = 15.sp)
        }
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = DetailWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = DetailMuted, fontSize = 8.sp)
        }
        DetailChip("＋", false)
    }
}

@Composable
private fun CaptionPreviewRow(time: String, text: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(7.dp)).background(DetailCard).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(time, color = DetailAccent, fontSize = 8.sp, modifier = Modifier.width(54.dp))
        Text(text, color = DetailWhite, fontSize = 9.sp, modifier = Modifier.weight(1f))
        Text("✎", color = DetailMuted, fontSize = 12.sp)
    }
}

private fun Modifier.clipRoundedBackground(color: Color): Modifier =
    this.background(color, RoundedCornerShape(10.dp))
