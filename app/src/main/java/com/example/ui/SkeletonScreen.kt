package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * A highly performant pulse modifier that animates the draw layer's alpha channel.
 * Since it uses graphicsLayer, it completely avoids recomposition and layout stages,
 * running entirely on the GPU/RenderThread at locked 60/120fps.
 */
@Composable
fun Modifier.pulseAnimation(enabled: Boolean = true): Modifier {
    if (!enabled) return this
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    return this.graphicsLayer {
        this.alpha = alpha
    }
}

@Composable
fun SkeletonLoadingScreen(isWideScreen: Boolean) {
    val horizontalPadding = if (isWideScreen) 32.dp else 20.dp
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        if (isWideScreen) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .pulseAnimation()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    SkeletonDetails()
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
                    .clip(RoundedCornerShape(32.dp))
                    .pulseAnimation()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            SkeletonDetails()
        }
    }
}

@Composable
fun SkeletonDetails() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .pulseAnimation()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth(0.3f)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
                .pulseAnimation()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .pulseAnimation()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .pulseAnimation()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        repeat(5) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (index == 4) 0.7f else 1f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .pulseAnimation()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

