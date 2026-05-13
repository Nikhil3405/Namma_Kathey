package com.example.nammakathey.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakathey.data.local.loadHeroes
import com.example.nammakathey.ui.components.BadgeData
import com.example.nammakathey.ui.components.DistrictBadge
import com.example.nammakathey.ui.components.DistrictBadgeRow
import com.example.nammakathey.viewmodel.AppViewModel

// 1. MUST DEFINE ENUM HERE (OR TOP LEVEL)
enum class BadgeDisplayMode { GRID, LIST }

@OptIn(ExperimentalMaterial3Api::class) // 2. NEEDED FOR SEGMENTED BUTTONS
@Composable
fun ProfileScreen(appViewModel: AppViewModel) {
    val context = LocalContext.current
    val completedHeroes by appViewModel.completedHeroes.collectAsState()
    val data = remember { loadHeroes(context) }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Stats Logic
    val totalHeroes = data.districts.sumOf { it.heroes.size }
    val completedHeroCount = data.districts.flatMap { it.heroes }.count { completedHeroes.contains(it.id) }
    val completedDistrictCount = data.districts.count { d -> d.heroes.isNotEmpty() && d.heroes.all { completedHeroes.contains(it.id) } }

    val badges: List<BadgeData> = data.districts.mapIndexed { index, district ->
        val completed = district.heroes.count { completedHeroes.contains(it.id) }
        val code = district.name_en.split(" ").take(2).joinToString("") { it.take(1).uppercase() }.take(3)
        BadgeData(code, district.name_en, completed, district.heroes.size, index)
    }.sortedWith(compareByDescending<BadgeData> { it.isUnlocked }.thenByDescending { it.progress })

    // 3. SPECIFY TYPE EXPLICITLY TO FIX INFERENCE ERROR
    var displayMode by remember { mutableStateOf<BadgeDisplayMode>(BadgeDisplayMode.GRID) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(Modifier.height(24.dp)) }

            item {
                Column {
                    Text(
                        text = "Your Journey",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-1).sp
                        )
                    )
                    Text(
                        text = "Collecting the legends of Karnataka",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                AnimatedVisibility(visible = visible, enter = fadeIn() + expandVertically()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            label = "Heroes Found",
                            value = "$completedHeroCount/$totalHeroes",
                            fraction = completedHeroCount.toFloat() / totalHeroes.coerceAtLeast(1),
                            icon = Icons.Rounded.EmojiEvents,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Districts Won",
                            value = "$completedDistrictCount/${data.districts.size}",
                            fraction = completedDistrictCount.toFloat() / data.districts.size.coerceAtLeast(1),
                            icon = Icons.Rounded.Map,
                            color = Color(0xFF2EBD85),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "District Badges",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    // 4. FIXED SEGMENTED BUTTON ROW
                    SingleChoiceSegmentedButtonRow {
                        SegmentedButton(
                            selected = displayMode == BadgeDisplayMode.GRID,
                            onClick = { displayMode = BadgeDisplayMode.GRID },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            label = { Text("Grid") },
                            icon = { Icon(Icons.Default.GridView, null, modifier = Modifier.size(16.dp)) }
                        )
                        SegmentedButton(
                            selected = displayMode == BadgeDisplayMode.LIST,
                            onClick = { displayMode = BadgeDisplayMode.LIST },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            label = { Text("List") },
                            icon = { Icon(Icons.Default.List, null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }

            if (displayMode == BadgeDisplayMode.GRID) {
                val rows = badges.chunked(3)
                itemsIndexed(rows) { _, rowBadges ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowBadges.forEach { badge ->
                            AnimatedBadgeItem(badge, Modifier.weight(1f))
                        }
                        repeat(3 - rowBadges.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            } else {
                itemsIndexed(badges) { index, badge ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(300, delayMillis = index * 50)) +
                                slideInVertically(tween(300, delayMillis = index * 50)) { it / 2 }
                    ) {
                        DistrictBadgeRow(badge = badge)
                    }
                }
            }

            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

// ── Supporting Composable ─────────────────────────────────────────────────────

@Composable
private fun AnimatedBadgeItem(badge: BadgeData, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(if (badge.isUnlocked) 8.dp else 0.dp, CircleShape)
                .background(
                    if (badge.isUnlocked) Color.Transparent else Color.Black.copy(alpha = 0.05f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            DistrictBadge(badge = badge, size = 72.dp)

            if (!badge.isUnlocked && badge.progress > 0) {
                CircularProgressIndicator(
                    progress = { badge.progress },
                    modifier = Modifier.size(76.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    strokeWidth = 2.dp,
                    trackColor = Color.Transparent,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = badge.districtName,
            fontSize = 11.sp,
            fontWeight = if (badge.isUnlocked) FontWeight.Bold else FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 12.sp,
            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    fraction: Float,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(1200, easing = EaseOutExpo),
        label = "progress"
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .size(28.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape)
                    .padding(4.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(listOf(color.copy(alpha = 0.7f), color))
                        )
                )
            }
        }
    }
}