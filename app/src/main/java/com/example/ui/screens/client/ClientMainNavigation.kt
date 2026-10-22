package com.example.ui.screens.client

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.repository.AgencyRepository
import com.example.ui.components.BrandLogoHeader
import com.example.ui.components.NotificationsDialog
import com.example.ui.components.SupportBottomSheet
import com.example.ui.theme.ColorPrimaryOrange
import com.example.ui.theme.ColorSecondaryPurple

enum class ClientNavDestination(val titleAr: String, val icon: ImageVector) {
    HOME("الرئيسية", Icons.Default.Home),
    PROJECTS("مشاريعي", Icons.Default.FolderOpen),
    SERVICES("الخدمات", Icons.Default.AddShoppingCart),
    INVOICES("فواتيري", Icons.Default.ReceiptLong),
    ACCOUNT("حسابي", Icons.Default.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientMainNavigation(
    user: UserProfile,
    repository: AgencyRepository,
    onOpenProject: (String) -> Unit,
    onLogout: () -> Unit
) {
    var currentDestination by remember { mutableStateOf(ClientNavDestination.HOME) }
    var showSupportSheet by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    val notifications by repository.notifications.collectAsState()
    val unreadNotifications = remember(notifications, user.uid) {
        notifications.filter { it.toUid == user.uid && !it.read }
    }

    // Gentle pulsing animation for the Support FAB
    val infiniteTransition = rememberInfiniteTransition(label = "fab_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fab_scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    BrandLogoHeader(iconSize = 32.dp, showSlogan = false)
                },
                actions = {
                    // Notifications Bell with Badge
                    IconButton(onClick = { showNotificationsDialog = true }) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifications.isNotEmpty()) {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text("${unreadNotifications.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "الإشعارات")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                ClientNavDestination.entries.forEach { dest ->
                    val isSelected = currentDestination == dest
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = dest },
                        icon = {
                            Icon(
                                imageVector = dest.icon,
                                contentDescription = dest.titleAr,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                text = dest.titleAr,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("client_tab_${dest.name}")
                    )
                }
            }
        },
        floatingActionButton = {
            // Permanent Floating Action Button for Support with subtle pulse
            FloatingActionButton(
                onClick = { showSupportSheet = true },
                containerColor = ColorPrimaryOrange,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .scale(pulseScale)
                    .testTag("support_fab_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = "الدعم المباشر", modifier = Modifier.size(24.dp))
                    Text("الدعم", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentDestination) {
                ClientNavDestination.HOME -> {
                    ClientHomeScreen(
                        user = user,
                        repository = repository,
                        onNavigateToProjects = { currentDestination = ClientNavDestination.PROJECTS },
                        onNavigateToServices = { currentDestination = ClientNavDestination.SERVICES },
                        onNavigateToInvoices = { currentDestination = ClientNavDestination.INVOICES },
                        onOpenProject = onOpenProject,
                        onOpenSupport = { showSupportSheet = true }
                    )
                }
                ClientNavDestination.PROJECTS -> {
                    ClientProjectsScreen(
                        user = user,
                        repository = repository,
                        onOpenProject = onOpenProject,
                        onNavigateToServices = { currentDestination = ClientNavDestination.SERVICES }
                    )
                }
                ClientNavDestination.SERVICES -> {
                    ClientServicesScreen(
                        user = user,
                        repository = repository
                    )
                }
                ClientNavDestination.INVOICES -> {
                    ClientInvoicesScreen(
                        user = user,
                        repository = repository
                    )
                }
                ClientNavDestination.ACCOUNT -> {
                    ClientAccountScreen(
                        user = user,
                        repository = repository,
                        onLogout = onLogout
                    )
                }
            }
        }
    }

    // Support Bottom Sheet
    if (showSupportSheet) {
        SupportBottomSheet(
            user = user,
            repository = repository,
            onDismiss = { showSupportSheet = false }
        )
    }

    // Notifications Dialog
    if (showNotificationsDialog) {
        NotificationsDialog(
            userUid = user.uid,
            notifications = notifications.filter { it.toUid == user.uid },
            repository = repository,
            onDismiss = { showNotificationsDialog = false }
        )
    }
}
