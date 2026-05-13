package com.example.nammakathey.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.nammakathey.R

// ── Nav destinations ──────────────────────────────────────────────────────────
private data class NavItem(
    val route    : String,
    val label    : String,
    val icon     : ImageVector? = null,
    val resIcon  : Int?         = null   // for drawable resource icons
)

private val navItems = listOf(
    NavItem(route = "home",     label = "Home",     icon = Icons.Rounded.Home),
    NavItem(route = "district", label = "Map",      resIcon = R.drawable.map),
    NavItem(route = "profile",  label = "Profile",  icon = Icons.Rounded.Person)
)

// ── Colors ────────────────────────────────────────────────────────────────────
private val NavyBlue  = Color(0xFF1A237E)
private val TealGreen = Color(0xFF4CAF82)
private val BarBg     = Color(0xFFFAFAFA)

@Composable
fun BottomBar(navController: NavController) {

    // Observe current route so selected state is always accurate
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute   = backStackEntry?.destination?.route

    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .shadow(
                elevation     = 16.dp,
                shape         = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ambientColor  = NavyBlue.copy(alpha = 0.08f),
                spotColor     = NavyBlue.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = BarBg
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            navItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavBarItem(
                    item       = item,
                    isSelected = isSelected,
                    onClick    = {
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                // Pop back to home to avoid deep back stack
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item       : NavItem,
    isSelected : Boolean,
    onClick    : () -> Unit
) {
    // Animate pill background color
    val pillColor by animateColorAsState(
        targetValue   = if (isSelected) NavyBlue else Color.Transparent,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label         = "pill_color"
    )

    // Animate icon + label color
    val contentColor by animateColorAsState(
        targetValue   = if (isSelected) Color.White else Color(0xFF9E9E9E),
        animationSpec = tween(300),
        label         = "content_color"
    )

    // Animate icon scale for a little "pop" on selection
    val iconScale by animateFloatAsState(
        targetValue   = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "icon_scale"
    )

    Column(
        modifier            = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,   // no ripple — pill handles the feedback
                onClick           = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Icon pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(pillColor)
                .padding(horizontal = if (isSelected) 20.dp else 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val iconModifier = Modifier
                .size(22.dp)
                .scale(iconScale)

            if (item.icon != null) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.label,
                    tint               = contentColor,
                    modifier           = iconModifier
                )
            } else if (item.resIcon != null) {
                Icon(
                    painter            = painterResource(id = item.resIcon),
                    contentDescription = item.label,
                    tint               = contentColor,
                    modifier           = iconModifier
                )
            }
        }

        // Label
        Text(
            text       = item.label,
            fontSize   = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color      = if (isSelected) NavyBlue else Color(0xFF9E9E9E)
        )
    }
}