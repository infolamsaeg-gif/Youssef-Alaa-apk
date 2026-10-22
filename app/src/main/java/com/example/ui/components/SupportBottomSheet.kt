package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.AgencyConfig
import com.example.data.model.ChatMessage
import com.example.data.model.FAQItem
import com.example.data.model.SupportTicket
import com.example.data.model.UserProfile
import com.example.data.repository.AgencyRepository
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportBottomSheet(
    user: UserProfile,
    repository: AgencyRepository,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val tickets by repository.tickets.collectAsState()
    val faqs by repository.faqs.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Chat, 1: Contact, 2: FAQ
    var messageText by remember { mutableStateOf("") }

    val userTicket = tickets.find { it.clientUid == user.uid }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = Modifier.fillMaxHeight(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ColorPrimaryOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = ColorPrimaryOrange)
                    }
                    Column {
                        Text(
                            text = "مركز الدعم والمساعدة المباشر",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "متوسط الرد: ${AgencyConfig.AVG_RESPONSE_TIME}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ColorSuccess
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            // Tab bar
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("المحادثة المباشرة 💬") }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("واتساب واتصال 📞") }
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("الأسئلة الشائعة ❓") }
                )
            }

            when (activeTab) {
                0 -> {
                    // LIVE CHAT
                    if (userTicket == null) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("ابدأ محادثة فورية مع فريق الدعم", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("متاحون للرد على كافة استفساراتكم بخصوص المشاريع والدفع والملفات", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    repository.createTicket(
                                        clientUid = user.uid,
                                        clientName = user.fullName,
                                        subject = "محادثة دعم عامة",
                                        firstMessage = "مرحباً، أحتاج إلى استفسار بخصوص خدمات MY AGENCY."
                                    )
                                }
                            ) {
                                Text("فتح تذكرة وبدء الشات")
                            }
                        }
                    } else {
                        val listState = rememberLazyListState()
                        LaunchedEffect(userTicket.messages.size) {
                            if (userTicket.messages.isNotEmpty()) {
                                listState.animateScrollToItem(userTicket.messages.size - 1)
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(userTicket.messages) { msg ->
                                ChatBubbleItem(message = msg, isMe = !msg.isFromAdmin)
                            }
                        }

                        // Input bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                placeholder = { Text("اكتب رسالتك للدعم...") },
                                modifier = Modifier.weight(1f),
                                maxLines = 3,
                                shape = RoundedCornerShape(20.dp)
                            )

                            IconButton(
                                onClick = {
                                    if (messageText.isNotBlank()) {
                                        repository.sendMessage(userTicket.id, messageText, isFromAdmin = false)
                                        messageText = ""
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.White)
                            }
                        }
                    }
                }
                1 -> {
                    // CONTACT BUTTONS & WORKING HOURS
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("ساعات العمل الرسمية", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                    Text(AgencyConfig.WORKING_HOURS, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        item {
                            // Primary WhatsApp
                            ContactActionCard(
                                title = "واتساب الدعم الفني الرئيسي",
                                subtitle = "الرقم: 01508664132",
                                icon = Icons.Default.Chat,
                                iconColor = ColorSuccess,
                                onClick = {
                                    try {
                                        val url = "https://wa.me/${AgencyConfig.WHATSAPP_PRIMARY}"
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "تعذر فتح تطبيق واتساب", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        item {
                            // Secondary WhatsApp
                            ContactActionCard(
                                title = "واتساب إدارة المشاريع والتعاقدات",
                                subtitle = "الرقم: 01151735927",
                                icon = Icons.Default.Chat,
                                iconColor = ColorSuccess,
                                onClick = {
                                    try {
                                        val url = "https://wa.me/${AgencyConfig.WHATSAPP_SECONDARY}"
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "تعذر فتح تطبيق واتساب", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        item {
                            // Direct Phone Call
                            ContactActionCard(
                                title = "الاتصال الهاتفي المباشر",
                                subtitle = "01508664132",
                                icon = Icons.Default.Phone,
                                iconColor = ColorPrimaryOrange,
                                onClick = {
                                    try {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${AgencyConfig.CALL_PHONE}"))
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "تعذر فتح تطبيق الهاتف", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
                2 -> {
                    // FAQ ACCORDION
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(faqs) { faq ->
                            FaqAccordionCard(faq = faq)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = message.text,
                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ContactActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FaqAccordionCard(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
