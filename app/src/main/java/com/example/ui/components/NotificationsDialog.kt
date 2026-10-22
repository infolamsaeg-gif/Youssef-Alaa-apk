package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppNotification
import com.example.data.repository.AgencyRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsDialog(
    userUid: String,
    notifications: List<AppNotification>,
    repository: AgencyRepository,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("مركز الإشعارات والتنبيهات", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                if (notifications.isNotEmpty()) {
                    TextButton(
                        onClick = { repository.markAllNotificationsRead(userUid) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("تعليم الكل كمقروء", style = MaterialTheme.typography.labelSmall)
                    }
                }

                if (notifications.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Notifications,
                        title = "لا توجد إشعارات جديدة",
                        message = "ستصلك التنبيهات هنا فور حدوث أي مستجدات على مشاريعك وفواتيرك."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationCardItem(notification = notif)
                        }
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إغلاق")
                }
            }
        }
    }
}

@Composable
fun NotificationCardItem(notification: AppNotification) {
    val dateStr = remember(notification.timestamp) {
        val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
        sdf.format(Date(notification.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(notification.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(notification.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(dateStr, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}
