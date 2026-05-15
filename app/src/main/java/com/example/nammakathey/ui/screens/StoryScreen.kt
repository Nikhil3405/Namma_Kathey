package com.example.nammakathey.ui.screens

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.nammakathey.data.local.loadHeroes
import java.util.Locale

// ── Kindle-inspired palette ───────────────────────────────────────────────────
private val KindleBg        = Color(0xFFFBF0D9)   // warm sepia paper
private val KindleBgDark    = Color(0xFFF5E6C0)   // slightly darker for cards
private val KindleInk       = Color(0xFF2C2416)   // deep warm black
private val KindleInkMuted  = Color(0xFF7A6A52)   // muted brown for secondary text
private val KindleAccent    = Color(0xFF8B5E3C)   // warm brown accent
private val KindleDivider   = Color(0xFFD9C9A8)   // soft rule line
private val KindleHighlight = Color(0xFFE8D5A3)   // selection tint
private val NavyBlue        = Color(0xFF1A237E)
private val TealGreen       = Color(0xFF4CAF82)
private val AmberWarm       = Color(0xFFFAC775)

// Serif-like system fallback — on a real project add a Merriweather / Lora font via Google Fonts
private val ReadingFamily = FontFamily.Serif

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryScreen(
    heroId       : String,
    districtName : String,
    navController: NavController
) {
    fun cleanTextForTTS(text: String) = text
        .replace(Regex("[.,;:!?()\\[\\]{}|\\\\/'\"\\u201C\\u201D]"), " ")

    val context = LocalContext.current

    var storyPages   by remember { mutableStateOf(listOf<String>()) }
    var storyPagesKn by remember { mutableStateOf(listOf<String>()) }
    var isKannada    by remember { mutableStateOf(false) }
    var isTtsReady   by remember { mutableStateOf(false) }
    var isSpeaking   by remember { mutableStateOf(false) }
    var tts          by remember { mutableStateOf<TextToSpeech?>(null) }
    var heroLocation by remember { mutableStateOf<com.example.nammakathey.data.model.Location?>(null) }

    // UI state
    var showControls  by remember { mutableStateOf(true) }   // tap to hide/show chrome
    var fontSize      by remember { mutableStateOf(20f) }     // adjustable reading size

    val pages      = if (isKannada) storyPagesKn else storyPages
    val pagerState = rememberPagerState(initialPage = 0) { pages.size }

    val currentPage = pagerState.currentPage
    val totalPages  = pages.size
    val isLastPage  = totalPages > 0 && currentPage == totalPages - 1
    val readFraction = if (totalPages > 1)
        currentPage.toFloat() / (totalPages - 1) else 1f

    val animReadFraction by animateFloatAsState(
        targetValue   = readFraction,
        animationSpec = tween(400, easing = EaseOutCubic),
        label         = "read_progress"
    )

    // Load data
    LaunchedEffect(Unit) {
        val data = loadHeroes(context)
        val hero = data.districts.flatMap { it.heroes }.find { it.id == heroId }
        storyPages   = hero?.story?.en ?: emptyList()
        storyPagesKn = hero?.story?.kn ?: emptyList()
        heroLocation = hero?.location

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setSpeechRate(0.65f)
                tts?.setPitch(1.0f)
                tts?.setLanguage(Locale.US)
                isTtsReady = true
            }
        }
    }

    // Speak current page when narration is active
    LaunchedEffect(isSpeaking, currentPage) {
        if (!isSpeaking) return@LaunchedEffect
        val text = pages.getOrNull(currentPage) ?: return@LaunchedEffect
        tts?.speak(cleanTextForTTS(text), TextToSpeech.QUEUE_FLUSH, null, "page")
    }

    // Language switch
    LaunchedEffect(isKannada, isTtsReady) {
        if (!isTtsReady) return@LaunchedEffect
        val locale = if (isKannada) Locale("kn", "IN") else Locale.US
        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(
                context,
                "Kannada voice not installed. Go to Settings → Text-to-Speech.",
                Toast.LENGTH_LONG
            ).show()
            tts?.setLanguage(Locale.US)
        }
        if (isSpeaking) { tts?.stop(); isSpeaking = false }
    }

    DisposableEffect(Unit) { onDispose { tts?.stop(); tts?.shutdown() } }

    // ── Root: Kindle sepia background ─────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KindleBg)
            .clickable(
                indication        = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { showControls = !showControls }
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ══════════════════════════════════════════════════════════════════
            //  TOP CHROME — slide away on tap
            // ══════════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible         = showControls,
                enter           = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit            = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Surface(
                    color           = KindleBgDark,
                    shadowElevation = 2.dp,
                    modifier        = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Back
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = KindleAccent
                            )
                        }

                        // Title
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier            = Modifier.weight(1f)
                        ) {
                            Text(
                                "Hero Story",
                                fontFamily = ReadingFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp,
                                color      = KindleInk
                            )
                            Text(
                                districtName,
                                fontSize = 11.sp,
                                color    = KindleInkMuted,
                                fontFamily = ReadingFamily
                            )
                        }

                        // Language toggle
                        LanguageTogglePill(isKannada = isKannada, onToggle = { isKannada = it })
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            //  READING PROGRESS BAR — always visible, very thin
            // ══════════════════════════════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(KindleDivider)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animReadFraction)
                        .fillMaxHeight()
                        .background(KindleAccent)
                )
            }

            // ══════════════════════════════════════════════════════════════════
            //  PAGER — the reading canvas
            // ══════════════════════════════════════════════════════════════════
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Decorative drop-cap style page number
                        Text(
                            text       = "— ${page + 1} —",
                            fontFamily = ReadingFamily,
                            fontSize   = 11.sp,
                            color      = KindleInkMuted,
                            modifier   = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp),
                            textAlign  = TextAlign.Center,
                            letterSpacing = 3.sp
                        )

                        // Opening decorative glyph on first page
                        if (page == 0) {
                            Text(
                                "❧",
                                fontSize   = 28.sp,
                                color      = KindleAccent.copy(alpha = 0.6f),
                                modifier   = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                textAlign  = TextAlign.Center
                            )
                        }

                        // Story text
                        Text(
                            text       = pages.getOrNull(page) ?: "",
                            fontFamily = ReadingFamily,
                            fontSize   = fontSize.sp,
                            lineHeight  = (fontSize * 1.85f).sp,
                            color      = KindleInk,
                            textAlign  = TextAlign.Justify
                        )

                        Spacer(Modifier.height(32.dp))

                        // End-of-page ornament
                        Text(
                            text      = "✦  ✦  ✦",
                            modifier  = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color     = KindleDivider,
                            fontSize  = 16.sp
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            //  LAST PAGE ACTION BUTTONS — always shown when on last page
            // ══════════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = isLastPage && pages.isNotEmpty(),
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    color           = KindleBgDark,
                    modifier        = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        heroLocation?.let { loc ->
                            OutlinedButton(
                                onClick = {
                                    val uri = "geo:${loc.lat},${loc.lng}?q=${loc.lat},${loc.lng}(${loc.name})".toUri()
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape    = RoundedCornerShape(14.dp),
                                border   = BorderStroke(1.5.dp, KindleAccent)
                            ) {
                                Icon(
                                    Icons.Rounded.LocationOn, null,
                                    tint     = KindleAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Visit Memorial",
                                    fontWeight = FontWeight.SemiBold,
                                    color      = KindleAccent
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val enc = java.net.URLEncoder.encode(districtName, "UTF-8")
                                navController.navigate("quiz/$heroId/$enc")
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape    = RoundedCornerShape(14.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = KindleAccent)
                        ) {
                            Icon(
                                Icons.Rounded.EmojiEvents, null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Take the Quiz",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp
                            )
                        }
                    }
                }
            }

            // ══════════════════════════════════════════════════════════════════
            //  BOTTOM CHROME — slide away on tap
            // ══════════════════════════════════════════════════════════════════
            AnimatedVisibility(
                visible = showControls,
                enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Surface(
                    color           = KindleBgDark,
                    shadowElevation = 4.dp,
                    modifier        = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Page indicator + font size controls
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            // Font size adjuster
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SmallKindleButton(
                                    label   = "A−",
                                    onClick = { if (fontSize > 14f) fontSize -= 2f }
                                )
                                SmallKindleButton(
                                    label   = "A+",
                                    onClick = { if (fontSize < 30f) fontSize += 2f }
                                )
                            }

                            // Page X of Y
                            Text(
                                "${currentPage + 1} of $totalPages",
                                fontFamily = ReadingFamily,
                                fontSize   = 13.sp,
                                color      = KindleInkMuted
                            )

                            // Narration button
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    onClick = {
                                        isSpeaking = !isSpeaking
                                        if (!isSpeaking) tts?.stop()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSpeaking)
                                        KindleAccent.copy(alpha = 0.15f)
                                    else
                                        KindleDivider.copy(alpha = 0.6f)
                                ) {
                                    Row(
                                        modifier              = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                                    ) {
                                        Icon(
                                            if (isSpeaking) Icons.Rounded.VolumeOff
                                            else            Icons.Rounded.VolumeUp,
                                            contentDescription = null,
                                            tint     = KindleAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            if (isSpeaking) "Pause" else "Listen",
                                            fontSize   = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color      = KindleAccent
                                        )
                                    }
                                }
                            }
                        }

                        // Dot page indicator
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            val dotCount = totalPages.coerceAtMost(12)
                            repeat(dotCount) { i ->
                                val isCurrent = i == (currentPage.coerceAtMost(dotCount - 1))
                                val dotSize by animateDpAsState(
                                    targetValue   = if (isCurrent) 8.dp else 5.dp,
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                                    label         = "dot"
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 3.dp)
                                        .size(dotSize)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCurrent) KindleAccent else KindleDivider
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Small font-size button ────────────────────────────────────────────────────
@Composable
private fun SmallKindleButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape   = RoundedCornerShape(8.dp),
        color   = KindleDivider.copy(alpha = 0.7f),
        modifier = Modifier.size(width = 38.dp, height = 30.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold,
                color      = KindleAccent
            )
        }
    }
}

// ── Language toggle ───────────────────────────────────────────────────────────
@Composable
private fun LanguageTogglePill(isKannada: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = KindleDivider.copy(alpha = 0.6f)
    ) {
        Row(
            modifier          = Modifier.padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(false to "EN", true to "ಕನ್ನಡ").forEach { (kannada, label) ->
                val active = isKannada == kannada
                Surface(
                    modifier = Modifier.clickable(
                        indication        = null,
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                    ) { onToggle(kannada) },
                    shape = RoundedCornerShape(17.dp),
                    color = if (active) KindleBg else Color.Transparent
                ) {
                    Text(
                        label,
                        modifier   = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        fontSize   = 12.sp,
                        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        color      = if (active) KindleInk else KindleInkMuted
                    )
                }
            }
        }
    }
}