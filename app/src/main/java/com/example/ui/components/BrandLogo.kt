package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.AgencyConfig
import com.example.ui.theme.ColorPrimaryOrange
import com.example.ui.theme.ColorSecondaryPurple

/**
 * Geometric Monogram M + A Vector Logo for MY AGENCY
 */
@Composable
fun BrandMonogramIcon(
    size: Dp = 48.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val strokeWidth = w * 0.12f

        // Fiery Orange to Electric Purple Gradient
        val brush = Brush.linearGradient(
            colors = listOf(ColorPrimaryOrange, ColorSecondaryPurple),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )

        // Geometric M Path
        val pathM = Path().apply {
            moveTo(w * 0.14f, h * 0.88f)
            lineTo(w * 0.14f, h * 0.22f)
            lineTo(w * 0.50f, h * 0.58f)
            lineTo(w * 0.86f, h * 0.22f)
            lineTo(w * 0.86f, h * 0.88f)
        }

        drawPath(
            path = pathM,
            brush = brush,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Geometric Intersecting A Peak (Sharp Arrow/Flame Accent)
        val pathA = Path().apply {
            moveTo(w * 0.32f, h * 0.72f)
            lineTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.68f, h * 0.72f)
        }

        drawPath(
            path = pathA,
            brush = Brush.linearGradient(
                colors = listOf(ColorSecondaryPurple, ColorPrimaryOrange),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            ),
            style = Stroke(
                width = strokeWidth * 0.85f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Crossbar for A
        drawLine(
            brush = brush,
            start = Offset(w * 0.36f, h * 0.54f),
            end = Offset(w * 0.64f, h * 0.54f),
            strokeWidth = strokeWidth * 0.75f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun BrandLogoHeader(
    modifier: Modifier = Modifier,
    iconSize: Dp = 44.dp,
    showSlogan: Boolean = true
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(iconSize + 12.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.radialGradient(
                        listOf(ColorPrimaryOrange.copy(alpha = 0.25f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            BrandMonogramIcon(size = iconSize)
        }

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = AgencyConfig.AGENCY_NAME,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            if (showSlogan) {
                Text(
                    text = AgencyConfig.AGENCY_SLOGAN,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
