package com.saji.texteffects

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF08090D)
private val Panel = Color(0xFF17191F)
private val Panel2 = Color(0xFF22252D)
private val Line = Color(0xFF30343E)
private val Accent = Color(0xFF25E0C0)
private val AccentBlue = Color(0xFF53B8FF)
private val Muted = Color(0xFF9299A6)
private val White = Color(0xFFF6F7FA)

private val editorTools = listOf(
    "Edit", "Audio", "Text", "Stickers", "Overlay", "Effects",
    "Filters", "Captions", "Format", "Canvas", "More"
)

private val toolOptions = mapOf(
    "Edit" to listOf("Split", "Speed", "Animation", "Style", "Crop", "Rotate", "Mirror", "Freeze", "Stabilize", "Retouch", "Mask", "Tracking", "Cutout", "Adjust", "Opacity", "Volume"),
    "Clip" to listOf("Replace", "Split", "Speed", "Volume", "Animation", "Adjust", "Mask", "Opacity", "Copy", "Reverse", "Freeze", "Delete"),
    "Audio" to listOf("Sounds", "Sound effects", "Extract audio", "Voiceover", "Text to speech", "Beats", "Volume", "Fade", "Voice effects", "Noise reduction"),
    "Text" to listOf("Add text", "Text templates", "Auto captions", "Fonts", "Style", "Effects", "Animation", "Bubbles", "Text to speech", "Tracking"),
    "Stickers" to listOf("Trending", "Emoji", "Decorative", "Labels", "Social", "My stickers", "AI stickers"),
    "Overlay" to listOf("Add overlay", "Mask", "Blend", "Opacity", "Animation", "Tracking", "Remove background"),
    "Effects" to listOf("Video effects", "Body effects", "AI effects", "Trending", "Glitch", "Blur", "Light", "Retro", "Lens", "Distortion"),
    "Filters" to listOf("Trending", "Portrait", "Movie", "Retro", "Food", "Night", "Landscape", "Mono", "Adjust"),
    "Captions" to listOf("Auto captions", "Manual captions", "Bilingual captions", "Caption templates", "Edit captions", "Text to speech"),
    "Format" to listOf("9:16", "16:9", "1:1", "4:5", "3:4", "Fit", "Fill", "Background"),
    "Canvas" to listOf("Ratio", "Background", "Blur", "Color", "Border", "Round corners"),
    "More" to listOf("Chroma key", "Remove background", "Stabilize", "Motion blur", "Enhance", "Retouch", "Relight", "Noise reduction", "Flicker removal", "Camera tracking")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(8, 9, 13)
        window.navigationBarColor = android.graphics.Color.rgb(8, 9, 13)
        setContent { CapCutTwoApp() }
    }
}

@Composable
private fun CapCutTwoApp() {
    var page by remember { mutableStateOf("home") }
    var pickerReturnTo by remember { mutableStateOf("editor") }
    var selectedExportNotice by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Bg,
        surface = Panel,
        primary = Accent,
        onPrimary = Bg,
        onBackground = White,
        onSurface = White
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            when (page) {
                "home" -> HomeScreen(
                    onNewProject = { pickerReturnTo = "editor"; page = "picker" },
                    onOpenProject = { page = "editor" },
                    onNavigate = { page = it }
                )
                "picker" -> MediaPickerScreen(
                    onBack = { page = if (pickerReturnTo == "editor") "home" else pickerReturnTo },
                    onAdd = { page = "editor" }
                )
                "editor" -> EditorScreen(
                    onBack = { page = "home" },
                    onExport = { selectedExportNotice = false; page = "export" },
                    onAddMedia = { pickerReturnTo = "editor"; page = "picker" }
                )
                "templates" -> TemplatesScreen(
                    onBack = { page = "home" },
                    onUseTemplate = { pickerReturnTo = "editor"; page = "picker" },
                    onNavigate = { page = it }
                )
                "library" -> LibraryScreen(onBack = { page = "home" }, onNavigate = { page = it })
                "profile" -> ProfileScreen(onBack = { page = "home" }, onNavigate = { page = it })
                "export" -> ExportScreen(
                    noticeShown = selectedExportNotice,
                    onBack = { page = "editor" },
                    onConfirm = { selectedExportNotice = true }
                )
                "settings" -> CapCutSettingsScreen(onBack = { page = "profile" })
                else -> HomeScreen(
                    onNewProject = { pickerReturnTo = "editor"; page = "picker" },
                    onOpenProject = { page = "editor" },
                    onNavigate = { page = it }
                )
            }
        }
    }
}

