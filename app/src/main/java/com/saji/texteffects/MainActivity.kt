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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
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
    "Filters", "Transitions", "Captions", "Format", "Canvas", "More"
)

private val toolOptions = mapOf(
    "Edit" to listOf("Split", "Speed", "Animation", "Style", "Crop", "Rotate", "Mirror", "Freeze", "Keyframe", "Stabilize", "Retouch", "Mask", "Tracking", "Cutout", "Adjust", "Opacity", "Volume"),
    "Clip" to listOf("Replace", "Split", "Speed", "Volume", "Animation", "Adjust", "Mask", "Opacity", "Copy", "Reverse", "Freeze", "Keyframe", "Delete"),
    "Audio" to listOf("Sounds", "Sound effects", "Extract audio", "Voiceover", "Text to speech", "Beats", "Volume", "Fade", "Voice effects", "Noise reduction"),
    "Text" to listOf("Add text", "Text templates", "Auto captions", "Fonts", "Style", "Effects", "Animation", "Bubbles", "Text to speech", "Tracking"),
    "Stickers" to listOf("Trending", "Emoji", "Decorative", "Labels", "Social", "My stickers", "AI stickers"),
    "Overlay" to listOf("Add overlay", "Mask", "Blend", "Opacity", "Animation", "Tracking", "Remove background"),
    "Effects" to listOf("Video effects", "Body effects", "AI effects", "Trending", "Glitch", "Blur", "Light", "Retro", "Lens", "Distortion"),
    "Filters" to listOf("Trending", "Portrait", "Movie", "Retro", "Food", "Night", "Landscape", "Mono", "Adjust"),
    "Transitions" to listOf("Basic", "Camera", "MG", "Blur", "Slide", "Mask", "Split", "Light", "3D", "Zoom"),
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
    var searchReturnTo by remember { mutableStateOf("home") }
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
                    onNewProject = { pickerReturnTo = "home"; page = "picker" },
                    onOpenProject = { page = "editor" },
                    onSeeAll = { page = "projects" },
                    onSearch = { searchReturnTo = "home"; page = "search" },
                    onNavigate = { page = it }
                )
                "projects" -> ProjectsScreen(
                    onBack = { page = "home" },
                    onOpenProject = { page = "editor" },
                    onNavigate = { page = it }
                )
                "picker" -> MediaPickerScreen(
                    onBack = { page = pickerReturnTo },
                    onAdd = { page = "editor" }
                )
                "capture" -> CaptureStudioScreen(
                    onBack = { page = "home" },
                    onImport = { pickerReturnTo = "capture"; page = "picker" }
                )
                "editor" -> EditorScreen(
                    onBack = { page = "home" },
                    onExport = { selectedExportNotice = false; page = "export" },
                    onAddMedia = { pickerReturnTo = "editor"; page = "picker" }
                )
                "templates" -> TemplatesScreen(
                    onBack = { page = "home" },
                    onUseTemplate = { page = "templateDetail" },
                    onSearch = { searchReturnTo = "templates"; page = "search" },
                    onNavigate = { page = it }
                )
                "templateDetail" -> TemplateDetailScreen(
                    onBack = { page = "templates" },
                    onUseTemplate = { pickerReturnTo = "templateDetail"; page = "picker" }
                )
                "search" -> DiscoverSearchScreen(
                    onBack = { page = searchReturnTo },
                    onOpenTemplate = { page = "templateDetail" },
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
                    onNewProject = { pickerReturnTo = "home"; page = "picker" },
                    onOpenProject = { page = "editor" },
                    onSeeAll = { page = "projects" },
                    onSearch = { searchReturnTo = "home"; page = "search" },
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
    onSeeAll: () -> Unit,
    onSearch: () -> Unit,
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
                CircleAction("⌕", onSearch)
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
                HomeSmallCard("◉", "Camera", Modifier.weight(1f)) { onNavigate("capture") }
                HomeSmallCard("▤", "Teleprompter", Modifier.weight(1f)) { onNewProject() }
            }
            Spacer(Modifier.height(26.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Recent projects", color = White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("See all  ›", color = Accent, fontSize = 12.sp, modifier = Modifier.clickable { onSeeAll() })
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
private fun TemplatesScreen(onBack: () -> Unit, onUseTemplate: () -> Unit, onSearch: () -> Unit, onNavigate: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Templates", color = White, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                CircleAction("⌕", onSearch)
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
    var query by remember { mutableStateOf("") }
    var effectType by remember { mutableStateOf("Video") }
    var fontType by remember { mutableStateOf("Popular") }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your library", fontSize = 26.sp, color = White, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                CircleAction("＋") { }
            }
            Spacer(Modifier.height(12.dp))
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search saved assets") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Panel,
                    unfocusedContainerColor = Panel,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = Accent
                )
            )
            Spacer(Modifier.height(12.dp))
            HorizontalChips(listOf("Sounds", "Effects", "Fonts", "Stickers", "Brand kit"), category) { category = it }
            Spacer(Modifier.height(18.dp))
            when (category) {
                "Sounds" -> {
                    SectionHeading("Saved sounds", "Browse")
                    Spacer(Modifier.height(8.dp))
                    HorizontalChips(listOf("All", "Music", "Effects", "Voice"), "All") { }
                    Spacer(Modifier.height(9.dp))
                    LibraryAudioRow("Midnight drive", "00:24  •  Chill / Electronic", query)
                    LibraryAudioRow("Dreamy piano", "00:32  •  Instrumental", query)
                    LibraryAudioRow("Urban beat", "00:18  •  Hip hop", query)
                    LibraryAudioRow("Soft ambience", "00:45  •  Ambient", query)
                    Spacer(Modifier.height(10.dp))
                    SectionHeading("Browse music", "")
                    ToolExploreCard("Trending tracks", "Popular sounds for short videos", "♫") { }
                    ToolExploreCard("Sound effects", "Transitions, clicks and atmosphere", "✧") { }
                    ToolExploreCard("Extracted audio", "Audio from your imported clips", "⇣") { }
                }
                "Effects" -> {
                    SectionHeading("My effects", "Browse")
                    Spacer(Modifier.height(8.dp))
                    HorizontalChips(listOf("Video", "Body", "Photo"), effectType) { effectType = it }
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        LibraryAssetCard("Glow", "Neon", Modifier.weight(1f))
                        LibraryAssetCard("Retro", "Film", Modifier.weight(1f))
                        LibraryAssetCard("Blur", "Motion", Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        LibraryAssetCard("RGB", "Glitch", Modifier.weight(1f))
                        LibraryAssetCard("Light", "Lens", Modifier.weight(1f))
                        LibraryAssetCard("3D", "Depth", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(10.dp))
                    ToolExploreCard("Effect collections", "Browse saved and downloaded effects", "✦") { }
                }
                "Fonts" -> {
                    SectionHeading("My fonts", "Manage")
                    Spacer(Modifier.height(8.dp))
                    HorizontalChips(listOf("Popular", "Classic", "Display", "Handwriting", "Serif"), fontType) { fontType = it }
                    Spacer(Modifier.height(9.dp))
                    FontLibraryRow("Classic Bold", "Aa  •  Sans serif", query)
                    FontLibraryRow("Modern Display", "Aa  •  Display", query)
                    FontLibraryRow("Handwritten Note", "Aa  •  Handwriting", query)
                    FontLibraryRow("Editorial Serif", "Aa  •  Serif", query)
                    ToolExploreCard("Download fonts", "Browse typefaces for your projects", "Aa") { }
                }
                "Stickers" -> {
                    SectionHeading("Saved stickers", "Browse")
                    Spacer(Modifier.height(8.dp))
                    HorizontalChips(listOf("All", "Emoji", "Decorative", "Labels", "Social"), "All") { }
                    Spacer(Modifier.height(10.dp))
                    repeat(2) { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("✦", "♡", "★", "☀", "♫").forEachIndexed { index, symbol ->
                                Box(Modifier.weight(1f).height(58.dp).clip(RoundedCornerShape(10.dp)).background(Panel).clickable { }, contentAlignment = Alignment.Center) {
                                    Text(symbol, color = listOf(Accent, Color(0xFFFFC45C), Color(0xFFFF7595))[index % 3], fontSize = 23.sp)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    ToolExploreCard("Sticker collection", "Discover decorative packs and labels", "☺") { }
                }
                else -> {
                    SectionHeading("Brand kit", "Edit")
                    Spacer(Modifier.height(8.dp))
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Panel).padding(14.dp)) {
                        Column {
                            Text("Brand colors", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(Accent, AccentBlue, Color(0xFFFFC45C), Color(0xFFFF658A), Color(0xFF8B79FF), Color.White).forEach {
                                    Box(Modifier.size(32.dp).clip(CircleShape).background(it).border(1.dp, Line, CircleShape).clickable { })
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    SettingHeading("Brand fonts", "")
                    FontLibraryRow("Brand headline", "Display typeface", query)
                    FontLibraryRow("Brand body", "Readable sans serif", query)
                    SettingHeading("Logos and assets", "")
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        LibraryAssetCard("Logo", "Primary", Modifier.weight(1f))
                        LibraryAssetCard("Logo", "Alternate", Modifier.weight(1f))
                        LibraryAssetCard("Mark", "Icon", Modifier.weight(1f))
                    }
                    ToolExploreCard("Add brand assets", "Import logos, colors and fonts", "＋") { }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        BottomNav("Library", onNavigate)
    }
}

@Composable
private fun LibraryAudioRow(title: String, meta: String, query: String) {
    if (query.isNotBlank() && !title.contains(query, ignoreCase = true)) return
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp)).background(Panel).padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A514B)), contentAlignment = Alignment.Center) {
            Text("♫", color = Accent, fontSize = 18.sp)
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(meta, color = Muted, fontSize = 8.sp)
        }
        SmallPill("＋", Accent)
        Spacer(Modifier.width(5.dp))
        Text("⋮", color = Muted, fontSize = 18.sp)
    }
}

