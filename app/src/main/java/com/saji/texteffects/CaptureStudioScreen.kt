package com.saji.texteffects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CaptureBg = Color(0xFF08090D)
private val CapturePanel = Color(0xFF17191F)
private val CaptureMuted = Color(0xFF9299A6)
private val CaptureWhite = Color(0xFFF6F7FA)
private val CaptureAccent = Color(0xFF25E0C0)

@Composable
fun CaptureStudioScreen(onBack: () -> Unit, onImport: () -> Unit) {
    var mode by remember { mutableStateOf("Video") }
    var ratio by remember { mutableStateOf("9:16") }
    var filter by remember { mutableStateOf("Original") }
    var flash by remember { mutableStateOf("Off") }
    var timer by remember { mutableStateOf("Off") }
    var zoom by remember { mutableFloatStateOf(1f) }
    var recording by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(CaptureBg).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("×", color = CaptureWhite, fontSize = 29.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Capture studio", color = CaptureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Camera workspace preview", color = CaptureMuted, fontSize = 10.sp)
            }
            CapturePill(flash, CaptureAccent) { flash = if (flash == "Off") "Auto" else if (flash == "Auto") "On" else "Off" }
            Spacer(Modifier.width(6.dp))
            CapturePill("Timer $timer", CaptureMuted) { timer = if (timer == "Off") "3s" else if (timer == "3s") "10s" else "Off" }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Color(0xFF183B46), Color(0xFF332B45), Color(0xFF121A27))))) {
            Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SAJI CAMERA", color = CaptureWhite.copy(alpha = .75f), fontSize = 10.sp, letterSpacing = 4.sp)
                Spacer(Modifier.height(10.dp))
                Text("LIVE PREVIEW", color = CaptureWhite, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("Camera preview will appear here", color = CaptureWhite.copy(alpha = .65f), fontSize = 10.sp)
            }
            CapturePill(ratio, CaptureWhite, Modifier.align(Alignment.TopEnd).padding(12.dp)) {
                ratio = when (ratio) { "9:16" -> "16:9"; "16:9" -> "1:1"; else -> "9:16" }
            }
            Text(if (recording) "● REC  00:00" else "● READY", color = if (recording) Color(0xFFFF6878) else CaptureWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.BottomStart).padding(14.dp))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            listOf("0.5×", "1×", "2×", "3×").forEach { label ->
                CapturePill(label, if (zoom == label.substringBefore("×").toFloat()) CaptureAccent else CaptureMuted) {
                    zoom = label.substringBefore("×").toFloat()
                }
                Spacer(Modifier.width(7.dp))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("Filters", color = CaptureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Original", "Cinematic", "Warm", "Cool", "Vintage", "B&W", "Vivid").forEach {
                CapturePill(it, if (filter == it) CaptureAccent else CaptureMuted) { filter = it }
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onImport() }) {
                Text("▧", color = CaptureWhite, fontSize = 22.sp)
                Text("Gallery", color = CaptureMuted, fontSize = 9.sp)
            }
            Box(Modifier.size(72.dp).clip(CircleShape).border(4.dp, CaptureWhite, CircleShape).padding(6.dp).clip(CircleShape).background(if (recording) Color(0xFFFF5267) else CaptureAccent).clickable { recording = !recording }, contentAlignment = Alignment.Center) {
                Text(if (recording) "■" else if (mode == "Photo") "●" else "▶", color = CaptureBg, fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { mode = if (mode == "Video") "Photo" else "Video" }) {
                Text("⇄", color = CaptureWhite, fontSize = 22.sp)
                Text("Switch", color = CaptureMuted, fontSize = 9.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            listOf("Photo", "Video").forEach {
                CapturePill(it, if (mode == it) CaptureAccent else CaptureMuted) { mode = it; recording = false }
                Spacer(Modifier.width(8.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Note: Camera capture, flash and recording controls are UI preview only in this phase.", color = CaptureMuted, fontSize = 9.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun CapturePill(label: String, tint: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).background(CapturePanel).clickable { onClick() }.padding(horizontal = 11.dp, vertical = 8.dp)) {
        Text(label, color = tint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