@Composable
private fun HomeScreen(
    onNewProject: () -> Unit,
    onOpenProject: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("SAJI", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                    Text("CapCut 2", color = White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                }
                SmallPill("PRO UI", Accent)
                Spacer(Modifier.width(8.dp))
                CircleAction("⚙", onClick = { onNavigate("profile") })
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeQuickCard("＋", "New project", "Edit videos", true, Modifier.weight(1.5f), onNewProject)
                HomeQuickCard("✂", "AutoCut", "Quick edit", false, Modifier.weight(1f), onNewProject)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeSmallCard("▧", "Photo editor", Modifier.weight(1f)) { onNewProject() }
                HomeSmallCard("◉", "Camera", Modifier.weight(1f)) { onNewProject() }
                HomeSmallCard("▤", "Teleprompter", Modifier.weight(1f)) { onNewProject() }
            }
            Spacer(Modifier.height(26.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recent projects", color = White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("See all  ›", color = Accent, fontSize = 12.sp, modifier = Modifier.clickable { onOpenProject() })
            }
            Spacer(Modifier.height(12.dp))
            ProjectRow("Untitled project", "Today  •  00:18", "9:16", listOf(Color(0xFF243F50), Color(0xFF70517C)), onOpenProject)
            ProjectRow("Travel montage", "Yesterday  •  00:32", "16:9", listOf(Color(0xFFB86A4D), Color(0xFF263C52)), onOpenProject)
            ProjectRow("Text animation", "Nov 16  •  00:12", "9:16", listOf(Color(0xFF302D65), Color(0xFF1C6D78)), onOpenProject)
            ProjectRow("My edit 04", "Nov 14  •  00:45", "1:1", listOf(Color(0xFF69503C), Color(0xFF24343D)), onOpenProject)
            Spacer(Modifier.height(24.dp))
            SectionHeading("Explore tools", "Create more")
            Spacer(Modifier.height(10.dp))
            ToolExploreCard("Auto captions", "Speech to stylish subtitles", "CC") { onNewProject() }
            ToolExploreCard("Text to speech", "Turn your words into voice", "TTS") { onNewProject() }
            ToolExploreCard("Background remover", "Cut out your subject", "BG") { onNewProject() }
            Spacer(Modifier.height(26.dp))
        }
        BottomNav("Edit", onNavigate)
    }
}

@Composable
private fun TemplatesScreen(onBack: () -> Unit, onUseTemplate: () -> Unit, onNavigate: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Templates", color = White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                CircleAction("⌕") { }
            }
            Spacer(Modifier.height(14.dp))
            FakeSearch("Search templates, trends and creators")
            Spacer(Modifier.height(14.dp))
            HorizontalChips(listOf("For you", "Trending", "Beat sync", "Vlog", "Business", "Slow mo", "Photo dump"))
            Spacer(Modifier.height(18.dp))
            SectionHeading("Trending now", "See all")
            Spacer(Modifier.height(10.dp))
            TemplateGrid(onUseTemplate)
            Spacer(Modifier.height(20.dp))
            SectionHeading("Popular categories", "Explore")
            Spacer(Modifier.height(10.dp))
            listOf("Travel & vlog", "Beat sync", "Lifestyle", "Gaming", "Fashion", "Birthday", "Cinematic", "Reels").chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { name -> SurfaceCard(name, "Browse templates", Modifier.weight(1f), onClick = onUseTemplate) }
                }
                Spacer(Modifier.height(9.dp))
            }
            Spacer(Modifier.height(20.dp))
        }
        BottomNav("Templates", onNavigate)
    }
}

