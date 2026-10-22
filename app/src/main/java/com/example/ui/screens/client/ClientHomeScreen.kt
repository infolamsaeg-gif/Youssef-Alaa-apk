package com.example.ui.screens.client

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.AgencyConfig
import com.example.data.model.*
import com.example.data.repository.AgencyRepository
import com.example.ui.components.CustomProgressBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.TypeBadge
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClientHomeScreen(
    user: UserProfile,
    repository: AgencyRepository,
    onNavigateToProjects: () -> Unit,
    onNavigateToServices: () -> Unit,
    onNavigateToInvoices: () -> Unit,
    onOpenProject: (String) -> Unit,
    onOpenSupport: () -> Unit
) {
    val projects by repository.projects.collectAsState()
    val invoices by repository.invoices.collectAsState()
    val announcements by repository.announcements.collectAsState()

    val myProjects = remember(projects, user.uid) { projects.filter { it.clientUid == user.uid } }
    val myInvoices = remember(invoices, user.uid) { invoices.filter { it.clientUid == user.uid } }

    val workingProjectsCount = myProjects.count { it.status == ProjectStatus.WORKING }
    val reviewNeededCount = myProjects.count { it.status == ProjectStatus.REVIEW }
    val pendingInvoicesCount = myInvoices.count { it.status == InvoiceStatus.PENDING || it.status == InvoiceStatus.PARTIAL }

    // Countdown Timer for Offer
    var countdownText by remember { mutableStateOf("جاري الحساب...") }
    LaunchedEffect(Unit) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val targetDate = try {
            sdf.parse(AgencyConfig.DEFAULT_OFFER_ENDS_AT)?.time ?: (System.currentTimeMillis() + 864000000L)
        } catch (e: Exception) {
            System.currentTimeMillis() + 864000000L
        }

        while (true) {
            val diff = targetDate - System.currentTimeMillis()
            if (diff <= 0) {
                countdownText = "انتهى العرض!"
                break
            } else {
                val days = diff / (1000 * 60 * 60 * 24)
                val hours = (diff / (1000 * 60 * 60)) % 24
                val minutes = (diff / (1000 * 60)) % 60
                val seconds = (diff / 1000) % 60
                countdownText = "$days يوم، $hours ساعة، $minutes دقيقة، $seconds ثانية"
            }
            delay(1000)
        }
    }

    var bannerDismissed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = BrandGradient)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "أهلاً بك يا فندم،",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = user.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(ColorPrimaryOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.fullName.take(1),
                                color = ColorPrimaryOrange,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // 3 Quick Metrics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        HomeStatColumn(
                            count = workingProjectsCount,
                            label = "مشاريع جارية",
                            color = ColorSecondaryPurple,
                            onClick = onNavigateToProjects
                        )
                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(MaterialTheme.colorScheme.outline))
                        HomeStatColumn(
                            count = reviewNeededCount,
                            label = "بانتظار مراجعتك",
                            color = ColorWarning,
                            onClick = onNavigateToProjects
                        )
                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(MaterialTheme.colorScheme.outline))
                        HomeStatColumn(
                            count = pendingInvoicesCount,
                            label = "فواتير مستحقة",
                            color = ColorDanger,
                            onClick = onNavigateToInvoices
                        )
                    }
                }
            }
        }

        // Announcement Banner
        val activeAnnouncement = announcements.firstOrNull { it.active }
        if (activeAnnouncement != null && !bannerDismissed) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(ColorPrimaryOrange, ColorSecondaryPurple))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, tint = ColorPrimaryOrange, modifier = Modifier.size(24.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(activeAnnouncement.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Text(activeAnnouncement.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { bannerDismissed = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Special Offer Countdown Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = BrandGradient)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ColorPrimaryOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = ColorPrimaryOrange, modifier = Modifier.size(28.dp))
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("عروض خاصة لعملاء MY AGENCY", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ColorDanger.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("خصم 50%", color = ColorDanger, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold))
                            }
                        }
                        Text("ينتهي العرض خلال: $countdownText", style = MaterialTheme.typography.bodySmall, color = ColorSecondaryPurple)
                    }

                    Button(
                        onClick = onNavigateToServices,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("اطلب الآن", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Quick Action Shortcuts
        item {
            Text(
                text = "إجراءات سريعة ⚡",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    title = "طلب خدمة",
                    icon = Icons.Default.AddShoppingCart,
                    color = ColorPrimaryOrange,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToServices
                )
                QuickActionCard(
                    title = "مشاريعي",
                    icon = Icons.Default.FolderOpen,
                    color = ColorSecondaryPurple,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToProjects
                )
                QuickActionCard(
                    title = "فواتيري",
                    icon = Icons.Default.ReceiptLong,
                    color = ColorSuccess,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToInvoices
                )
                QuickActionCard(
                    title = "الدعم المباشر",
                    icon = Icons.Default.SupportAgent,
                    color = ColorInfo,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSupport
                )
            }
        }

        // Active Projects Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مشاريعك الجارية (${myProjects.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "عرض الكل",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToProjects() }
                )
            }
        }

        if (myProjects.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("لا توجد مشاريع مضافة حالياً", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("اطلب أول خدمة وسيقوم الفريق ببدء التنفيذ فوراً.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = onNavigateToServices) { Text("تصفح الخدمات والأسعار") }
                    }
                }
            }
        } else {
            items(myProjects.take(2)) { proj ->
                HomeProjectCard(project = proj, onClick = { onOpenProject(proj.id) })
            }
        }
    }
}

@Composable
fun HomeStatColumn(count: Int, label: String, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(text = "$count", color = color, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp))
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(title, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun HomeProjectCard(project: Project, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BorderDark, ColorSurfaceElevated)))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypeBadge(type = project.type)
                StatusBadge(status = project.status)
            }
            Text(project.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            CustomProgressBar(progress = project.progress)
        }
    }
}
