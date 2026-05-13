package com.example.nammakathey.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Composable
fun KarnatakaMapBackground(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data("file:///android_asset/karnataka_map.svg")
            .decoderFactory(SvgDecoder.Factory())
            .build(),
        contentDescription = "Karnataka Map",
        modifier = modifier
    )
}