@Composable
private fun LibraryScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    var category by remember { mutableStateOf("Sounds") }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your library", fontSize = 26.sp, color = White, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                CircleAction("＋") { }
            }
            Spacer(Modifier.height(14.dp))
            HorizontalChips(listOf("Sounds", "Effects", "Fonts", "Stickers", "Brand kit"), category) { category = it }
            Spacer(Modifier.height(20.dp))
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel).padding(22.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(if (category == "Sounds") "♫" else "✦", color = Accent, fontSize = 36.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("Your " + category.lowercase() + " will appear here", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Save items while editing and find them here later.", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
                }
            }
            Spacer(Modifier.height(22.dp))
            SectionHeading("Recommended", "Browse")
            Spacer(Modifier.height(10.dp))
            ToolExploreCard("Music collection", "Browse tracks by mood and genre", "♫") { category = "Sounds" }
            ToolExploreCard("Text styles", "Keep your favorite fonts together", "Aa") { category = "Fonts" }
            ToolExploreCard("My effects", "Saved video and body effects", "✧") { category = "Effects" }
            Spacer(Modifier.height(24.dp))
        }
        BottomNav("Library", onNavigate)
    }
}

@Composable
private fun ProfileScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(20.dp))
            Text("Me", color = White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(Panel).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(54.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Accent, AccentBlue))), contentAlignment = Alignment.Center) {
                    Text("S", color = Bg, fontWeight = FontWeight.Black, fontSize = 24.sp)
                }
                Spacer(Modifier.width(13.dp))
                Column(Modifier.weight(1f)) {
                    Text("SAJI Creator", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Local workspace", color = Muted, fontSize = 11.sp)
                }
                SmallPill("FREE", AccentBlue)
            }
            Spacer(Modifier.height(20.dp))
            SectionHeading("Workspace", "")
            ProfileRow("☁", "Cloud space", "Sync projects and assets") { }
            ProfileRow("▣", "My drafts", "Saved editing projects") { onNavigate("home") }
            ProfileRow("⬇", "Downloads", "Fonts, effects and media") { }
            Spacer(Modifier.height(18.dp))
            SectionHeading("Preferences", "")
            ProfileRow("⚙", "Settings", "App language, appearance and cache") { onNavigate("settings") }
            ProfileRow("⌕", "Privacy and permissions", "Media and app permissions") { onNavigate("settings") }
            ProfileRow("?", "Help center", "Tips, guides and support") { onNavigate("settings") }
            ProfileRow("☆", "About SAJI CapCut 2", "Version and acknowledgements") { onNavigate("settings") }
            Spacer(Modifier.height(20.dp))
        }
        BottomNav("Me", onNavigate)
    }
}

@Composable
private fun MediaPickerScreen(onBack: () -> Unit, onAdd: () -> Unit) {
    var tab by remember { mutableStateOf("Videos") }
    var album by remember { mutableStateOf("All media") }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("×", color = White, fontSize = 28.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(18.dp))
            Text("Add media", color = White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Next", color = if (selected.isNotEmpty()) Accent else Muted, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { if (selected.isNotEmpty()) onAdd() })
        }
        HorizontalChips(listOf("Videos", "Photos", "Live photos"), tab) { tab = it; selected = emptySet() }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(album + "  ▾", color = White, fontSize = 13.sp, modifier = Modifier.clickable { album = if (album == "All media") "Camera" else "All media" })
            Spacer(Modifier.weight(1f))
            Text("Select multiple  ▦", color = Muted, fontSize = 11.sp)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 10.dp)) {
            for (rowIndex in 0..3) {
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                    for (columnIndex in 0..2) {
                        val index = rowIndex * 3 + columnIndex
                        MediaGridTile(index, tab, selected.contains(index), Modifier.weight(1f)) {
                            selected = if (selected.contains(index)) selected - index else selected + index
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Showing sample media layout. Device gallery connection will be enabled in the import phase.", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(12.dp))
        }
        Row(Modifier.fillMaxWidth().background(Panel).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(selected.size.toString() + " selected", color = White, fontSize = 12.sp, modifier = Modifier.weight(1f))
            PrimaryAction("Add to project", selected.isNotEmpty()) { onAdd() }
        }
    }
}

@Composable
private fun EditorScreen(onBack: () -> Unit, onExport: () -> Unit, onAddMedia: () -> Unit) {
    var activeTool by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var clipSelected by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().height(52.dp).padding(horizontal = 13.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 30.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Untitled project", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Saved locally  •  9:16", color = Muted, fontSize = 10.sp)
            }
            SmallPill("1080P ▾", Muted)
            Spacer(Modifier.width(8.dp))
            PrimaryAction("Export", true, onExport)
        }
        Box(Modifier.weight(1.15f).fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp), contentAlignment = Alignment.Center) {
            PreviewStage(isPlaying)
        }
        Row(Modifier.fillMaxWidth().height(34.dp).padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("00:00 / 00:18", color = Muted, fontSize = 10.sp, modifier = Modifier.weight(1f))
            Text("⛶", color = White, fontSize = 17.sp, modifier = Modifier.clickable { activeTool = "Format" }.padding(horizontal = 10.dp))
            Text(if (isPlaying) "Ⅱ" else "▶", color = White, fontSize = 20.sp, modifier = Modifier.clickable { isPlaying = !isPlaying }.padding(horizontal = 12.dp))
            Text("↶", color = Muted, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 7.dp))
            Text("↷", color = Muted, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 7.dp))
        }
        TimelinePanel(
            modifier = Modifier.weight(0.82f).fillMaxWidth(),
            clipSelected = clipSelected,
            onClipClick = { clipSelected = true; activeTool = "Clip" },
            onAddAudio = { activeTool = "Audio" },
            onAddText = { activeTool = "Text" },
            onAddTrack = onAddMedia
        )
        Row(
            modifier = Modifier.fillMaxWidth().height(72.dp).background(Bg).horizontalScroll(rememberScrollState()).padding(horizontal = 8.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            editorTools.forEach { tool ->
                EditorToolButton(tool, activeTool == tool) {
                    clipSelected = false
                    activeTool = if (activeTool == tool) "" else tool
                }
            }
        }
        if (activeTool.isNotEmpty()) {
            ToolTray(activeTool, onClose = { activeTool = "" }, onAddMedia = onAddMedia)
        }
    }
}

