package com.example.nammakathey.ui.screens

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nammakathey.data.local.loadHeroes
import androidx.core.net.toUri
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

private val NavyBlue  = Color(0xFF1A237E)
private val TealGreen = Color(0xFF4CAF82)
private val AmberWarm = Color(0xFFFAC775)
private val LightBg   = Color(0xFFF7F8FC)
private val MutedText = Color(0xFF5F5E5A)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoryScreen(
    heroId: String,
    districtName: String,
    navController: NavController
) {

    fun cleanTextForTTS(text: String): String {
        return text
            .replace(".", " ")
            .replace(",", " ")
            .replace(":", " ")
            .replace(";", " ")
            .replace("!", " ")
            .replace("?", " ")
            .replace("(", " ")
            .replace(")", " ")
            .replace("[", " ")
            .replace("]", " ")
            .replace("{", " ")
            .replace("}", " ")
            .replace("|", " ")
            .replace("\\", " ")
            .replace("/", " ")
            .replace("'", " ")
            .replace("\"", " ")
            .replace("\u201C", " ")
            .replace("\u201D", " ")
    }


    val context = LocalContext.current

    var storyPages by remember { mutableStateOf(listOf<String>()) }
    var storyPagesKn by remember { mutableStateOf(listOf<String>()) }

    var isKannada by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) } // FIX 4: guard against use-before-init

    var heroLocation by remember { mutableStateOf<com.example.nammakathey.data.model.Location?>(null) }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isSpeaking by remember { mutableStateOf(false) }

    val pages = if (isKannada) storyPagesKn else storyPages // FIX 6: hoisted above Column

    // FIX 6: pagerState hoisted above Column so it is stable across recompositions
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
//    var triggerNextPage by remember { mutableStateOf(false) }


    fun estimateSpeechDuration(text: String, isKannada: Boolean): Long {

        val words = text.split(" ").size

        return if (isKannada) {
            words * 850L   // 🔥 slower for Kannada
        } else {
            words * 550L
        }
    }


    // Load data
    LaunchedEffect(Unit) {
        val data = loadHeroes(context)

        val hero = data.districts
            .flatMap { it.heroes }
            .find { it.id == heroId }

        storyPages = hero?.story?.en ?: emptyList()
        storyPagesKn = hero?.story?.kn ?: emptyList()
        heroLocation = hero?.location

        // FIX 1: TTS init does not capture isKannada — language is set dynamically later
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {

                tts?.setSpeechRate(if (isKannada) 0.80f else 0.65f)
                tts?.setPitch(1.0f)
                tts?.setLanguage(Locale.US)

                isTtsReady = true
            }
        }
    }

    LaunchedEffect(isSpeaking, pagerState.currentPage) {

        if (!isSpeaking) return@LaunchedEffect

        val text = pages.getOrNull(
            pagerState.currentPage
        ) ?: return@LaunchedEffect

        val cleanText = cleanTextForTTS(text)

        tts?.speak(
            cleanText,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "current_page"
        )
    }

    // FIX 3: Reactively update TTS language whenever isKannada changes
    LaunchedEffect(isKannada, isTtsReady) {
        if (!isTtsReady) return@LaunchedEffect

        val locale = if (isKannada) Locale("kn", "IN") else Locale.US
        val result = tts?.setLanguage(locale)

        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(
                context,
                "Kannada voice not installed. Please install it in Settings → Text-to-Speech.",
                Toast.LENGTH_LONG
            ).show()
            tts?.setLanguage(Locale.US)
        }

        // FIX 5: stop any ongoing speech when language is toggled mid-session
        if (isSpeaking) {
            tts?.stop()
            isSpeaking = false
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            NavyBlue,
                            TealGreen
                        )
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {

                        Text(
                            text = "Page ${pagerState.currentPage + 1} / ${pages.size}",
                            modifier = Modifier.padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),
                            color = Color.White
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text("EN", color = Color.White)

                        Switch(
                            checked = isKannada,
                            onCheckedChange = {
                                isKannada = it
                            }
                        )

                        Text("ಕನ್ನಡ", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Hero Story",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {

                        isSpeaking = !isSpeaking

                        if (!isSpeaking) {
                            tts?.stop()
                        }
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberWarm
                    ),

                    shape = RoundedCornerShape(18.dp)
                ) {

                    Text(
                        if (isSpeaking)
                            "⏸ Pause Narration"
                        else
                            "🔊 Play Narration",

                        color = NavyBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 18.dp,
                        bottom = 12.dp
                    )
            ) {

                Card(
                    modifier = Modifier.fillMaxSize(),

                    shape = RoundedCornerShape(30.dp),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(28.dp)
                            .verticalScroll(rememberScrollState())
                    ) {

                        // 📖 Story Text
                        Text(
                            text = pages.getOrNull(page) ?: "",

                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 24.sp,
                                lineHeight = 42.sp
                            ),

                            color = NavyBlue
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // 🌟 Decorative Ending
                        Text(
                            text = "✦ ✦ ✦",

                            modifier = Modifier.fillMaxWidth(),

                            textAlign = TextAlign.Center,

                            color = TealGreen.copy(alpha = 0.5f),

                            fontSize = 20.sp
                        )
                    }
                }
            }
        }

        val encodedDistrict = java.net.URLEncoder.encode(districtName, "UTF-8")
        if (pagerState.currentPage == pages.size - 1 && pages.isNotEmpty()) {

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                heroLocation?.let { location ->

                    OutlinedButton(
                        onClick = {

                            val uri =
                                "geo:${location.lat},${location.lng}?q=${location.lat},${location.lng}(${location.name})"
                                    .toUri()

                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, uri)
                            )
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Text(
                            "📍 Visit Memorial",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Button(
                    onClick = {

                        val encodedDistrict =
                            java.net.URLEncoder.encode(
                                districtName,
                                "UTF-8"
                            )

                        navController.navigate(
                            "quiz/$heroId/$encodedDistrict"
                        )
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),

                    shape = RoundedCornerShape(18.dp),

                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealGreen
                    )
                ) {

                    Text(
                        "Start Quiz Challenge",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}