package com.example.nammakathey.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nammakathey.data.local.loadHeroes
import com.example.nammakathey.viewmodel.AppViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val NavyBlue  = Color(0xFF1A237E)
private val TealGreen = Color(0xFF4CAF82)
private val AmberWarm = Color(0xFFFAC775)
private val LightBg   = Color(0xFFF7F8FC)
private val MutedText = Color(0xFF5F5E5A)

@Composable
fun HeroListScreen(districtName: String, navController: NavController,appViewModel: AppViewModel) {

    val context = LocalContext.current

    var heroes by remember { mutableStateOf(listOf<com.example.nammakathey.data.model.Hero>()) }
    val decodedDistrictName = remember(districtName) {
        java.net.URLDecoder.decode(districtName, "UTF-8")
    }
    LaunchedEffect(Unit) {
        val data = loadHeroes(context)

        val district = data.districts.find {
            it.name_en == decodedDistrictName
        }

        heroes = district?.heroes ?: emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FB))
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .background(
                    Brush.linearGradient(
                        colors = listOf(
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
                    .padding(24.dp)
            ) {

                Text(
                    text = "District Heroes",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = decodedDistrictName,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color.White.copy(alpha = 0.18f)
                ) {

                    Text(
                        text = "Discover legendary heroes",
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        ),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {

            items(heroes) { hero ->
                val completedHeroes by appViewModel.completedHeroes.collectAsState()

                val isCompleted = completedHeroes.contains(hero.id)

                val imageRes = context.resources.getIdentifier(
                    hero.image,
                    "drawable",
                    context.packageName
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {

                            val encoded = java.net.URLEncoder.encode(
                                decodedDistrictName,
                                "UTF-8"
                            )

                            navController.navigate(
                                "story/${hero.id}/$encoded"
                            )
                        },

                    shape = RoundedCornerShape(28.dp),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),

                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {

                    Column {

                        // 🖼 HERO IMAGE
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {

                            if (imageRes != 0) {

                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = hero.name_en,
                                    modifier = Modifier.fillMaxSize()
                                )

                            } else {

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.linearGradient(
                                                listOf(
                                                    NavyBlue,
                                                    TealGreen
                                                )
                                            )
                                        ),

                                    contentAlignment = Alignment.Center
                                ) {

                                    Text(
                                        hero.name_en.take(1),
                                        color = Color.White,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // 🏆 Completion badge
                            if (isCompleted) {

                                Surface(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(14.dp),

                                    shape = CircleShape,

                                    color = AmberWarm
                                ) {

                                    Text(
                                        "🏆",
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Text(
                                text = hero.name_en,

                                fontSize = 28.sp,

                                fontWeight = FontWeight.ExtraBold,

                                color = NavyBlue
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = hero.short_desc_en,

                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 28.sp
                                ),

                                color = MutedText
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Surface(
                                shape = RoundedCornerShape(50.dp),

                                color = NavyBlue.copy(alpha = 0.08f)
                            ) {

                                Text(
                                    text = "Read Story →",

                                    modifier = Modifier.padding(
                                        horizontal = 18.dp,
                                        vertical = 10.dp
                                    ),

                                    color = NavyBlue,

                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}