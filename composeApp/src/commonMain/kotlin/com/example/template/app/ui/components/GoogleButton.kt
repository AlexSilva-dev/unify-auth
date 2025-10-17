package com.example.template.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import unify_auth.composeapp.generated.resources.Res
import unify_auth.composeapp.generated.resources.google_brand_web_dark_rd

@Composable
fun GoogleButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
    ) {
        Image(
            painter = painterResource(Res.drawable.google_brand_web_dark_rd),
            contentDescription = "back",
            contentScale = ContentScale.Crop,
        )
    }
}