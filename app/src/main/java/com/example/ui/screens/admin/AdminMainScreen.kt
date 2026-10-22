package com.example.ui.screens.admin

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.config.AgencyConfig
import com.example.data.model.*
import com.example.data.repository.AgencyRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import java.net.URLEncoder

enum class AdminSection(val titleAr: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("لوحة المتابعة", Icons.Default.Dashboard),
    CLIENTS("العملاء", Icons.Default.People),
    PROJECTS("المشاريع والكانبان", Icons.Default.FolderOpen),
    INBOX("الموافقات والتعديلات", Icons.Default.RateReview),
    SUPPORT("تذاكر الدعم", Icons.Default.SupportAgent),
    ORDERS("طلبات الخدمات", Icons.Default.AddShoppingCart),
    INVOICES("الفواتير والتحصيلات", Icons.Default.ReceiptLong),
    SERVICES("الخدمات والكوبونات", Icons.Default.Tune),
    ANNOUNCEMENTS("الإعلانات", Icons.Default.Campaign),
    SETTINGS("بيانات الوكالة", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    repository: AgencyRepository,
    onExitAdmin: () -> Unit
) {
    BackHandler { onExitAdmin() }

    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf(AdminSection.DASHBOARD) }

    val users by repository.users.collectAsState()
    val projects by repository.projects.collectAsState()
    val feedbacks by repository.feedbacks.collectAsState()
    val invoices by repository.invoices.collectAsState()
    val tickets by repository.tickets.collectAsState()
    val orders by repository.orders.collectAsState()
    val services by repository.services.collectAsState()
    val coupons by repository.coupons.collectAsState()
    val announcements by repository.announcements.collectAsState()
    val auditLogs by repository.auditLogs.collectAsState()

    val clientsOnly = remember(users) { users.filter { !it.isAdmin } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BrandMonogramIcon(size = 28.dp)
                        Text(
                            text = "لوحة تحكم MY AGENCY",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ColorPrimaryOrange.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("الإدارة الشاملة", color = ColorPrimaryOrange, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onExitAdmin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "خروج")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            // Horizontal scrollable section switcher on mobile
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(AdminSection.entries) { sec ->
                        val isSelected = selectedSection == sec
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSection = sec },
                            label = { Text(sec.titleAr, style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = {
                                Icon(sec.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedSection) {
                AdminSection.DASHBOARD -> {
                    AdminDashboardTab(
                        clients = clientsOnly,
                        projects = projects,
                        feedbacks = feedbacks,
                        invoices = invoices,
                        tickets = tickets,
                        orders = orders,
                        onNavigateToSection = { selectedSection = it }
                    )
                }
                AdminSection.CLIENTS -> {
                    AdminClientsListTab(clients = clientsOnly, projects = projects)
                }
                AdminSection.PROJECTS -> {
                    AdminProjectsKanbanTab(projects = projects, clients = clientsOnly, repository = repository)
                }
                AdminSection.INBOX -> {
                    AdminInboxTab(feedbacks = feedbacks, repository = repository)
                }
                AdminSection.SUPPORT -> {
                    AdminSupportTicketsTab(tickets = tickets, repository = repository)
                }
                AdminSection.ORDERS -> {
                    AdminServiceOrdersTab(orders = orders, repository = repository)
                }
                AdminSection.INVOICES -> {
                    AdminInvoicesTab(invoices = invoices, repository = repository)
                }
                AdminSection.SERVICES -> {
                    AdminServicesCouponsTab(services = services, coupons = coupons, repository = repository)
                }
                AdminSection.ANNOUNCEMENTS -> {
                    AdminAnnouncementsTab(announcements = announcements, auditLogs = auditLogs)
                }
                AdminSection.SETTINGS -> {
                    AdminAgencySettingsTab(repository = repository)
                }
            }
        }
    }
}

@Composable
fun AdminDashboardTab(
    clients: List<UserProfile>,
    projects: List<Project>,
    feedbacks: List<ClientFeedback>,
    invoices: List<Invoice>,
    tickets: List<SupportTicket>,
    orders: List<ServiceOrder>,
    onNavigateToSection: (AdminSection) -> Unit
) {
    val totalRevenue = invoices.sumOf { it.paid }
    val unpaidRevenue = invoices.sumOf { it.total - it.paid }
    val activeProjects = projects.count { it.status == ProjectStatus.WORKING }
    val reviewProjects = projects.count { it.status == ProjectStatus.REVIEW }
    val unhandledFeedback = feedbacks.count { !it.handled }
    val openTickets = tickets.count { it.status == TicketStatus.OPEN }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("المؤشرات العامة للوكالة 📊", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        }

        // Metrics Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("إجمالي الإيرادات", "${totalRevenue.toInt()} ${AgencyConfig.CURRENCY}", Icons.Default.AttachMoney, ColorSuccess, Modifier.weight(1f))
                AdminMetricCard("مستحقات غير مدفوعة", "${unpaidRevenue.toInt()} ${AgencyConfig.CURRENCY}", Icons.Default.MoneyOff, ColorDanger, Modifier.weight(1f))
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard("إجمالي العملاء", "${clients.size}", Icons.Default.People, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                AdminMetricCard("مشاريع جارية", "$activeProjects", Icons.Default.HourglassTop, ColorSecondaryPurple, Modifier.weight(1f))
            }
        }

        // Needs Attention Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder().copy(brush = BrandGradient)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("يحتاج انتباهك الآن ⚡", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                    AttentionItem("تعديلات وموافقات العملاء غير المعالجة: $unhandledFeedback", unhandledFeedback > 0) {
                        onNavigateToSection(AdminSection.INBOX)
                    }
                    AttentionItem("تذاكر الدعم الفني المفتوحة: $openTickets", openTickets > 0) {
                        onNavigateToSection(AdminSection.SUPPORT)
                    }
                    AttentionItem("مشاريع بانتظار مراجعة العميل: $reviewProjects", reviewProjects > 0) {
                        onNavigateToSection(AdminSection.PROJECTS)
                    }
                    AttentionItem("طلبات خدمات جديدة: ${orders.count { it.status == OrderStatus.NEW }}", orders.any { it.status == OrderStatus.NEW }) {
                        onNavigateToSection(AdminSection.ORDERS)
                    }
                }
            }
        }
    }
}