@Composable
private fun ExportScreen(noticeShown: Boolean, onBack: () -> Unit, onConfirm: () -> Unit) {
    var resolution by remember { mutableStateOf("1080p") }
    var frameRate by remember { mutableStateOf("30 fps") }
    var codeRate by remember { mutableFloatStateOf(0.65f) }
    var smartHdr by remember { mutableStateOf(false) }
    var opticalFlow by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 30.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Text("Export settings", color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        Box(Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFF213849), Color(0xFF1E1C34), Color(0xFF422B45)))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SAJI", color = Accent, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 5.sp)
                Text("VIDEO EXPORT", color = White, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
                Text("Final quality preview", color = White.copy(alpha = 0.65f), fontSize = 11.sp)
            }
        }
        Spacer(Modifier.height(22.dp))
        SettingHeading("Resolution", "Choose output size")
        ChoiceRow(listOf("480p", "720p", "1080p", "2K", "4K"), resolution) { resolution = it }
        Spacer(Modifier.height(20.dp))
        SettingHeading("Frame rate", "Higher FPS can create smoother motion")
        ChoiceRow(listOf("24 fps", "25 fps", "30 fps", "50 fps", "60 fps"), frameRate) { frameRate = it }
        Spacer(Modifier.height(20.dp))
        SettingHeading("Code rate", "Low                                     Recommended")
        Slider(value = codeRate, onValueChange = { codeRate = it })
        Text(if (codeRate > 0.8f) "High quality" else if (codeRate > 0.4f) "Recommended" else "Smaller file", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(15.dp))
        ToggleRow("Smart HDR", "Expanded color and contrast", smartHdr) { smartHdr = it }
        ToggleRow("Optical flow", "Frame interpolation for slow motion", opticalFlow) { opticalFlow = it }
        Spacer(Modifier.height(18.dp))
        Text("Estimated file size: calculated after export engine is connected", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(18.dp))
        PrimaryAction("Export video", true, onConfirm, Modifier.fillMaxWidth())
        if (noticeShown) {
            Spacer(Modifier.height(12.dp))
            Text("Export screen is ready. Video rendering will be connected in a later phase.", color = Accent, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PreviewStage(isPlaying: Boolean) {
    Box(Modifier.fillMaxHeight().aspectRatio(9f / 16f).clip(RoundedCornerShape(13.dp)).background(Brush.linearGradient(listOf(Color(0xFF153746), Color(0xFF28233F), Color(0xFF713F50)))), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SAJI FILMS", color = Color.White.copy(alpha = 0.58f), fontSize = 9.sp, letterSpacing = 4.sp)
            Spacer(Modifier.height(22.dp))
            Text("YOUR", color = White, fontWeight = FontWeight.Black, fontSize = 29.sp, letterSpacing = 4.sp)
            Text("STORY", color = Accent, fontWeight = FontWeight.Black, fontSize = 37.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(18.dp))
            Text("EDIT • CREATE • SHARE", color = White.copy(alpha = 0.72f), fontSize = 8.sp, letterSpacing = 2.sp)
        }
        Text(if (isPlaying) "Ⅱ" else "▶", color = White.copy(alpha = 0.8f), fontSize = 18.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp))
        Text("00:00", color = White.copy(alpha = 0.75f), fontSize = 9.sp, modifier = Modifier.align(Alignment.BottomStart).padding(9.dp))
        Text("9:16", color = White.copy(alpha = 0.75f), fontSize = 9.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(9.dp))
    }
}

