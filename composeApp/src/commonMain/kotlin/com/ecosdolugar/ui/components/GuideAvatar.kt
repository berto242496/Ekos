package com.ecosdolugar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecosdolugar.ui.theme.PineDeep
import com.ecosdolugar.ui.theme.SurfaceCream

@Composable
fun GuideAvatar(
    name: String,
    avatarUrl: String?,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(size / 4)
    if (avatarUrl.isNullOrBlank()) {
        Box(
            modifier = modifier
                .size(size)
                .background(PineDeep, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1).uppercase(),
                color = SurfaceCream,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4f).sp
            )
        }
    } else {
        RouteCoverImage(
            url = avatarUrl,
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(PineDeep)
        )
    }
}
