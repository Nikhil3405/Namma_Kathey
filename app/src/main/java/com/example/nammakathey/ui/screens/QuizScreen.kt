package com.example.nammakathey.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nammakathey.data.local.loadHeroes
import com.example.nammakathey.viewmodel.AppViewModel
import java.net.URLEncoder
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign

private val NavyBlue  = Color(0xFF1A237E)
private val TealGreen = Color(0xFF4CAF82)
private val AmberWarm = Color(0xFFFAC775)
private val LightBg   = Color(0xFFF7F8FC)
private val MutedText = Color(0xFF5F5E5A)
private val CoralRed  = Color(0xFFEF5350)

@Composable
fun QuizScreen(
    heroId: String,
    districtName: String,
    appViewModel: AppViewModel,
    navController: NavController
) {

    val context = LocalContext.current

    var questions by remember { mutableStateOf(listOf<com.example.nammakathey.data.model.Quiz>()) }

    var currentQuestion by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }

    var selectedIndex by remember { mutableIntStateOf(-1) }

    var isKannada by remember { mutableStateOf(false) }

    var quizFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val data = loadHeroes(context)

        val hero = data.districts
            .flatMap { it.heroes }
            .find { it.id == heroId }

        questions = hero?.quiz ?: emptyList()
    }

    if (questions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavyBlue)
        }
        return
    }

    // 🔥 RESULT SCREEN
    if (quizFinished) {

        val passed = score >= 2

        if (passed) {
            appViewModel.markHeroCompleted(heroId)
        }

        // 🔥 Animation
        val scale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing
            ),
            label = ""
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NavyBlue,
                            TealGreen
                        )
                    )
                )
                .padding(24.dp),

            contentAlignment = Alignment.Center
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),

                shape = RoundedCornerShape(28.dp),

                elevation = CardDefaults.cardElevation(10.dp),

                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),

                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 🏆 Trophy / Result Emoji
                    Text(
                        text =
                            if (passed) "🏆"
                            else "📚",

                        fontSize = 72.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text =
                            if (passed)
                                "Amazing Work!"
                            else
                                "Keep Trying!",

                        style = MaterialTheme.typography.headlineMedium,

                        fontWeight = FontWeight.ExtraBold,

                        color =
                            if (passed)
                                TealGreen
                            else
                                CoralRed
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text =
                            if (passed)
                                "You successfully completed this hero challenge!"
                            else
                                "Read the story once more and try again.",

                        textAlign = TextAlign.Center,

                        color = MutedText,

                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 📊 Score Circle
                    Surface(
                        shape = CircleShape,
                        color =
                            if (passed)
                                TealGreen.copy(alpha = 0.12f)
                            else
                                CoralRed.copy(alpha = 0.12f)
                    ) {

                        Column(
                            modifier = Modifier.padding(
                                horizontal = 28.dp,
                                vertical = 20.dp
                            ),

                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                "Score",
                                color = MutedText
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                "$score / ${questions.size}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // 🔁 Retry Button
                    OutlinedButton(
                        onClick = {
                            currentQuestion = 0
                            score = 0
                            selectedIndex = -1
                            quizFinished = false
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),

                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Text(
                            "Retry Quiz",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 🔙 Back Button
                    Button(
                        onClick = {

                            val encoded = URLEncoder.encode(
                                districtName,
                                "UTF-8"
                            )

                            navController.navigate(
                                "hero_list/$encoded"
                            ) {
                                popUpTo("hero_list/$encoded") {
                                    inclusive = true
                                }
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),

                        shape = RoundedCornerShape(18.dp),

                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealGreen
                        )
                    ) {

                        Text(
                            "Back to Heroes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        return
    }

    val q = questions[currentQuestion]
    val options = if (isKannada) q.options_kn else q.options_en

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
    ) {

        // 🔥 Language Toggle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(NavyBlue, TealGreen)
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.18f)
                    ) {
                        Text(
                            "Q ${currentQuestion + 1} / ${questions.size}",
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 5.dp
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "EN",
                            color = Color.White
                        )

                        Switch(
                            checked = isKannada,
                            onCheckedChange = {
                                isKannada = it
                            }
                        )

                        Text(
                            "ಕನ್ನಡ",
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Quiz Challenge",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = {
                        (currentQuestion + 1).toFloat() / questions.size
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = AmberWarm,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isKannada) q.question_kn else q.question_en,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 OPTIONS WITH HIGHLIGHT
        options.forEachIndexed { index, option ->

            val isSelected = selectedIndex == index

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        selectedIndex = index
                    },

                shape = RoundedCornerShape(16.dp),

                colors = CardDefaults.cardColors(
                    containerColor =
                        if (isSelected)
                            NavyBlue.copy(alpha = 0.1f)
                        else
                            Color.White
                ),

                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected)
                        NavyBlue
                    else
                        Color(0xFFE0E0E0)
                )
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected)
                                    NavyBlue
                                else
                                    Color(0xFFEAEAEA)
                            ),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            ('A' + index).toString(),
                            color =
                                if (isSelected)
                                    Color.White
                                else
                                    MutedText
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        option,
                        color = NavyBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 NEXT BUTTON
        Button(
            onClick = {

                if (selectedIndex == -1) return@Button

                if (selectedIndex == q.answer) {
                    score++
                }

                if (currentQuestion < questions.size - 1) {

                    currentQuestion++
                    selectedIndex = -1

                } else {

                    quizFinished = true
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .height(55.dp),

            shape = RoundedCornerShape(18.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = TealGreen
            )
        ) {

            Text(
                if (currentQuestion == questions.size - 1)
                    "Finish Quiz"
                else
                    "Next Question",

                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}