@Composable
private fun TimelinePanel(
    modifier: Modifier,
    clipSelected: Boolean,
    onClipClick: () -> Unit,
    onAddAudio: () -> Unit,
    onAddText: () -> Unit,
    onAddTrack: () -> Unit
) {
    Column(modifier.background(Color(0xFF101217)).padding(horizontal = 12.dp, vertical = 7.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("00:00", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            Text("00:06        00:12        00:18", color = Muted, fontSize = 9.sp)
            Spacer(Modifier.width(8.dp))
            Text("＋", color = Accent, fontSize = 19.sp, modifier = Modifier.clickable { onAddTrack() })
        }
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(43.dp)) {
            Row(Modifier.fillMaxSize().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(7) { index ->
                    val clipColors = when (index % 4) {
                        0 -> listOf(Color(0xFF355B68), Color(0xFF76506A))
                        1 -> listOf(Color(0xFF446B70), Color(0xFF674B69))
                        2 -> listOf(Color(0xFF3C5B73), Color(0xFF855A60))
                        else -> listOf(Color(0xFF496B62), Color(0xFF584B73))
                    }
                    Box(
                        Modifier.width(58.dp).fillMaxHeight().clip(RoundedCornerShape(5.dp))
                            .background(Brush.linearGradient(clipColors))
                            .border(if (clipSelected) 1.dp else 0.dp, if (clipSelected) Accent else Color.Transparent, RoundedCornerShape(5.dp))
                            .clickable { onClipClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (index % 2 == 0) "VIDEO" else "CLIP", color = White.copy(alpha = 0.85f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Box(Modifier.align(Alignment.Center).width(2.dp).fillMaxHeight().background(Accent))
        }
        Spacer(Modifier.height(5.dp))
        TrackRow("♫", "Add audio", Color(0xFF276E63), onAddAudio)
        TrackRow("T", "Add text", Color(0xFF574A84), onAddText)
    }
}

@Composable
private fun TrackRow(icon: String, label: String, color: Color, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(27.dp).padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = Muted, fontSize = 12.sp, modifier = Modifier.width(22.dp))
        Box(
            Modifier.fillMaxWidth().height(22.dp).clip(RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.24f)).clickable { onClick() }.padding(horizontal = 9.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(label + "  ＋", color = White.copy(alpha = 0.82f), fontSize = 9.sp)
        }
    }
}

@Composable
private fun EditorToolButton(label: String, selected: Boolean, onClick: () -> Unit) {
    val icon = when (label) {
        "Edit" -> "✂"
        "Audio" -> "♫"
        "Text" -> "T"
        "Stickers" -> "☺"
        "Overlay" -> "▣"
        "Effects" -> "✦"
        "Filters" -> "◐"
        "Captions" -> "CC"
        "Format" -> "▤"
        "Canvas" -> "▧"
        else -> "•••"
    }
    Column(
        modifier = Modifier.width(62.dp).clip(RoundedCornerShape(10.dp))
            .background(if (selected) Panel2 else Color.Transparent).clickable { onClick() }.padding(vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = if (selected) Accent else White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Text(label, color = if (selected) Accent else Muted, fontSize = 9.sp, maxLines = 1)
    }
}

@Composable
private fun ToolTray(tool: String, onClose: () -> Unit, onAddMedia: () -> Unit) {
    var detail by remember(tool) { mutableStateOf("") }
    var intensity by remember(tool) { mutableFloatStateOf(0.55f) }
    var duration by remember(tool) { mutableFloatStateOf(0.5f) }
    val options = toolOptions[tool] ?: toolOptions["More"].orEmpty()
    Column(
        Modifier.fillMaxWidth().height(192.dp).background(Panel).padding(horizontal = 13.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (detail.isNotEmpty()) {
                Text("‹", color = White, fontSize = 24.sp, modifier = Modifier.clickable { detail = "" }.padding(end = 9.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(if (detail.isEmpty()) tool else detail, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(if (detail.isEmpty()) "Choose an option" else "Customize your selection", color = Muted, fontSize = 9.sp)
            }
            Text("×", color = Muted, fontSize = 23.sp, modifier = Modifier.clickable { onClose() })
        }
        Spacer(Modifier.height(9.dp))
        if (detail.isEmpty()) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    TrayOption(option) { if (option == "Add overlay" || option == "Add text") onAddMedia() else detail = option }
                }
            }
            Spacer(Modifier.height(9.dp))
            Text("Swipe to explore all " + tool.lowercase() + " tools", color = Muted, fontSize = 10.sp)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallPill("Reset", Muted)
                SmallPill("Favorites ☆", AccentBlue)
                Spacer(Modifier.weight(1f))
                SmallPill("Apply  ✓", Accent)
            }
        } else {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                val names = when (detail) {
                    "Speed" -> listOf("Normal", "Curve", "Montage", "Hero", "Bullet")
                    "Animation" -> listOf("In", "Out", "Combo", "Loop")
                    "Fonts" -> listOf("Trending", "Basic", "Handwriting", "Serif", "Display")
                    "Effects", "Video effects", "Body effects" -> listOf("Trending", "Pro", "Basic", "Lens", "Retro")
                    "Filters" -> listOf("Portrait", "Movie", "Retro", "Food", "Night")
                    "Format", "Ratio" -> listOf("9:16", "16:9", "1:1", "4:5", "3:4")
                    else -> listOf("Basic", "Popular", "Trending", "Favorites")
                }
                names.forEach { SmallPill(it, if (it == names.first()) Accent else Muted) }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Strength", color = White, fontSize = 10.sp, modifier = Modifier.width(66.dp))
                Slider(value = intensity, onValueChange = { intensity = it }, modifier = Modifier.weight(1f))
                Text((intensity * 100).toInt().toString(), color = Muted, fontSize = 10.sp, modifier = Modifier.width(30.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Duration", color = White, fontSize = 10.sp, modifier = Modifier.width(66.dp))
                Slider(value = duration, onValueChange = { duration = it }, modifier = Modifier.weight(1f))
                Text((duration * 10).toInt().toString() + "s", color = Muted, fontSize = 10.sp, modifier = Modifier.width(30.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                SmallPill("Reset", Muted)
                Spacer(Modifier.weight(1f))
                SmallPill("Apply  ✓", Accent)
            }
        }
    }
}

@Composable
private fun TrayOption(label: String, onClick: () -> Unit) {
    val icon = when (label) {
        "Split" -> "✂"
        "Speed" -> "◴"
        "Animation" -> "⌁"
        "Add text" -> "T"
        "Sounds" -> "♫"
        "Extract audio" -> "⇣"
        "Auto captions" -> "CC"
        "Crop" -> "□"
        "Rotate" -> "⟳"
        "Mask" -> "◒"
        "Chroma key" -> "◉"
        else -> "✦"
    }
    Column(
        Modifier.width(66.dp).clip(RoundedCornerShape(10.dp)).background(Panel2)
            .clickable { onClick() }.padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, color = Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(label, color = White, fontSize = 9.sp, maxLines = 2, textAlign = TextAlign.Center, lineHeight = 11.sp)
    }
}

@Composable
private fun HomeQuickCard(
    icon: String, title: String, subtitle: String, primary: Boolean, modifier: Modifier, onClick: () -> Unit
) {
    Column(
        modifier.clip(RoundedCornerShape(18.dp))
            .background(if (primary) Brush.linearGradient(listOf(Color(0xFF1D907F), Color(0xFF1B6175))) else Brush.linearGradient(listOf(Panel2, Panel)))
            .clickable { onClick() }.padding(15.dp)
    ) {
        Text(icon, color = if (primary) White else Accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text(title, color = White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = White.copy(alpha = 0.65f), fontSize = 10.sp)
    }
}

@Composable
private fun HomeSmallCard(icon: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clip(RoundedCornerShape(15.dp)).background(Panel).clickable { onClick() }.padding(vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, color = Accent, fontSize = 21.sp)
        Spacer(Modifier.height(7.dp))
        Text(label, color = White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun ProjectRow(title: String, info: String, ratio: String, colors: List<Color>, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp).clip(RoundedCornerShape(14.dp))
            .background(Panel).clickable { onClick() }.padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(width = 66.dp, height = 76.dp).clip(RoundedCornerShape(9.dp)).background(Brush.linearGradient(colors)), contentAlignment = Alignment.Center) {
            Text("▶", color = White, fontSize = 15.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(info, color = Muted, fontSize = 10.sp)
        }
        SmallPill(ratio, Muted)
        Spacer(Modifier.width(5.dp))
        Text("⋮", color = Muted, fontSize = 20.sp)
    }
}

@Composable
private fun ToolExploreCard(title: String, description: String, icon: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(14.dp)).background(Panel).clickable { onClick() }.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(11.dp)).background(Panel2), contentAlignment = Alignment.Center) {
            Text(icon, color = Accent, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(description, color = Muted, fontSize = 10.sp)
        }
        Text("›", color = Muted, fontSize = 23.sp)
    }
}

@Composable
private fun TemplateGrid(onUseTemplate: () -> Unit) {
    val colors = listOf(
        listOf(Color(0xFF2B4662), Color(0xFF6C4772)),
        listOf(Color(0xFFB86A4D), Color(0xFF4A334F)),
        listOf(Color(0xFF267C78), Color(0xFF26394E)),
        listOf(Color(0xFFB07B46), Color(0xFF493A61))
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        for (i in 0..1) TemplateTile("Template " + (i + 1), "00:15  •  " + (i + 4) + " clips", colors[i], Modifier.weight(1f), onUseTemplate)
    }
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        for (i in 2..3) TemplateTile("Template " + (i + 1), "00:12  •  " + (i + 3) + " clips", colors[i], Modifier.weight(1f), onUseTemplate)
    }
}

@Composable
private fun TemplateTile(title: String, meta: String, colors: List<Color>, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Panel).clickable { onClick() }) {
        Box(Modifier.fillMaxWidth().height(145.dp).background(Brush.linearGradient(colors)), contentAlignment = Alignment.Center) {
            Text("▶", color = White, fontSize = 24.sp)
            SmallPill("TRENDING", White)
        }
        Column(Modifier.padding(10.dp)) {
            Text(title, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(meta, color = Muted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun MediaGridTile(index: Int, tab: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val palettes = listOf(
        listOf(Color(0xFF315F72), Color(0xFF1F3247)),
        listOf(Color(0xFF845B55), Color(0xFF322D48)),
        listOf(Color(0xFF3C7770), Color(0xFF273F56)),
        listOf(Color(0xFF9A7449), Color(0xFF443752)),
        listOf(Color(0xFF596D92), Color(0xFF2A344A)),
        listOf(Color(0xFF9A5F72), Color(0xFF45304B))
    )
    Box(modifier.aspectRatio(0.92f).clip(RoundedCornerShape(5.dp)).background(Brush.linearGradient(palettes[index % palettes.size])).clickable { onClick() }) {
        Text(if (tab == "Videos") "▶  00:" + (10 + index).toString() else "▧", color = White.copy(alpha = 0.8f), fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart).padding(6.dp))
        if (selected) {
            Box(Modifier.align(Alignment.TopEnd).padding(6.dp).size(22.dp).clip(CircleShape).background(Accent), contentAlignment = Alignment.Center) {
                Text("✓", color = Bg, fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun SurfaceCard(title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Panel).clickable { onClick() }.padding(14.dp)) {
        Text(title, color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(subtitle, color = Muted, fontSize = 9.sp)
    }
}

@Composable
private fun ProfileRow(icon: String, title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = Accent, fontSize = 18.sp, modifier = Modifier.width(35.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = Muted, fontSize = 10.sp)
        }
        Text("›", color = Muted, fontSize = 22.sp)
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(Line.copy(alpha = 0.55f)))
}

@Composable
private fun SettingHeading(title: String, subtitle: String) {
    Text(title, color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    if (subtitle.isNotEmpty()) Text(subtitle, color = Muted, fontSize = 10.sp)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ChoiceRow(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            SmallPill(item, if (item == selected) Accent else Muted, onClick = { onSelect(item) })
        }
    }
}

@Composable
private fun ToggleRow(title: String, description: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(description, color = Muted, fontSize = 10.sp)
        }
        SmallPill(if (checked) "ON" else "OFF", if (checked) Accent else Muted) { onToggle(!checked) }
    }
}

@Composable
private fun BottomNav(selected: String, onNavigate: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color(0xFF101217)).padding(horizontal = 12.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceAround) {
        BottomNavItem("Edit", "✂", selected == "Edit") { onNavigate("home") }
        BottomNavItem("Templates", "▦", selected == "Templates") { onNavigate("templates") }
        BottomNavItem("Library", "♫", selected == "Library") { onNavigate("library") }
        BottomNavItem("Me", "◉", selected == "Me") { onNavigate("profile") }
    }
}

@Composable
private fun BottomNavItem(label: String, icon: String, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(70.dp).clip(RoundedCornerShape(10.dp)).clickable { onClick() }.padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, color = if (selected) Accent else Muted, fontSize = 19.sp)
        Text(label, color = if (selected) Accent else Muted, fontSize = 9.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun SectionHeading(title: String, action: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (action.isNotEmpty()) Text(action + "  ›", color = Accent, fontSize = 11.sp)
    }
}

@Composable
private fun HorizontalChips(items: List<String>, selected: String = items.first(), onSelect: (String) -> Unit = {}) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item -> SmallPill(item, if (item == selected) Accent else Muted) { onSelect(item) } }
    }
}

@Composable
private fun FakeSearch(hint: String) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(13.dp)).background(Panel).padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("⌕", color = Muted, fontSize = 19.sp)
        Spacer(Modifier.width(9.dp))
        Text(hint, color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun SmallPill(text: String, color: Color, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.clip(CircleShape).clickable { onClick() },
        color = when (color) {
            Accent -> Color(0xFF173E39)
            AccentBlue -> Color(0xFF1D3447)
            else -> Panel2
        },
        shape = CircleShape
    ) {
        Text(text, color = if (color == Accent || color == AccentBlue) color else White, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
    }
}

@Composable
private fun CircleAction(text: String, onClick: () -> Unit) {
    Box(Modifier.size(38.dp).clip(CircleShape).background(Panel).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(text, color = White, fontSize = 19.sp)
    }
}

@Composable
private fun PrimaryAction(text: String, enabled: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.clip(RoundedCornerShape(10.dp))
            .background(if (enabled) Accent else Panel2)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 15.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (enabled) Bg else Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
private fun CapCutSettingsScreen(onBack: () -> Unit) {
    var language by remember { mutableStateOf("English") }
    var appearance by remember { mutableStateOf("Dark") }
    var notifications by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 30.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Text("Settings", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(18.dp))
        SettingHeading("App preferences", "")
        ProfileRow("文", "Language", language) { language = if (language == "English") "Urdu" else "English" }
        ProfileRow("◐", "Appearance", appearance) { appearance = if (appearance == "Dark") "System default" else "Dark" }
        ToggleRow("Notifications", "Product tips and project reminders", notifications) { notifications = it }
        ToggleRow("Haptic feedback", "Vibration on editing controls", haptics) { haptics = it }
        Spacer(Modifier.height(16.dp))
        SettingHeading("Editor settings", "")
        ProfileRow("▤", "Default video resolution", "1080p") { }
        ProfileRow("◷", "Default frame rate", "30 fps") { }
        ProfileRow("▣", "Save exports to", "Movies / SAJI CapCut 2") { }
        ProfileRow("⌫", "Clear cache", "Temporary files and previews") { }
        Spacer(Modifier.height(16.dp))
        SettingHeading("About", "")
        ProfileRow("ⓘ", "App version", "0.2.0 UI Foundation") { }
        ProfileRow("♡", "Feedback", "Send suggestions") { }
        Text("SAJI CapCut 2  •  UI Foundation", color = Muted, fontSize = 10.sp, modifier = Modifier.fillMaxWidth().padding(top = 18.dp), textAlign = TextAlign.Center)
    }
}