@Composable
private fun FontLibraryRow(title: String, meta: String, query: String) {
    if (query.isNotBlank() && !title.contains(query, ignoreCase = true)) return
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp)).background(Panel).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(width = 48.dp, height = 42.dp).clip(RoundedCornerShape(7.dp)).background(Panel2), contentAlignment = Alignment.Center) {
            Text("Aa", color = Accent, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(meta, color = Muted, fontSize = 8.sp)
        }
        SmallPill("Use", Accent)
    }
}

@Composable
private fun LibraryAssetCard(title: String, meta: String, modifier: Modifier) {
    Column(modifier.padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp)).background(Panel).clickable { }.padding(8.dp)) {
        Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(7.dp)).background(Brush.linearGradient(listOf(Color(0xFF315766), Color(0xFF6B4E77)))), contentAlignment = Alignment.Center) {
            Text(title, color = White, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(5.dp))
        Text(meta, color = Muted, fontSize = 8.sp)
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
    var albumOpen by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(setOf<Int>()) }
    val albums = listOf("All media", "Camera", "CapCut", "Downloads", "Favorites", "Screenshots", "WhatsApp")
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("×", color = White, fontSize = 28.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(18.dp))
            Text("Add media", color = White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Next", color = if (selected.isNotEmpty()) Accent else Muted, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { if (selected.isNotEmpty()) onAdd() })
        }
        HorizontalChips(listOf("Videos", "Photos", "Live photos"), tab) { tab = it; selected = emptySet() }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(album + "  ▾", color = White, fontSize = 13.sp, modifier = Modifier.clickable { albumOpen = !albumOpen })
            Spacer(Modifier.weight(1f))
            Text("Select multiple  ▦", color = Muted, fontSize = 11.sp, modifier = Modifier.clickable { selected = if (selected.size == 12) emptySet() else (0..11).toSet() })
        }
        if (albumOpen) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp).clip(RoundedCornerShape(12.dp)).background(Panel).padding(9.dp)) {
                Text("Albums", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                albums.forEach { name ->
                    Row(Modifier.fillMaxWidth().clickable { album = name; albumOpen = false }.padding(horizontal = 8.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("▧", color = Accent, fontSize = 16.sp, modifier = Modifier.width(32.dp))
                        Text(name, color = if (album == name) Accent else White, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        if (album == name) Text("✓", color = Accent, fontSize = 12.sp)
                    }
                }
            }
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
            if (selected.isNotEmpty()) {
                Text(selected.size.toString() + " item(s) selected", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    selected.sorted().forEach { index ->
                        Box(Modifier.size(54.dp).clip(RoundedCornerShape(7.dp)).background(Brush.linearGradient(listOf(Color(0xFF355B68), Color(0xFF76506A)))), contentAlignment = Alignment.Center) {
                            Text((index + 1).toString(), color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Text("Showing sample media layout. Device gallery connection will be enabled in the import phase.", color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(12.dp))
            }
        }
        Row(Modifier.fillMaxWidth().background(Panel).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(selected.size.toString() + " selected", color = White, fontSize = 12.sp, modifier = Modifier.weight(1f))
            PrimaryAction("Add to project", selected.isNotEmpty(), onClick = { onAdd() })
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
        Modifier.fillMaxWidth().height(292.dp).background(Panel).padding(horizontal = 13.dp, vertical = 9.dp)
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
        Spacer(Modifier.height(8.dp))
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (detail.isEmpty()) {
                Column(Modifier.fillMaxWidth()) {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        options.forEach { option ->
                            TrayOption(option) {
                                when (option) {
                                    "Add overlay" -> onAddMedia()
                                    "Add text" -> detail = "Text editor"
                                    else -> detail = option
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Swipe to explore all " + tool.lowercase() + " tools", color = Muted, fontSize = 10.sp)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallPill("Reset", Muted)
                        SmallPill("Favorites ☆", AccentBlue)
                        Spacer(Modifier.weight(1f))
                        SmallPill("Choose a tool  ›", Accent)
                    }
                }
            } else {
                when (tool) {
                    "Text" -> TextStudioControls(detail)
                    "Audio" -> AudioStudioControls(detail)
                    "Transitions" -> TransitionsStudioControls(detail)
                    "Filters" -> FiltersStudioControls(detail)
                    "Effects" -> EffectsStudioControls(detail)
                    "Captions" -> CaptionsStudioControls(detail)
                    "Stickers" -> StickersStudioControls(detail)
                    "Overlay" -> OverlayStudioControls(detail)
                    else -> ToolDetailControls(tool, detail)
                }
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SmallPill("Reset", Muted) { intensity = 0.55f; duration = 0.5f }
            Spacer(Modifier.weight(1f))
            SmallPill("Apply  ✓", Accent)
        }
    }
}

@Composable
private fun TextStudioControls(detail: String) {
    var textValue by remember { mutableStateOf("Your story starts here") }
    var selected by remember { mutableStateOf("Modern") }
    var fontSize by remember { mutableFloatStateOf(42f) }
    val choices = when (detail) {
        "Fonts" -> listOf("Modern", "Classic", "Bold", "Handwriting", "Serif", "Mono")
        "Effects" -> listOf("Shadow", "Glow", "Outline", "Neon", "3D", "Glitch")
        "Animation" -> listOf("Fade in", "Pop up", "Typewriter", "Bounce", "Slide", "Loop")
        "Bubbles" -> listOf("Basic", "Speech", "Comic", "Rounded", "Cloud", "Minimal")
        "Text templates" -> listOf("Title", "Subtitle", "Vlog", "Lyrics", "Quote", "Minimal")
        "Auto captions" -> listOf("Auto generate", "Bilingual", "Highlight words", "Caption style", "Speaker")
        "Text to speech" -> listOf("Voice library", "Male voice", "Female voice", "Speed", "Pitch")
        "Tracking" -> listOf("Track object", "Attach text", "Reset tracking")
        else -> listOf("Classic", "Modern", "Minimal", "Bold", "Outline", "Gradient")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        if (detail == "Text editor" || detail == "Add text" || detail == "Style") {
            Text("TEXT CONTENT", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            TextField(
                value = textValue, onValueChange = { textValue = it },
                modifier = Modifier.fillMaxWidth().height(54.dp), singleLine = true,
                textStyle = TextStyle(color = White, fontSize = 13.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Panel2, unfocusedContainerColor = Panel2,
                    focusedTextColor = White, unfocusedTextColor = White,
                    focusedIndicatorColor = Accent, unfocusedIndicatorColor = Color.Transparent
                )
            )
            Spacer(Modifier.height(8.dp))
            Text("FONT PRESETS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Modern", "Classic", "Bold", "Handwriting", "Serif").forEach {
                    SmallPill(it, if (selected == it) Accent else Muted) { selected = it }
                }
            }
            Spacer(Modifier.height(5.dp))
            Text("Font size: " + fontSize.toInt() + " px", color = White, fontSize = 10.sp)
            Slider(value = fontSize, onValueChange = { fontSize = it }, valueRange = 12f..100f)
        } else {
            Text(("EXPLORE " + detail).uppercase(), color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            choices.chunked(3).forEach { rowItems ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    rowItems.forEach { item ->
                        Box(Modifier.weight(1f).padding(bottom = 7.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (selected == item) Accent.copy(alpha = 0.18f) else Panel2)
                            .clickable { selected = item }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                            Text(item, color = if (selected == item) Accent else White, fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }
                    repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Text("Preview style", color = Muted, fontSize = 9.sp)
            Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(10.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF193D4A), Panel2))), contentAlignment = Alignment.Center) {
                Text("SAJI  •  " + selected, color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AudioStudioControls(detail: String) {
    var selected by remember { mutableStateOf("Original") }
    var volume by remember { mutableFloatStateOf(0.8f) }
    var fadeIn by remember { mutableFloatStateOf(0.5f) }
    var fadeOut by remember { mutableFloatStateOf(0.5f) }
    val choices = when (detail) {
        "Sounds" -> listOf("Recommended", "Trending", "Vlog", "Cinematic", "Lo-fi", "Saved")
        "Sound effects" -> listOf("Whoosh", "Impact", "Riser", "Pop", "Transition", "Nature")
        "Voice effects" -> listOf("Original", "Deep", "Chipmunk", "Robot", "Echo", "Radio")
        "Beats" -> listOf("Auto detect", "Add beat", "Remove beat", "Beat markers")
        "Noise reduction" -> listOf("Light", "Standard", "Strong", "Voice focus")
        "Text to speech" -> listOf("Voice library", "Male voice", "Female voice", "Narration", "Preview voice")
        "Voiceover" -> listOf("Record voiceover", "Microphone", "Countdown", "Retake")
        "Extract audio" -> listOf("Import video", "Extract track", "Separate audio")
        else -> listOf("Original", "Fade in", "Fade out", "Volume", "Stereo", "Normalize")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        if (detail == "Volume" || detail == "Fade" || detail == "Voiceover") {
            Text("AUDIO TRACK PREVIEW", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth().height(38.dp).clip(RoundedCornerShape(9.dp)).background(Panel2).padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(42) { index ->
                    Box(Modifier.weight(1f).height((7 + ((index * 17) % 25)).dp).clip(RoundedCornerShape(3.dp))
                        .background(if (index % 4 == 0) AccentBlue else Accent.copy(alpha = 0.75f)))
                }
            }
            Spacer(Modifier.height(7.dp))
            Text("Volume  " + (volume * 100).toInt() + "%", color = White, fontSize = 10.sp)
            Slider(value = volume, onValueChange = { volume = it })
            Text("Fade in  " + fadeIn + "s", color = White, fontSize = 10.sp)
            Slider(value = fadeIn, onValueChange = { fadeIn = it }, valueRange = 0f..5f)
            Text("Fade out  " + fadeOut + "s", color = White, fontSize = 10.sp)
            Slider(value = fadeOut, onValueChange = { fadeOut = it }, valueRange = 0f..5f)
        } else {
            Text(("EXPLORE " + detail).uppercase(), color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            choices.chunked(3).forEach { rowItems ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    rowItems.forEach { item ->
                        Box(Modifier.weight(1f).padding(bottom = 7.dp).clip(RoundedCornerShape(10.dp))
                            .background(if (selected == item) Accent.copy(alpha = 0.18f) else Panel2)
                            .clickable { selected = item }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) {
                            Text(item, color = if (selected == item) Accent else White, fontSize = 10.sp, textAlign = TextAlign.Center)
                        }
                    }
                    repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
                }
            }
            Text("Selected: " + selected, color = Muted, fontSize = 9.sp)
            Row(Modifier.fillMaxWidth().height(38.dp).clip(RoundedCornerShape(9.dp)).background(Panel2).padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(36) { index ->
                    Box(Modifier.weight(1f).height((6 + ((index * 13) % 24)).dp).clip(RoundedCornerShape(3.dp))
                        .background(if (index % 3 == 0) AccentBlue else Accent.copy(alpha = 0.75f)))
                }
            }
        }
    }
}

@Composable
private fun TransitionsStudioControls(detail: String) {
    var selected by remember(detail) { mutableStateOf("Smooth zoom") }
    var duration by remember { mutableFloatStateOf(0.6f) }
    var direction by remember { mutableStateOf("In") }
    val presets = when (detail) {
        "Camera" -> listOf("Camera push", "Whip pan", "Handheld", "Dolly", "Orbit", "Pull focus")
        "MG" -> listOf("Spin", "Flip", "Cube", "Glitch", "Warp", "3D rotate")
        "Blur" -> listOf("Motion blur", "Zoom blur", "Radial blur", "Soft blur", "Directional")
        "Slide" -> listOf("Slide left", "Slide right", "Slide up", "Slide down", "Push", "Wipe")
        "Mask" -> listOf("Circle", "Split", "Diagonal", "Heart", "Iris", "Shape reveal")
        "Split" -> listOf("Vertical", "Horizontal", "Cross", "Four panels", "Shutter")
        "Light" -> listOf("Flash", "Light leak", "Glow wash", "Flicker", "Lens flare")
        "3D" -> listOf("Cube", "Page turn", "Flip", "Depth zoom", "Perspective")
        "Zoom" -> listOf("Smooth zoom", "Zoom in", "Zoom out", "Bounce zoom", "Crash zoom")
        else -> listOf("Dissolve", "Fade", "Mix", "Cross blur", "Dip to black", "Dip to white")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("TRANSITION PREVIEW", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(10.dp)).background(Brush.horizontalGradient(listOf(Color(0xFF214A5B), Color(0xFF51406E), Color(0xFF9B5B59)))), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("CLIP A", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("✦", color = Accent, fontSize = 20.sp)
                Text("CLIP B", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(selected, color = White.copy(alpha = 0.8f), fontSize = 8.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 3.dp))
        }
        Spacer(Modifier.height(5.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf("In", "Out", "Both").forEach { SmallPill(it, if (direction == it) Accent else Muted) { direction = it } }
            Spacer(Modifier.weight(1f))
            Text("Direction", color = Muted, fontSize = 9.sp)
        }
        Text("Duration  " + String.format("%.1f", 0.2f + duration * 2.8f) + "s", color = White, fontSize = 10.sp)
        Slider(value = duration, onValueChange = { duration = it })
        Text("PRESETS  •  " + detail.uppercase(), color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        presets.chunked(3).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { item ->
                    Box(Modifier.weight(1f).padding(bottom = 5.dp).clip(RoundedCornerShape(8.dp)).background(if (selected == item) Accent.copy(alpha = 0.18f) else Panel2).clickable { selected = item }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(item, color = if (selected == item) Accent else White, fontSize = 9.sp, textAlign = TextAlign.Center)
                    }
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun FiltersStudioControls(detail: String) {
    var selected by remember(detail) { mutableStateOf("Cinematic") }
    var intensity by remember { mutableFloatStateOf(0.75f) }
    var filterGroup by remember { mutableStateOf("Trending") }
    val filters = when (filterGroup) {
        "Portrait" -> listOf("Natural", "Soft skin", "Warm face", "Studio", "Glow", "Classic")
        "Movie" -> listOf("Cinematic", "Teal orange", "Film 01", "Film 02", "Noir", "Drama")
        "Retro" -> listOf("Vintage", "1998", "Fade", "Disposable", "VHS", "Dust")
        "Food" -> listOf("Fresh", "Sweet", "Warm", "Juicy", "Bright", "Cafe")
        "Night" -> listOf("Night glow", "Neon city", "Low light", "Blue hour", "Moonlight")
        "Landscape" -> listOf("Vivid", "Nature", "Golden hour", "Ocean", "Forest")
        "Mono" -> listOf("B&W", "Silver", "Matte mono", "High contrast")
        else -> listOf("Cinematic", "Warm", "Cool", "Clean", "Vivid", "Soft")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("FILTER PREVIEW", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(0xFF254A58), Color(0xFFB47755), Color(0xFF43385D)))), contentAlignment = Alignment.Center) {
            Text(selected.uppercase(), color = White, fontSize = 17.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Trending", "Portrait", "Movie", "Retro", "Food", "Night", "Landscape", "Mono").forEach {
                SmallPill(it, if (filterGroup == it) Accent else Muted) { filterGroup = it; selected = when (it) { "Movie" -> "Cinematic"; "Retro" -> "Vintage"; "Mono" -> "B&W"; else -> "Natural" } }
            }
        }
        filters.chunked(3).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { item ->
                    Box(Modifier.weight(1f).padding(bottom = 5.dp).clip(RoundedCornerShape(8.dp)).background(if (selected == item) Accent.copy(alpha = 0.18f) else Panel2).clickable { selected = item }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(item, color = if (selected == item) Accent else White, fontSize = 9.sp, textAlign = TextAlign.Center)
                    }
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Text("Intensity  " + (intensity * 100).toInt() + "%", color = White, fontSize = 10.sp)
        Slider(value = intensity, onValueChange = { intensity = it })
    }
}

@Composable
private fun EffectsStudioControls(detail: String) {
    var selected by remember(detail) { mutableStateOf("Glow") }
    var intensity by remember { mutableFloatStateOf(0.7f) }
    var effectGroup by remember { mutableStateOf("Video") }
    val effects = when (detail) {
        "Body effects" -> listOf("Face glow", "Outline", "Body stretch", "Motion trail", "Sparkle", "Aura")
        "AI effects" -> listOf("AI portrait", "Dream", "Comic", "Anime look", "3D face", "Background")
        "Glitch" -> listOf("RGB split", "Digital noise", "Signal loss", "Pixel sort", "Scan lines")
        "Blur" -> listOf("Motion blur", "Gaussian", "Radial", "Zoom blur", "Background blur")
        "Light" -> listOf("Glow", "Lens flare", "Light leak", "Bokeh", "Flash", "Prism")
        "Retro" -> listOf("VHS", "Film grain", "Vintage", "Old TV", "Dust", "Flicker")
        "Lens" -> listOf("Fisheye", "Wide lens", "Chromatic", "Dream lens", "Vignette")
        "Distortion" -> listOf("Wave", "Ripple", "Warp", "Kaleidoscope", "Liquid")
        else -> listOf("Glow", "Neon", "3D", "RGB", "Film grain", "Motion blur")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("EFFECT PREVIEW", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(10.dp)).background(Brush.linearGradient(listOf(Color(0xFF173F50), Color(0xFF56426C), Color(0xFF9B5C69)))), contentAlignment = Alignment.Center) {
            Text("SAJI  ✦  " + selected.uppercase(), color = White, fontSize = 16.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Video", "Body", "AI").forEach { SmallPill(it, if (effectGroup == it) Accent else Muted) { effectGroup = it } }
        }
        effects.chunked(3).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { item ->
                    Box(Modifier.weight(1f).padding(bottom = 5.dp).clip(RoundedCornerShape(8.dp)).background(if (selected == item) Accent.copy(alpha = 0.18f) else Panel2).clickable { selected = item }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(item, color = if (selected == item) Accent else White, fontSize = 9.sp, textAlign = TextAlign.Center)
                    }
                }
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Text("Intensity  " + (intensity * 100).toInt() + "%", color = White, fontSize = 10.sp)
        Slider(value = intensity, onValueChange = { intensity = it })
        Text("Effect selected: " + selected + "  •  Preview UI only", color = Muted, fontSize = 8.sp)
    }
}

@Composable
private fun CaptionsStudioControls(detail: String) {
    var captionText by remember { mutableStateOf("Your story starts here...") }
    var selectedStyle by remember { mutableStateOf("Bold highlight") }
    var language by remember { mutableStateOf("English") }
    var highlight by remember { mutableStateOf(true) }
    val styles = listOf("Bold highlight", "Karaoke", "Minimal", "Boxed", "Classic", "Creator")
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("CAPTION EDITOR", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        TextField(value = captionText, onValueChange = { captionText = it }, modifier = Modifier.fillMaxWidth().height(54.dp), singleLine = true, textStyle = TextStyle(color = White, fontSize = 12.sp), colors = TextFieldDefaults.colors(focusedContainerColor = Panel2, unfocusedContainerColor = Panel2, focusedTextColor = White, unfocusedTextColor = White, focusedIndicatorColor = Accent, unfocusedIndicatorColor = Color.Transparent))
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SmallPill("English", if (language == "English") Accent else Muted) { language = "English" }
            SmallPill("Urdu", if (language == "Urdu") Accent else Muted) { language = "Urdu" }
            SmallPill("Bilingual", if (language == "Bilingual") Accent else Muted) { language = "Bilingual" }
        }
        Spacer(Modifier.height(6.dp))
        Text("STYLE PRESETS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        styles.chunked(3).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { item ->
                    Box(Modifier.weight(1f).padding(bottom = 5.dp).clip(RoundedCornerShape(8.dp)).background(if (selectedStyle == item) Accent.copy(alpha = 0.18f) else Panel2).clickable { selectedStyle = item }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(item, color = if (selectedStyle == item) Accent else White, fontSize = 9.sp, textAlign = TextAlign.Center)
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(9.dp)).background(Brush.horizontalGradient(listOf(Panel2, Color(0xFF284D5A)))).padding(10.dp), contentAlignment = Alignment.Center) {
            Text(captionText.ifBlank { "Caption preview" }, color = if (highlight) Accent else White, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Highlight active word", color = White, fontSize = 9.sp, modifier = Modifier.weight(1f))
            SmallPill(if (highlight) "ON" else "OFF", if (highlight) Accent else Muted) { highlight = !highlight }
        }
        Text("Language: " + language + "  •  " + detail + " panel", color = Muted, fontSize = 8.sp)
    }
}

@Composable
private fun StickersStudioControls(detail: String) {
    var category by remember(detail) { mutableStateOf("Trending") }
    var selected by remember { mutableStateOf("✨") }
    var scale by remember { mutableFloatStateOf(0.5f) }
    val stickers = when (category) {
        "Emoji" -> listOf("😀", "😂", "🥹", "😍", "😎", "🤩", "🔥", "💯", "❤️", "🥳", "😴", "🤔")
        "Decorative" -> listOf("✦", "♡", "★", "☾", "☀", "❀", "✧", "♛", "❋", "☁", "➳", "∞")
        "Labels" -> listOf("NEW", "VLOG", "LOVE", "WOW", "SALE", "DAY", "LIVE", "BEST", "MOOD", "TOP", "GO", "HEY")
        "Social" -> listOf("▶", "♡", "♪", "＋", "↗", "#", "@", "♥", "✉", "☻", "◎", "⌁")
        "AI stickers" -> listOf("🤖", "👾", "🪐", "🐉", "🦋", "🌈", "🧸", "🦄", "🚀", "🌙", "💎", "🪄")
        else -> listOf("✨", "🔥", "❤️", "⭐", "🌸", "⚡", "🎧", "☁", "🦋", "💫", "🌙", "🎬")
    }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("STICKER PREVIEW", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(9.dp)).background(Brush.linearGradient(listOf(Color(0xFF1D3F50), Color(0xFF493A65)))), contentAlignment = Alignment.Center) {
            Text(selected, color = White, fontSize = (24 + scale * 24).sp)
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Trending", "Emoji", "Decorative", "Labels", "Social", "AI stickers").forEach {
                SmallPill(it, if (category == it) Accent else Muted) { category = it; selected = when (it) { "Emoji" -> "😀"; "Decorative" -> "✦"; "Labels" -> "NEW"; "Social" -> "▶"; "AI stickers" -> "🤖"; else -> "✨" } }
            }
        }
        Spacer(Modifier.height(6.dp))
        stickers.chunked(6).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                rowItems.forEach { item ->
                    Box(Modifier.weight(1f).height(38.dp).padding(bottom = 3.dp).clip(RoundedCornerShape(7.dp)).background(if (selected == item) Accent.copy(alpha = 0.2f) else Panel2).clickable { selected = item }, contentAlignment = Alignment.Center) {
                        Text(item, color = White, fontSize = 18.sp)
                    }
                }
                repeat(6 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Text("Sticker size  " + (70 + scale * 130).toInt() + "%", color = White, fontSize = 9.sp)
        Slider(value = scale, onValueChange = { scale = it })
        Text("Selected sticker: " + selected + "  •  Sample library preview", color = Muted, fontSize = 8.sp)
    }
}

@Composable
private fun OverlayStudioControls(detail: String) {
    var blend by remember { mutableStateOf("Normal") }
    var opacity by remember { mutableFloatStateOf(0.85f) }
    var scale by remember { mutableFloatStateOf(0.6f) }
    var selectedMask by remember { mutableStateOf("Rectangle") }
    val blendModes = listOf("Normal", "Screen", "Multiply", "Overlay", "Soft light", "Add", "Darken", "Lighten")
    val masks = listOf("Rectangle", "Circle", "Split", "Linear", "Heart", "Custom")
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("OVERLAY COMPOSITION", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(5.dp))
        Box(Modifier.fillMaxWidth().height(54.dp).clip(RoundedCornerShape(9.dp)).background(Brush.linearGradient(listOf(Color(0xFF1A4655), Color(0xFF5A416D), Color(0xFF9B6555)))), contentAlignment = Alignment.Center) {
            Box(Modifier.fillMaxWidth(0.38f).height(34.dp).clip(RoundedCornerShape(7.dp)).background(Accent.copy(alpha = opacity * 0.55f)), contentAlignment = Alignment.Center) {
                Text("OVERLAY", color = White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Text("BASE CLIP", color = White.copy(alpha = 0.65f), fontSize = 8.sp, modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 4.dp))
        }
        Spacer(Modifier.height(6.dp))
        when (detail) {
            "Mask" -> {
                Text("MASK SHAPES", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    masks.forEach { SmallPill(it, if (selectedMask == it) Accent else Muted) { selectedMask = it } }
                }
                Text("Mask: " + selectedMask, color = White, fontSize = 9.sp)
            }
            "Blend" -> {
                Text("BLEND MODE", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                blendModes.chunked(4).forEach { rowItems ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        rowItems.forEach { item -> SmallPill(item, if (blend == item) Accent else Muted) { blend = item } }
                    }
                }
                Text("Blend: " + blend, color = White, fontSize = 9.sp)
            }
            "Tracking" -> {
                Text("MOTION TRACKING", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallPill("Select target", Accent) { }
                    SmallPill("Track forward", Muted) { }
                    SmallPill("Reset", Muted) { }
                }
                Text("Tracking controls are visual placeholders until tracking engine integration.", color = Muted, fontSize = 8.sp)
            }
            else -> {
                Text("OVERLAY TOOLS", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallPill("Add media", Accent) { }
                    SmallPill("Duplicate", Muted) { }
                    SmallPill("Flip", Muted) { }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text("Opacity  " + (opacity * 100).toInt() + "%", color = White, fontSize = 9.sp)
        Slider(value = opacity, onValueChange = { opacity = it })
        Text("Overlay scale  " + (50 + scale * 150).toInt() + "%", color = White, fontSize = 9.sp)
        Slider(value = scale, onValueChange = { scale = it })
        Text("Overlay controls preview  •  Not yet connected to clip rendering", color = Muted, fontSize = 8.sp)
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
    var tab by remember { mutableStateOf("General") }
    var language by remember { mutableStateOf("English") }
    var appearance by remember { mutableStateOf("Dark") }
    var resolution by remember { mutableStateOf("1080p") }
    var frameRate by remember { mutableStateOf("30 fps") }
    var format by remember { mutableStateOf("MP4") }
    var notifications by remember { mutableStateOf(true) }
    var haptics by remember { mutableStateOf(true) }
    var autoSave by remember { mutableStateOf(true) }
    var timelineSnap by remember { mutableStateOf(true) }
    var highQualityPreview by remember { mutableStateOf(false) }
    var smartHdr by remember { mutableStateOf(false) }
    var clearConfirmation by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 30.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(12.dp))
            Text("Settings", color = White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalChips(listOf("General", "Editor", "Export", "Storage", "Privacy", "About"), tab) { tab = it }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp)) {
            when (tab) {
                "General" -> {
                    SettingHeading("App preferences", "Personalize your workspace")
                    ProfileRow("文", "Language", language) { language = if (language == "English") "Urdu" else "English" }
                    ProfileRow("◐", "Appearance", appearance) { appearance = if (appearance == "Dark") "System default" else if (appearance == "System default") "Light" else "Dark" }
                    ToggleRow("Notifications", "Product tips and project reminders", notifications) { notifications = it }
                    ToggleRow("Haptic feedback", "Vibration on editing controls", haptics) { haptics = it }
                }
                "Editor" -> {
                    SettingHeading("Editing preferences", "Control your timeline workspace")
                    ToggleRow("Auto save projects", "Save project changes automatically", autoSave) { autoSave = it }
                    ToggleRow("Timeline snapping", "Align clips and layers to nearby edges", timelineSnap) { timelineSnap = it }
                    ToggleRow("High quality preview", "Use a sharper preview when possible", highQualityPreview) { highQualityPreview = it }
                    SettingHeading("Default canvas", "New project aspect ratio")
                    ChoiceRow(listOf("9:16", "16:9", "1:1", "4:5"), "9:16") { }
                    ProfileRow("◷", "Undo history", "Up to 20 steps") { }
                    ProfileRow("▤", "Default timeline zoom", "Fit project") { }
                }
                "Export" -> {
                    SettingHeading("Default export", "Starting values for new exports")
                    Text("Resolution", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    ChoiceRow(listOf("720p", "1080p", "2K", "4K"), resolution) { resolution = it }
                    Spacer(Modifier.height(13.dp))
                    Text("Frame rate", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    ChoiceRow(listOf("24 fps", "30 fps", "50 fps", "60 fps"), frameRate) { frameRate = it }
                    Spacer(Modifier.height(13.dp))
                    Text("Format", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    ChoiceRow(listOf("MP4", "MOV"), format) { format = it }
                    ToggleRow("Smart HDR", "Preserve expanded color information", smartHdr) { smartHdr = it }
                    ProfileRow("▣", "Default save location", "Movies / SAJI CapCut 2") { }
                }
                "Storage" -> {
                    SettingHeading("Storage management", "Local workspace usage preview")
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Panel).padding(14.dp)) {
                        Column {
                            Text("App storage", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Sample usage display", color = Muted, fontSize = 9.sp)
                            Spacer(Modifier.height(10.dp))
                            Box(Modifier.fillMaxWidth().height(7.dp).clip(CircleShape).background(Panel2)) {
                                Box(Modifier.fillMaxWidth(0.42f).height(7.dp).clip(CircleShape).background(Accent))
                            }
                            Spacer(Modifier.height(7.dp))
                            Text("Cache  •  182 MB        Drafts  •  640 MB", color = Muted, fontSize = 9.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    ProfileRow("⌫", "Clear cache", "Temporary previews and thumbnails") { clearConfirmation = true }
                    ProfileRow("▣", "Downloads", "Manage downloaded fonts and effects") { }
                    ProfileRow("▤", "Export folder", "Movies / SAJI CapCut 2") { }
                    if (clearConfirmation) {
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Panel2).padding(12.dp)) {
                            Text("Clear temporary cache?", color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("This sample screen does not delete files yet. Storage controls will be connected later.", color = Muted, fontSize = 9.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                SmallPill("Cancel", Muted) { clearConfirmation = false }
                                SmallPill("Confirm", Accent) { clearConfirmation = false }
                            }
                        }
                    }
                }
                "Privacy" -> {
                    SettingHeading("Permissions", "Manage access used by editing tools")
                    ProfileRow("▧", "Photos and videos", "Gallery access for importing media") { }
                    ProfileRow("♫", "Microphone", "Voiceover and audio recording") { }
                    ProfileRow("◉", "Camera", "Capture new photos and videos") { }
                    ProfileRow("☁", "Cloud sync", "Optional project synchronization") { }
                    SettingHeading("Privacy controls", "")
                    ProfileRow("ⓘ", "Privacy policy", "How app data is handled") { }
                    ProfileRow("♧", "Usage diagnostics", "Optional crash and performance reports") { }
                }
                else -> {
                    SettingHeading("About SAJI CapCut 2", "Native video editing UI project")
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Panel).padding(20.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("SAJI", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                            Text("CapCut 2", color = White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                            Text("UI Foundation  •  Version 0.2.0", color = Muted, fontSize = 9.sp)
                        }
                    }
                    ProfileRow("ⓘ", "Version information", "0.2.0 UI Foundation") { }
                    ProfileRow("♡", "Feedback", "Send suggestions") { }
                    ProfileRow("©", "Open-source licenses", "Third-party notices") { }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("SAJI CapCut 2  •  UI Foundation", color = Muted, fontSize = 10.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProjectsScreen(
    onBack: () -> Unit,
    onOpenProject: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var category by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    var selectedActions by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 17.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("‹", color = White, fontSize = 28.sp, modifier = Modifier.clickable { onBack() })
                Spacer(Modifier.width(9.dp))
                Text("Projects", color = White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                SmallPill("＋ New", Accent) { onOpenProject() }
            }
            Spacer(Modifier.height(14.dp))
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search your projects") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Panel,
                    unfocusedContainerColor = Panel,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = Accent
                )
            )
            Spacer(Modifier.height(12.dp))
            HorizontalChips(listOf("All", "Drafts", "Shared", "Trash"), category) { category = it; selectedActions = "" }
            Spacer(Modifier.height(17.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (category == "All") "All projects" else category, color = White, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                SmallPill("Modified ▾", Muted)
                Spacer(Modifier.width(6.dp))
                SmallPill("▦", Muted)
            }
            Spacer(Modifier.height(9.dp))
            val projects = listOf(
                Triple("Untitled project", "Today  •  00:18", "9:16"),
                Triple("Travel montage", "Yesterday  •  00:32", "16:9"),
                Triple("Text animation", "Nov 16  •  00:12", "9:16"),
                Triple("My edit 04", "Nov 14  •  00:45", "1:1"),
                Triple("Cinematic test", "Nov 12  •  00:27", "16:9")
            ).filter { it.first.contains(query, ignoreCase = true) }
            if (category == "Trash") {
                DetailEmptyState("Trash is empty", "Deleted projects will appear here")
            } else if (projects.isEmpty()) {
                DetailEmptyState("No matching projects", "Try another search")
            } else {
                projects.forEachIndexed { index, project ->
                    ProjectManagerRow(project.first, project.second, project.third, index, onOpenProject) {
                        selectedActions = if (selectedActions == project.first) "" else project.first
                    }
                    if (selectedActions == project.first) {
                        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            listOf("Rename", "Duplicate", "Share", "Delete").forEach { action ->
                                SmallPill(action, if (action == "Delete") Color(0xFFFF6D7A) else Muted) {
                                    actionMessage = action + " UI selected for " + project.first
                                }
                            }
                        }
                    }
                }
            }
            if (actionMessage.isNotEmpty()) {
                Text(actionMessage + "  •  Actual project management will be connected later.", color = Accent, fontSize = 9.sp, modifier = Modifier.padding(vertical = 8.dp))
            }
            Spacer(Modifier.height(22.dp))
        }
        BottomNav("Edit", onNavigate)
    }
}

@Composable
private fun ProjectManagerRow(
    title: String,
    info: String,
    ratio: String,
    index: Int,
    onOpen: () -> Unit,
    onMore: () -> Unit
) {
    val palettes = listOf(
        listOf(Color(0xFF243F50), Color(0xFF70517C)),
        listOf(Color(0xFFB86A4D), Color(0xFF263C52)),
        listOf(Color(0xFF302D65), Color(0xFF1C6D78)),
        listOf(Color(0xFF69503C), Color(0xFF24343D)),
        listOf(Color(0xFF3A665F), Color(0xFF573E65))
    )
    Row(
        Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(13.dp))
            .background(Panel).clickable { onOpen() }.padding(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(width = 62.dp, height = 72.dp).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(palettes[index % palettes.size])), contentAlignment = Alignment.Center) {
            Text("▶", color = White, fontSize = 15.sp)
            Text(ratio, color = White.copy(alpha = 0.75f), fontSize = 8.sp, modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(3.dp))
            Text(info, color = Muted, fontSize = 9.sp)
            Text("Draft  •  Local project", color = Muted, fontSize = 8.sp)
        }
        Text("⋮", color = White, fontSize = 22.sp, modifier = Modifier.clickable { onMore() }.padding(horizontal = 7.dp))
    }
}

@Composable
private fun TemplateDetailScreen(onBack: () -> Unit, onUseTemplate: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 17.dp)) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("‹", color = White, fontSize = 29.sp, modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(10.dp))
            Text("Template preview", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            CircleAction("♡") { }
            Spacer(Modifier.width(7.dp))
            CircleAction("↗") { }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(Color(0xFF294C63), Color(0xFF61466B), Color(0xFFB46D58)))), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CREATIVE MOMENTS", color = White.copy(alpha = 0.72f), fontSize = 9.sp, letterSpacing = 3.sp)
                Spacer(Modifier.height(15.dp))
                Text("MAKE", color = White, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
                Text("IT YOURS", color = Accent, fontSize = 36.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(Modifier.height(17.dp))
                Text("▶  Preview template", color = White, fontSize = 11.sp)
            }
            SmallPill("TRENDING", White)
        }
        Spacer(Modifier.height(15.dp))
        Text("Cinematic beat sync", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("A fast-paced template with timed cuts, title cards and smooth transitions.", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            SmallPill("00:15", Muted)
            SmallPill("5 clips", Muted)
            SmallPill("9:16", Muted)
            SmallPill("Beat sync", Accent)
        }
        Spacer(Modifier.height(18.dp))
        SectionHeading("Template structure", "5 scenes")
        Spacer(Modifier.height(9.dp))
        repeat(5) { index ->
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(9.dp)).background(Panel).padding(9.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(width = 48.dp, height = 42.dp).clip(RoundedCornerShape(6.dp)).background(Brush.linearGradient(listOf(Color(0xFF355A6C + (index * 0x00020202)), Color(0xFF76506A)))), contentAlignment = Alignment.Center) {
                    Text("▶", color = White, fontSize = 12.sp)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Scene " + (index + 1), color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(if (index == 0) "Opening title" else "Media placeholder", color = Muted, fontSize = 8.sp)
                }
                Text("00:03", color = Muted, fontSize = 9.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        PrimaryAction("Use template", true, onUseTemplate, Modifier.fillMaxWidth())
        Spacer(Modifier.height(23.dp))
    }
}

@Composable
private fun DiscoverSearchScreen(
    onBack: () -> Unit,
    onOpenTemplate: () -> Unit,
    onNavigate: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Templates") }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 17.dp)) {
            Spacer(Modifier.height(17.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("‹", color = White, fontSize = 29.sp, modifier = Modifier.clickable { onBack() })
                Spacer(Modifier.width(10.dp))
                Text("Search & discover", color = White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(13.dp))
            TextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Search templates, sounds, effects") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Panel,
                    unfocusedContainerColor = Panel,
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    cursorColor = Accent
                )
            )
            Spacer(Modifier.height(12.dp))
            DetailSearchTabs(listOf("Templates", "Sounds", "Effects", "Fonts", "Stickers"), category) { category = it }
            Spacer(Modifier.height(18.dp))
            SectionHeading(if (query.isBlank()) "Popular searches" else "Results for " + query, "See all")
            Spacer(Modifier.height(8.dp))
            if (query.isBlank()) {
                listOf("Beat sync edit", "Cinematic vlog", "Lyrics typography", "Photo dump", "Slow motion").forEach { suggestion ->
                    Row(Modifier.fillMaxWidth().clickable { query = suggestion }.padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⌕", color = Muted, fontSize = 16.sp, modifier = Modifier.width(30.dp))
                        Text(suggestion, color = White, fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text("↗", color = Muted, fontSize = 12.sp)
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(Line.copy(alpha = 0.55f)))
                }
            } else if (category == "Templates") {
                repeat(3) { index ->
                    TemplateSearchResult("Template " + (index + 1), "00:15  •  9:16  •  Beat sync", index, onOpenTemplate)
                }
            } else {
                repeat(4) { index ->
                    SearchAssetRow(category + " result " + (index + 1), "Popular  •  Saved by creators", category)
                }
            }
            Spacer(Modifier.height(20.dp))
            SectionHeading("Recommended categories", "")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Vlog", "Gaming", "Travel", "Music").forEach { SmallPill(it, Muted) { query = it } }
            }
            Spacer(Modifier.height(20.dp))
        }
        BottomNav("Templates", onNavigate)
    }
}

@Composable
private fun DetailSearchTabs(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        items.forEach { SmallPill(it, if (selected == it) Accent else Muted) { onSelect(it) } }
    }
}

@Composable
private fun TemplateSearchResult(title: String, info: String, index: Int, onClick: () -> Unit) {
    val palettes = listOf(
        listOf(Color(0xFF2B4662), Color(0xFF6C4772)),
        listOf(Color(0xFFB86A4D), Color(0xFF4A334F)),
        listOf(Color(0xFF267C78), Color(0xFF26394E))
    )
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(12.dp)).background(Panel).clickable { onClick() }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(width = 58.dp, height = 68.dp).clip(RoundedCornerShape(7.dp)).background(Brush.linearGradient(palettes[index % palettes.size])), contentAlignment = Alignment.Center) {
            Text("▶", color = White, fontSize = 14.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(info, color = Muted, fontSize = 8.sp)
        }
        Text("›", color = Accent, fontSize = 20.sp)
    }
}

@Composable
private fun SearchAssetRow(title: String, info: String, category: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(10.dp)).background(Panel).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(35.dp).clip(RoundedCornerShape(8.dp)).background(Panel2), contentAlignment = Alignment.Center) {
            Text(when (category) {
                "Sounds" -> "♫"
                "Effects" -> "✦"
                "Fonts" -> "Aa"
                else -> "☺"
            }, color = Accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(info, color = Muted, fontSize = 8.sp)
        }
        SmallPill("＋", Accent)
    }
}

@Composable
private fun DetailEmptyState(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("▢", color = Muted, fontSize = 32.sp)
        Text(title, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 9.sp)
    }
}
