package com.example.nammakathey.ui.screens

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

private val NavyBlue  = Color(0xFF1A237E)
private val TealGreen = Color(0xFF4CAF82)
private val AmberWarm = Color(0xFFFAC775)
private val LightGray = Color(0xFFF0F0F0)
private val MutedText = Color(0xFF5F5E5A)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DistrictScreen(
    completedDistricts : List<String>,
    totalDistricts     : Int,
    onDistrictClick    : (String) -> Unit
) {
    var webView          by remember { mutableStateOf<WebView?>(null) }
    var isLoading        by remember { mutableStateOf(true) }
    var selectedDistrict by remember { mutableStateOf<String?>(null) }

    val completedCount   = completedDistricts.size
    val progressFraction = completedCount.toFloat() / totalDistricts.coerceAtLeast(1)
    val pct              = (progressFraction * 100).toInt()
    val remaining        = totalDistricts - completedCount

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ── WebView fills the entire screen ───────────────────────────────────
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory  = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled  = true
                    settings.domStorageEnabled  = true
                    settings.setSupportZoom(true)
                    settings.builtInZoomControls   = true
                    settings.displayZoomControls   = false
                    settings.useWideViewPort        = true
                    settings.loadWithOverviewMode   = true
                    setBackgroundColor(android.graphics.Color.WHITE)
                    setInitialScale(1)

                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onDistrictClicked(name: String) {
                            post {
                                selectedDistrict = name
                                onDistrictClick(name)
                            }
                        }
                        @JavascriptInterface
                        fun onMapReady() { post { isLoading = false } }
                    }, "Android")

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            completedDistricts.forEach { d ->
                                view?.evaluateJavascript(
                                    "highlightDistrict('$d','#4CAF82');", null
                                )
                            }
                            post { isLoading = false }
                        }
                    }

                    loadUrl("file:///android_asset/index.html")
                    webView = this
                }
            }
        )

        // ── Loading overlay ───────────────────────────────────────────────────
        AnimatedVisibility(
            visible = isLoading,
            enter   = fadeIn(),
            exit    = fadeOut(tween(350))
        ) {
            val pulse by rememberInfiniteTransition(label = "p").animateFloat(
                initialValue  = 0.75f,
                targetValue   = 1f,
                animationSpec = infiniteRepeatable(
                    tween(700, easing = EaseInOutSine), RepeatMode.Reverse
                ),
                label = "pulse"
            )
            Box(
                modifier         = Modifier.fillMaxSize().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗺️", fontSize = (52 * pulse).sp)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Loading Karnataka map…",
                        fontWeight = FontWeight.Medium,
                        color      = NavyBlue.copy(alpha = pulse),
                        fontSize   = 14.sp
                    )
                    Spacer(Modifier.height(20.dp))
                    LinearProgressIndicator(
                        modifier   = Modifier.width(130.dp).clip(CircleShape),
                        color      = TealGreen,
                        trackColor = LightGray
                    )
                }
            }
        }

        // ════════════════════════════════════════════════════════════════════
        //  BOTTOM OVERLAY ONLY
        // ════════════════════════════════════════════════════════════════════
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // ── District tapped toast ─────────────────────────────────────────
            AnimatedVisibility(
                visible = selectedDistrict != null,
                enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                selectedDistrict?.let { name ->
                    val isDone   = completedDistricts.contains(name)
                    val initials = name.split(" ")
                        .take(2).joinToString("") { it.take(1) }.uppercase()

                    Surface(
                        shape           = RoundedCornerShape(16.dp),
                        color           = if (isDone) TealGreen else NavyBlue,
                        shadowElevation = 8.dp,
                        modifier        = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier              = Modifier.padding(14.dp, 12.dp),
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier         = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        initials,
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color      = Color.White
                                    )
                                }
                                Column {
                                    Text(
                                        name,
                                        fontWeight = FontWeight.Bold,
                                        color      = Color.White,
                                        fontSize   = 14.sp
                                    )
                                    Text(
                                        if (isDone) "✅ Badge earned!" else "Tap to explore heroes →",
                                        fontSize = 11.sp,
                                        color    = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Surface(
                                onClick  = { selectedDistrict = null },
                                shape    = CircleShape,
                                color    = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Rounded.Close, null,
                                        tint     = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ── Progress card ─────────────────────────────────────────────────
            Surface(
                shape           = RoundedCornerShape(18.dp),
                color           = Color.White,
                shadowElevation = 6.dp,
                modifier        = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp, 14.dp)) {

                    // Top row: label left, percentage + count right
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "District Progress",
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color      = NavyBlue
                        )
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "$pct%",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color      = TealGreen
                            )
                            Text("·", fontSize = 12.sp, color = MutedText)
                            Text(
                                "$completedCount / $totalDistricts",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color      = MutedText
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    // Animated gradient progress bar
                    val animFraction by animateFloatAsState(
                        targetValue   = progressFraction,
                        animationSpec = tween(900, easing = EaseOutCubic),
                        label         = "bar"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(LightGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animFraction)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(listOf(NavyBlue, TealGreen))
                                )
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Bottom row: legend left, remaining chip right
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            LegendDot(TealGreen,         "Completed")
                            LegendDot(Color(0xFFDDDDDD), "Not started")
                        }
                        if (remaining > 0) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = AmberWarm.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "$remaining left",
                                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color(0xFF7A5200)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = TealGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "All done! 🎉",
                                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize   = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = Color(0xFF1B6B4A)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Sync highlights on list change
    LaunchedEffect(completedDistricts) {
        webView?.let { v ->
            completedDistricts.forEach { d ->
                v.evaluateJavascript("highlightDistrict('$d','#4CAF82');", null)
            }
        }
    }
}

// ── Legend dot + label ────────────────────────────────────────────────────────
@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(label, fontSize = 11.sp, color = MutedText)
    }
}