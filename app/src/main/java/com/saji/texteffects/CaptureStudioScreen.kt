package com.saji.texteffects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CaptureStudioScreen(
    onBack: () -> Unit,
    onImport: () -> Unit,
    onCapture: (String) -> Unit
) {
    var mode by remember { mutableStateOf("Video") }
    Column(Modifier.fillMaxSize().background(CaptureBg).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("×", color = CaptureWhite, fontSize = 29.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Capture studio", color = CaptureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Use your device camera to capture media", color = CaptureMuted, fontSize = 10.sp)
            }
            CapturePill("DEVICE CAMERA", CaptureAccent) { }
        }
        Spacer(Modifier.height(18.dp))
        Box(
            Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(22.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF183B46), Color(0xFF332B45), Color(0xFF121A27)))),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(20.dp)) {
                Text("SAJI CAMERA", color = CaptureWhite.copy(alpha = .75f), fontSize = 10.sp, letterSpacing = 4.sp)
                Spacer(Modifier.height(12.dp))
                Text(if (mode == "Video") "VIDEO CAPTURE" else "PHOTO CAPTURE", color = CaptureWhite, fontSize = 23.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Tap the capture button to open your phone's camera app. Captured media will be added to this project.",
                    color = CaptureWhite.copy(alpha = .72f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(18.dp))
                CapturePill(if (mode == "Video") "MP4 VIDEO" else "PHOTO", CaptureAccent) { }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            listOf("Photo", "Video").forEach { item ->
                CapturePill(item, if (mode == item) CaptureAccent else CaptureMuted) { mode = item }
                Spacer(Modifier.width(8.dp))
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onImport() }.padding(8.dp)) {
                Text("▧", color = CaptureWhite, fontSize = 24.sp)
                Text("Gallery", color = CaptureMuted, fontSize = 10.sp)
            }
            Box(
                Modifier.size(76.dp).clip(CircleShape).border(4.dp, CaptureWhite, CircleShape)
                    .padding(6.dp).clip(CircleShape).background(CaptureAccent)
                    .clickable { onCapture(mode) },
                contentAlignment = Alignment.Center
            ) {
                Text(if (mode == "Photo") "●" else "▶", color = CaptureBg, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { mode = if (mode == "Video") "Photo" else "Video" }.padding(8.dp)) {
                Text("⇄", color = CaptureWhite, fontSize = 24.sp)
                Text("Switch", color = CaptureMuted, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "Capture opens the installed camera app. Flash, lens and camera-specific settings are controlled there.",
            color = CaptureMuted,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun CapturePill(label: String, tint: Color, onClick: () -> Unit) {
    Box(Modifier.clip(RoundedCornerShape(20.dp)).background(CapturePanel).clickable { onClick() }.padding(horizontal = 11.dp, vertical = 8.dp)) {
        Text(label, color = tint, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
