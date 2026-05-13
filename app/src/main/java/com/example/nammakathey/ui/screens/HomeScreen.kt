package com.example.nammakathey.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammakathey.R
import com.example.nammakathey.data.local.loadHeroes
import com.example.nammakathey.ui.components.BadgeData
import com.example.nammakathey.ui.components.DistrictBadge
import com.example.nammakathey.ui.components.districtColors
import com.example.nammakathey.viewmodel.AppViewModel

// ── Motivational messages cycling by progress level ───────────────────────────
private fun motivationalMessage(fraction: Float): Pair<String, String> = when {
    fraction == 0f   -> "✨" to "Start your journey — discover Karnataka's first hero!"
    fraction < 0.25f -> "🌱" to "Great start! Keep exploring more district heroes."
    fraction < 0.5f  -> "🔥" to "You're on a roll! Almost halfway there."
    fraction < 0.75f -> "⚡" to "Incredible! The badges are piling up — don't stop now."
    fraction < 1f    -> "🏆" to "So close! Just a few more heroes to become a legend."
    else             -> "🎉" to "You've explored every hero in Karnataka. Legendary!"
}

@Composable
fun HomeScreen(
    navController: NavController,
    appViewModel: AppViewModel
) {
    val context = LocalContext.current
    val completedHeroes by appViewModel.completedHeroes.collectAsState()
    val data = remember { loadHeroes(context) }

    // ── Computed stats ─────────────────────────────────────────────────────────
    val totalHeroes = data.districts.sumOf { it.heroes.size }
    val completedHeroCount = completedHeroes.size

    val completedDistrictCount = data.districts.count { d ->
        d.heroes.isNotEmpty() && d.heroes.all { completedHeroes.contains(it.id) }
    }
    val totalDistricts = data.districts.size

    val heroFraction = completedHeroCount.toFloat() / totalHeroes.coerceAtLeast(1)
    val districtFraction = completedDistrictCount.toFloat() / totalDistricts.coerceAtLeast(1)

    // ── In-progress districts (partially done) ─────────────────────────────────
    val inProgressBadges = data.districts
        .mapIndexed { index, district ->
            val done = district.heroes.count { completedHeroes.contains(it.id) }
            val code = district.name_en
                .split(" ").take(2)
                .joinToString("") { it.take(1).uppercase() }
                .take(3)
            BadgeData(
                districtCode    = code,
                districtName    = district.name_en,
                completedHeroes = done,
                totalHeroes     = district.heroes.size,
                colorIndex      = index
            )
        }
        .filter { it.isInProgress || it.isUnlocked }
        .sortedByDescending { it.progress }
        .take(8)

    val (motivationIcon, motivationText) = motivationalMessage(heroFraction)

    // ── Scroll state ───────────────────────────────────────────────────────────
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {

        // ╔════════════════════════════════════════════════════════╗
        // ║  HERO BANNER                                           ║
        // ╚════════════════════════════════════════════════════════╝
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1A237E), Color(0xFF4CAF82)),
                        start  = Offset(0f, 0f),
                        end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
        ) {
            // Decorative circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = Color.White.copy(alpha = 0.05f), radius = 160.dp.toPx(), center = Offset(size.width * 0.85f, -30f))
                drawCircle(color = Color.White.copy(alpha = 0.07f), radius = 100.dp.toPx(), center = Offset(30f, size.height * 1.1f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text       = "ನಮ್ಮ ಕಥೆ",
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color      = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 2.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text       = "Discover Karnataka's\nLocal Heroes",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White,
                        lineHeight = 30.sp
                    )
                }

                // Overall hero progress bar inside banner
                Column {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text      = "Overall Progress",
                            fontSize  = 12.sp,
                            color     = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text      = "$completedHeroCount / $totalHeroes heroes",
                            fontSize  = 12.sp,
                            color     = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    AnimatedProgressBar(
                        fraction       = heroFraction,
                        trackColor     = Color.White.copy(alpha = 0.25f),
                        progressColor  = Color(0xFFFAC775),
                        height         = 8.dp,
                        cornerRadius   = 50.dp
                    )
                }
            }
        }

        // ╔════════════════════════════════════════════════════════╗
        // ║  EXPLORE BUTTON                                        ║
        // ╚════════════════════════════════════════════════════════╝
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-18).dp)
        ) {
            Button(
                onClick  = { navController.navigate("district") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFAC775),
                    contentColor   = Color(0xFF2C1A00)
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.map),
                    contentDescription = "Map Icon",
                    modifier = Modifier.size(20.dp),
                    )
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = "Explore District Map",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }
        }

        // ╔════════════════════════════════════════════════════════╗
        // ║  STATS CARDS                                           ║
        // ╚════════════════════════════════════════════════════════╝
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCard(
                modifier  = Modifier.weight(1f),
                icon      = "🦸",
                label     = "Heroes",
                current   = completedHeroCount,
                total     = totalHeroes,
                fraction  = heroFraction,
                barColor  = Color(0xFF5DCAA5)
            )
            StatsCard(
                modifier  = Modifier.weight(1f),
                icon      = "🗺️",
                label     = "Districts",
                current   = completedDistrictCount,
                total     = totalDistricts,
                fraction  = districtFraction,
                barColor  = Color(0xFFAFA9EC)
            )
            StatsCard(
                modifier  = Modifier.weight(1f),
                icon      = "🎖",
                label     = "Badges",
                current   = completedDistrictCount,
                total     = totalDistricts,
                fraction  = districtFraction,
                barColor  = Color(0xFFFAC775)
            )
        }

        Spacer(Modifier.height(24.dp))

        // ╔════════════════════════════════════════════════════════╗
        // ║  DISTRICT PROGRESS — horizontal badge strip            ║
        // ╚════════════════════════════════════════════════════════╝
        if (inProgressBadges.isNotEmpty()) {
            SectionHeader(
                title    = "Active Districts",
                subtitle = "Tap a badge to continue",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            LazyRow(
                contentPadding        = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(inProgressBadges) { badge ->
                    ActiveDistrictCard(
                        badge       = badge,
                        onClick     = { navController.navigate("district") }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // ╔════════════════════════════════════════════════════════╗
        // ║  PER-DISTRICT PROGRESS LIST (top 5)                    ║
        // ╚════════════════════════════════════════════════════════╝
        SectionHeader(
            title    = "District Breakdown",
            subtitle = "Your hero count per district",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                data.districts
                    .mapIndexed { i, d ->
                        val done = d.heroes.count { completedHeroes.contains(it.id) }
                        Triple(d, done, i)
                    }
                    .sortedByDescending { (_, done, _) -> done }
                    .take(6)
                    .forEachIndexed { listIndex, (district, done, colorIdx) ->
                        DistrictProgressRow(
                            name       = district.name_en,
                            done       = done,
                            total      = district.heroes.size,
                            barColor   = districtColors.getOrElse(colorIdx) { Color(0xFF5DCAA5) }
                        )
                        if (listIndex < 5) {
                            HorizontalDivider(
                                modifier  = Modifier.padding(horizontal = 16.dp),
                                color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                thickness = 0.5.dp
                            )
                        }
                    }

                // "See all" button
                TextButton(
                    onClick  = { navController.navigate("profile") },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 4.dp)
                ) {
                    Text("See all districts →", fontSize = 13.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ╔════════════════════════════════════════════════════════╗
        // ║  MOTIVATIONAL CARD                                     ║
        // ╚════════════════════════════════════════════════════════╝
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape  = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A237E).copy(alpha = 0.08f)
            )
        ) {
            Row(
                modifier            = Modifier.padding(16.dp),
                verticalAlignment   = Alignment.CenterVertically
            ) {
                Text(motivationIcon, fontSize = 36.sp)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text       = "Keep Going!",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text     = motivationText,
                        fontSize = 13.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ── Animated horizontal progress bar ──────────────────────────────────────────
@Composable
fun AnimatedProgressBar(
    fraction      : Float,
    trackColor    : Color,
    progressColor : Color,
    height        : Dp           = 8.dp,
    cornerRadius  : Dp           = 50.dp,
    modifier      : Modifier     = Modifier
) {
    val animFraction by animateFloatAsState(
        targetValue  = fraction.coerceIn(0f, 1f),
        animationSpec = tween(900, easing = EaseOutCubic),
        label        = "progress"
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(trackColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animFraction)
                .fillMaxHeight()
                .clip(RoundedCornerShape(cornerRadius))
                .background(progressColor)
        )
    }
}

// ── Small stat card (3 in a row) ──────────────────────────────────────────────
@Composable
private fun StatsCard(
    modifier : Modifier,
    icon     : String,
    label    : String,
    current  : Int,
    total    : Int,
    fraction : Float,
    barColor : Color
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        )
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                text       = "$current",
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text     = "/ $total",
                fontSize = 11.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            AnimatedProgressBar(
                fraction      = fraction,
                trackColor    = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                progressColor = barColor,
                height        = 5.dp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text     = label,
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Active district badge card (in LazyRow) ────────────────────────────────────
@Composable
private fun ActiveDistrictCard(
    badge   : BadgeData,
    onClick : () -> Unit
) {
    val badgeColor = com.example.nammakathey.ui.components.districtColors
        .getOrElse(badge.colorIndex) { Color(0xFF5DCAA5) }

    Card(
        modifier = Modifier
            .width(110.dp)
            .clickable(onClick = onClick),
        shape  = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = badgeColor.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier            = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DistrictBadge(badge = badge, size = 64.dp)
            Spacer(Modifier.height(8.dp))
            Text(
                text      = badge.districtName,
                fontSize  = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                color     = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text     = "${badge.completedHeroes}/${badge.totalHeroes}",
                fontSize = 10.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            AnimatedProgressBar(
                fraction      = badge.progress,
                trackColor    = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                progressColor = badgeColor,
                height        = 5.dp
            )
        }
    }
}

// ── Per-district progress row ─────────────────────────────────────────────────
@Composable
private fun DistrictProgressRow(
    name     : String,
    done     : Int,
    total    : Int,
    barColor : Color
) {
    val fraction = if (total > 0) done.toFloat() / total else 0f
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text      = name,
                fontSize  = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                modifier  = Modifier.weight(1f)
            )
            Text(
                text     = "$done / $total",
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(5.dp))
        AnimatedProgressBar(
            fraction      = fraction,
            trackColor    = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
            progressColor = if (done >= total && total > 0) Color(0xFF4CAF82) else barColor,
            height        = 6.dp
        )
    }
}

// ── Section header with title + subtitle ──────────────────────────────────────
@Composable
private fun SectionHeader(
    title    : String,
    subtitle : String,
    modifier : Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text       = title,
            fontSize   = 17.sp,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text     = subtitle,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}