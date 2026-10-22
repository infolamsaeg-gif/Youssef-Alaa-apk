package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ClientFeedback
import com.example.data.model.FileKind
import com.example.data.model.ProjectFile
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatusBadge(
    status: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        ProjectStatus.NEW -> Triple(Color(0xFF1E293B), Color(0xFF94A3B8), Icons.Default.FiberNew)
        ProjectStatus.WORKING -> Triple(ColorSecondaryPurple.copy(alpha = 0.2f), ColorSecondaryPurple, Icons.Default.HourglassTop)
        ProjectStatus.REVIEW -> Triple(ColorWarning.copy(alpha = 0.2f), ColorWarning, Icons.Default.RateReview)
        ProjectStatus.DONE -> Triple(ColorSuccess.copy(alpha = 0.2f), ColorSuccess, Icons.Default.CheckCircle)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = status.titleAr,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun TypeBadge(
    type: ProjectType,
    modifier: Modifier = Modifier
) {
    val (bgColor, icon) = when (type) {
        ProjectType.DESIGN -> Pair(ColorPrimaryOrange.copy(alpha = 0.15f), Icons.Default.Brush)
        ProjectType.VIDEO -> Pair(ColorSecondaryPurple.copy(alpha = 0.15f), Icons.Default.Movie)
        ProjectType.AI_VIDEO -> Pair(ColorHighlightLime.copy(alpha = 0.15f), Icons.Default.AutoAwesome)
        ProjectType.WEB -> Pair(ColorInfo.copy(alpha = 0.15f), Icons.Default.Language)
        ProjectType.STORE -> Pair(ColorWarning.copy(alpha = 0.15f), Icons.Default.Storefront)
        ProjectType.OTHER -> Pair(ColorSurface2, Icons.Default.Category)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(12.dp)
        )
        Text(
            text = type.titleAr,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun CustomProgressBar(
    progress: Int,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val clamped = progress.coerceIn(0, 100)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "نسبة الإنجاز",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "$clamped%",
                    color = if (clamped == 100) ColorSuccess else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            val progressFraction = clamped / 100f
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                ColorPrimaryOrange,
                                if (clamped == 100) ColorSuccess else ColorSecondaryPurple
                            )
                        )
                    )
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    testTag: String = "gradient_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = ColorSurfaceElevated
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        BrandGradient
                    } else {
                        Brush.horizontalGradient(listOf(ColorSurfaceElevated, ColorSurfaceElevated))
                    }
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    color = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun StepperView(
    currentStatus: ProjectStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        ProjectStatus.NEW,
        ProjectStatus.WORKING,
        ProjectStatus.REVIEW,
        ProjectStatus.DONE
    )
    val currentIndex = currentStatus.stepIndex

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isPassed = index < currentIndex
            val isCurrent = index == currentIndex

            val circleColor = when {
                isCurrent -> ColorPrimaryOrange
                isPassed -> ColorSuccess
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            val textColor = when {
                isCurrent -> MaterialTheme.colorScheme.primary
                isPassed -> ColorSuccess
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(circleColor)
                        .border(
                            width = if (isCurrent) 2.dp else 1.dp,
                            color = if (isCurrent) ColorSecondaryPurple else circleColor,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Text(
                    text = step.titleAr,
                    color = textColor,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center
                )
            }

            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(if (index < currentIndex) ColorSuccess else MaterialTheme.colorScheme.surfaceVariant)
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAction,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun ErrorStateView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ColorDanger.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.WarningAmber,
            contentDescription = null,
            tint = ColorDanger,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "حدث خطأ غير متوقع",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("إعادة المحاولة")
        }
    }
}

@Composable
fun ProjectFileCard(
    file: ProjectFile,
    onOpenImage: () -> Unit,
    onOpenUrl: () -> Unit,
    onCopyLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // File Type Icon Box
                val (icon, tintColor) = when (file.kind) {
                    FileKind.IMAGE -> Pair(Icons.Default.Image, ColorPrimaryOrange)
                    FileKind.VIDEO -> Pair(Icons.Default.Movie, ColorSecondaryPurple)
                    FileKind.PDF -> Pair(Icons.Default.PictureAsPdf, ColorDanger)
                    FileKind.LINK -> Pair(Icons.Default.Link, ColorInfo)
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tintColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = file.kind.titleAr,
                        tint = tintColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = file.kind.titleAr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onCopyLink) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ الرابط",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Thumbnail if image
            if (file.kind == FileKind.IMAGE) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onOpenImage() },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = file.url,
                        contentDescription = file.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.ZoomIn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text("تكبير", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (file.kind == FileKind.IMAGE) {
                    OutlinedButton(
                        onClick = onOpenImage,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("معاينة دقيقة", style = MaterialTheme.typography.labelMedium)
                    }
                }
                Button(
                    onClick = onOpenUrl,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (file.kind == FileKind.IMAGE) "فتح الرابط" else "فتح ومعاينة", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun FeedbackHistoryCard(
    feedback: ClientFeedback,
    modifier: Modifier = Modifier
) {
    val isApprove = feedback.type.equals("approve", ignoreCase = true)
    val borderColor = if (isApprove) ColorSuccess else ColorWarning
    val dateStr = remember(feedback.at) {
        val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
        sdf.format(Date(feedback.at))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(borderColor.copy(alpha = 0.5f)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (isApprove) Icons.Default.CheckCircle else Icons.Default.Edit,
                        contentDescription = null,
                        tint = borderColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isApprove) "اعتماد المشروع ✅" else "طلب تعديل ✏️",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = borderColor
                    )
                }
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = feedback.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بواسطة: ${feedback.from}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (feedback.handled) {
                    Text(
                        text = "تم التنفيذ والمتابعة ✓",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = ColorSuccess
                    )
                } else {
                    Text(
                        text = "قيد المعالجة...",
                        style = MaterialTheme.typography.labelSmall,
                        color = ColorWarning
                    )
                }
            }
        }
    }
}
