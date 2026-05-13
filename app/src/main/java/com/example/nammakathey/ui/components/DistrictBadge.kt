package com.example.nammakathey.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Per-district color palette ────────────────────────────────────────────────
// Each district gets a unique unlocked color. Add/reorder to match your districts.
val districtColors = listOf(
    Color(0xFFFAC775), // Amber      – Bengaluru Urban
    Color(0xFF5DCAA5), // Teal       – Dakshina Kannada
    Color(0xFFAFA9EC), // Lavender   – Mysuru
    Color(0xFFF09595), // Coral Pink – Udupi
    Color(0xFF85B7EB), // Sky Blue   – Dharwad
    Color(0xFFA8D8A8), // Sage Green – Belagavi
    Color(0xFFFFB347), // Orange     – Tumakuru
    Color(0xFFDDA0DD), // Plum       – Shivamogga
    Color(0xFF87CEEB), // Light Blue – Hassan
    Color(0xFFF4A460), // Sandy      – Kalaburagi
    Color(0xFF98FB98), // Pale Green – Mandya
    Color(0xFFFFD700), // Gold       – Raichur
    Color(0xFFDB7093), // PaleVioletRed – Bidar
    Color(0xFF66CDAA), // MedAquamarine – Uttara Kannada
    Color(0xFFFFA07A), // LightSalmon   – Chitradurga
    Color(0xFFBA55D3), // MedOrchid     – Chikkaballapura
    Color(0xFF4682B4), // SteelBlue     – Vijayapura
    Color(0xFFFF6B6B), // Coral Red     – Bagalkot
    Color(0xFF48D1CC), // MedTurquoise  – Kodagu
    Color(0xFFDEB887), // Burlywood     – Chamarajanagar
    Color(0xFF9370DB), // MedPurple     – Gadag
    Color(0xFF3CB371), // MedSeaGreen   – Koppal
    Color(0xFFCD853F), // Peru          – Haveri
    Color(0xFF20B2AA), // LightSeaGreen – Yadgir
    Color(0xFFFF69B4), // HotPink       – Davangere
    Color(0xFF6495ED), // CornflowerBlue– Chikkamagaluru
    Color(0xFFF0E68C), // Khaki         – Ballari
    Color(0xFFBC8F8F), // RosyBrown     – Ramanagara
    Color(0xFF7B68EE), // MedSlateBlue  – Bengaluru Rural
    Color(0xFF00CED1), // DarkTurquoise – Vijayanagara
    Color(0xFFFF8C00), // DarkOrange    – Yadgir (extra)
)

// Derives a dark text/border color from the badge fill color
fun badgeTextColor(fill: Color): Color {
    // Darken the hue significantly for readable text on a light fill
    return Color(
        red   = (fill.red   * 0.35f).coerceIn(0f, 1f),
        green = (fill.green * 0.35f).coerceIn(0f, 1f),
        blue  = (fill.blue  * 0.35f).coerceIn(0f, 1f),
        alpha = 1f
    )
}

// ─── Data model ────────────────────────────────────────────────────────────────
data class BadgeData(
    val districtCode: String,       // 2–3 letter code, e.g. "BU", "MY", "DK"
    val districtName: String,       // Full name for accessibility / tooltip
    val completedHeroes: Int,
    val totalHeroes: Int,
    val colorIndex: Int             // Index into districtColors
) {
    val isUnlocked: Boolean get() = totalHeroes > 0 && completedHeroes >= totalHeroes
    val isInProgress: Boolean get() = completedHeroes > 0 && !isUnlocked
    val progress: Float get() = if (totalHeroes > 0) completedHeroes.toFloat() / totalHeroes else 0f
    val badgeColor: Color get() = districtColors.getOrElse(colorIndex) { districtColors[0] }
}

// ─── Main composable ──────────────────────────────────────────────────────────
@Composable
fun DistrictBadge(
    badge: BadgeData,
    size: Dp = 72.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    // Animate scale for a satisfying "pop" when first unlocked
    val targetScale = if (badge.isUnlocked) 1f else 0.93f
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "badge_scale"
    )

    // Animate progress ring sweep angle
    val animatedSweep by animateFloatAsState(
        targetValue = badge.progress * 360f,
        animationSpec = tween(durationMillis = 900, easing = EaseOutCubic),
        label = "progress_sweep"
    )

    val fillColor = if (badge.isUnlocked) badge.badgeColor else Color(0xFFD3D1C7)
    val strokeColor = if (badge.isUnlocked) badgeTextColor(badge.badgeColor) else Color(0xFF888780)
    val ringColor = badge.badgeColor

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // ── Canvas layer: fill circle + inner dashed ring + progress arc ──
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.toPx() / 2f
            val strokeWidth = (size.toPx() * 0.08f).coerceAtLeast(4f)
            val ringInset = strokeWidth / 2f

            // Outer filled circle
            drawCircle(
                color = fillColor,
                radius = radius - strokeWidth / 2f
            )

            // Border ring
            drawCircle(
                color = strokeColor,
                radius = radius - strokeWidth / 2f,
                style = Stroke(width = strokeWidth * 0.6f)
            )

            // Inner decorative dashed ring (only when unlocked)
            if (badge.isUnlocked) {
                drawCircle(
                    color = strokeColor.copy(alpha = 0.35f),
                    radius = radius * 0.72f,
                    style = Stroke(
                        width = 2f,
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(6f, 5f), 0f
                        )
                    )
                )
            }

            // Progress arc (shown when locked or in-progress)
            if (!badge.isUnlocked && badge.isInProgress) {
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }

        // ── Content layer: code text or lock icon ──
        if (badge.isUnlocked) {
            Text(
                text = badge.districtCode,
                fontSize = (size.value * 0.25f).sp,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor(badge.badgeColor)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = "Locked – ${badge.districtName}",
                    tint = Color(0xFF888780),
                    modifier = Modifier.size(size * 0.3f)
                )
                if (badge.isInProgress) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${badge.completedHeroes}/${badge.totalHeroes}",
                        fontSize = (size.value * 0.13f).sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5F5E5A)
                    )
                }
            }
        }
    }
}

// ─── Row variant: badge + district name label ────────────────────────────────
@Composable
fun DistrictBadgeRow(
    badge: BadgeData,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DistrictBadge(badge = badge, size = 64.dp, onClick = onClick)

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = badge.districtName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = when {
                    badge.isUnlocked   -> "All heroes explored!"
                    badge.isInProgress -> "${badge.completedHeroes} of ${badge.totalHeroes} heroes explored"
                    else               -> "Not started yet"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (badge.isUnlocked) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = badge.badgeColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "Earned",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = badgeTextColor(badge.badgeColor)
                )
            }
        }
    }
}