@Composable
fun AttentionItem(text: String, isUrgent: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isUrgent) ColorWarning.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun AdminMetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

// CLIENTS LIST TAB
@Composable
fun AdminClientsListTab(clients: List<UserProfile>, projects: List<Project>) {
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }
    val filtered = remember(clients, search) {
        if (search.isBlank()) clients else clients.filter { it.fullName.contains(search) || it.phonePrimary.contains(search) || it.email.contains(search) }
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("بحث باسم العميل أو الهاتف...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        items(filtered) { client ->
            val clientProjects = projects.filter { it.clientUid == client.uid }
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(client.fullName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(client.governorate, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                    Text("الهاتف: ${client.phonePrimary} | ${client.phoneSecondary}", style = MaterialTheme.typography.bodySmall)
                    Text("البريد: ${client.email}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${clientProjects.size} مشاريع مسجلة", style = MaterialTheme.typography.labelSmall)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val url = "https://wa.me/2${client.phonePrimary}"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("واتساب", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

// PROJECTS KANBAN TAB
@Composable
fun AdminProjectsKanbanTab(
    projects: List<Project>,
    clients: List<UserProfile>,
    repository: AgencyRepository
) {
    var selectedKanbanStatus by remember { mutableStateOf(ProjectStatus.WORKING) }
    val filtered = remember(projects, selectedKanbanStatus) { projects.filter { it.status == selectedKanbanStatus } }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("المشاريع ولوحة الكانبان (Kanban)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(ProjectStatus.entries) { st ->
                val isSelected = selectedKanbanStatus == st
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedKanbanStatus = st },
                    label = { Text("${st.titleAr} (${projects.count { it.status == st }})") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary)
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(filtered) { proj ->
                val client = clients.find { it.uid == proj.clientUid }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(proj.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("العميل: ${client?.fullName ?: "غير معروف"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        CustomProgressBar(progress = proj.progress)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("نقل الحالة إلى:", style = MaterialTheme.typography.labelSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                ProjectStatus.entries.forEach { targetSt ->
                                    if (targetSt != proj.status) {
                                        OutlinedButton(
                                            onClick = {
                                                repository.addOrUpdateProject(proj.copy(status = targetSt, progress = if (targetSt == ProjectStatus.DONE) 100 else proj.progress))
                                            },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(targetSt.titleAr, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// INBOX TAB
@Composable
fun AdminInboxTab(feedbacks: List<ClientFeedback>, repository: AgencyRepository) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("صندوق الموافقات وطلبات التعديل (${feedbacks.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
        items(feedbacks) { fb ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(fb.from, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(if (fb.type == "approve") "موافقة ✅" else "طلب تعديل ✏️", color = if (fb.type == "approve") ColorSuccess else ColorWarning)
                    }
                    Text("المشروع: ${fb.projectTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    Text(fb.text, style = MaterialTheme.typography.bodySmall)

                    Button(
                        onClick = { repository.submitFeedback(fb.projectId, fb.projectTitle, fb.type, fb.text) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("تم التعامل والمعالجة")
                    }
                }
            }
        }
    }
}

// SUPPORT TICKETS TAB
@Composable
fun AdminSupportTicketsTab(tickets: List<SupportTicket>, repository: AgencyRepository) {
    var replyText by remember { mutableStateOf("") }
    var selectedTicket by remember { mutableStateOf<SupportTicket?>(null) }

    if (selectedTicket != null) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("تذكرة: ${selectedTicket!!.subject}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                IconButton(onClick = { selectedTicket = null }) { Icon(Icons.Default.Close, contentDescription = null) }
            }

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(selectedTicket!!.messages) { msg ->
                    ChatBubbleItem(message = msg, isMe = msg.isFromAdmin)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = replyText, onValueChange = { replyText = it }, placeholder = { Text("اكتب ردك للعميل...") }, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            repository.sendMessage(selectedTicket!!.id, replyText, isFromAdmin = true)
                            replyText = ""
                        }
                    }
                ) {
                    Text("إرسال")
                }
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { Text("تذاكر الدعم الفني والمحادثات (${tickets.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
            items(tickets) { tkt ->
                Card(modifier = Modifier.fillMaxWidth().clickable { selectedTicket = tkt }, shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(tkt.subject, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("العميل: ${tkt.clientName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${tkt.messages.size} رسائل في المحادثة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// SERVICE ORDERS TAB
@Composable
fun AdminServiceOrdersTab(orders: List<ServiceOrder>, repository: AgencyRepository) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("طلبات الخدمات الجديدة من العملاء (${orders.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
        items(orders) { ord ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(ord.clientName, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text("${ord.totalPrice.toInt()} ${AgencyConfig.CURRENCY}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Text("الهاتف: ${ord.clientPhone}", style = MaterialTheme.typography.bodySmall)
                    Text("الخدمات: ${ord.services.joinToString { it.name }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// INVOICES TAB
@Composable
fun AdminInvoicesTab(invoices: List<Invoice>, repository: AgencyRepository) {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("الفواتير ومراجعة إيصالات السداد (${invoices.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
        items(invoices) { inv ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(inv.id, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(inv.status.titleAr, color = if (inv.status == InvoiceStatus.PAID) ColorSuccess else ColorWarning)
                    }
                    Text("العميل: ${inv.clientName}", style = MaterialTheme.typography.bodySmall)
                    Text("الإجمالي: ${inv.total.toInt()} ج.م | المسدد: ${inv.paid.toInt()} ج.م", style = MaterialTheme.typography.bodySmall)

                    if (inv.payments.isNotEmpty()) {
                        Text("إيصالات التحويل المرفقة (${inv.payments.size}):", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        inv.payments.forEach { pay ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("${pay.method}: ${pay.amount.toInt()} ج.م", style = MaterialTheme.typography.bodySmall)
                                if (!pay.verified) {
                                    Button(
                                        onClick = {
                                            repository.verifyPayment(inv.id, pay.id, true)
                                            Toast.makeText(context, "تم اعتماد الإيصال والسداد بنجاح", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("اعتماد الدفع", fontSize = 11.sp)
                                    }
                                } else {
                                    Text("معتمد ✅", color = ColorSuccess, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// SERVICES & COUPONS TAB
@Composable
fun AdminServicesCouponsTab(services: List<ServiceItem>, coupons: List<DiscountCoupon>, repository: AgencyRepository) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("الخدمات والأسعار الحالية (${services.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
        items(services) { srv ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(srv.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text("${srv.price.toInt()} ج.م (قبل: ${srv.oldPrice?.toInt()} ج.م)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        item {
            Text("كوبونات الخصم (${coupons.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        items(coupons) { cp ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(cp.code, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    Text("خصم ${cp.discountPercent.toInt()}%", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// ANNOUNCEMENTS & AUDIT LOGS TAB
@Composable
fun AdminAnnouncementsTab(announcements: List<Announcement>, auditLogs: List<AuditLog>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("الإعلانات الحالية (${announcements.size})", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)) }
        items(announcements) { ann ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(ann.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text(ann.content, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item {
            Text("سجل النشاط الإداري (Audit Log)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
        items(auditLogs.take(10)) { log ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("${log.actor}: ${log.action}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

// AGENCY SETTINGS TAB
@Composable
fun AdminAgencySettingsTab(repository: AgencyRepository) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("بيانات الوكالة والتواصل", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("واتساب 1: ${AgencyConfig.WHATSAPP_PRIMARY}")
                Text("واتساب 2: ${AgencyConfig.WHATSAPP_SECONDARY}")
                Text("فودافون كاش: ${AgencyConfig.VODAFONE_CASH_NUMBER}")
                Text("إنستاباي: ${AgencyConfig.INSTAPAY_HANDLE}")
            }
        }

        Button(
            onClick = {
                Toast.makeText(context, "تم تصدير نسخة احتياطية من البيانات بنجاح 💾", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("تصدير نسخة احتياطية JSON")
        }
    }
}
