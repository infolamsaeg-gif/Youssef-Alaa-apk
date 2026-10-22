package com.example.ui.screens.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.config.AgencyConfig
import com.example.data.model.*
import com.example.data.repository.AgencyRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientProjectDetailScreen(
    projectId: String,
    repository: AgencyRepository,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val projects by repository.projects.collectAsState()
    val feedbacks by repository.feedbacks.collectAsState()

    val project = projects.find { it.id == projectId }
    val projectFeedbacks = feedbacks.filter { it.projectId == projectId }

    var showApproveDialog by remember { mutableStateOf(false) }
    var showReviseDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var lightboxUrl by remember { mutableStateOf<String?>(null) }
    var lightboxTitle by remember { mutableStateOf("") }

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ErrorStateView(message = "المشروع غير موجود", onRetry = onBack)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    StatusBadge(status = project.status, modifier = Modifier.padding(end = 12.dp))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // 1. Stage Stepper
            item {
                Text("مراحل تنفيذ المشروع", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                StepperView(currentStatus = project.status)
            }

            // 2. Project Info & Description
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = BrandGradient)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TypeBadge(type = project.type)
                            if (!project.deadline.isNullOrBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Text("موعد التسليم: ${project.deadline}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        if (!project.description.isNullOrBlank()) {
                            Text(project.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 22.sp)
                        }

                        CustomProgressBar(progress = project.progress)
                    }
                }
            }

            // 3. Project Files
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ملفات ومعاينات المشروع (${project.files.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("اضغط للمعاينة والتحميل", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (project.files.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Text("جاري إعداد الملفات وسيتم رفعها هنا قريباً.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(16.dp))
                    }
                }
            } else {
                items(project.files) { file ->
                    ProjectFileCard(
                        file = file,
                        onOpenImage = {
                            lightboxUrl = file.url
                            lightboxTitle = file.name
                        },
                        onOpenUrl = {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(file.url)))
                            } catch (e: Exception) {
                                Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCopyLink = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("URL", file.url))
                            Toast.makeText(context, "تم نسخ رابط الملف", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }

            // 4. Decision Actions (قرارك)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = BrandGradient)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("قرارك بشأن المشروع 🎯", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = if (project.status == ProjectStatus.DONE) {
                                "تم اعتماد هذا المشروع بالكامل! يمكنك ترك تقييم بالنجوم أو طلب خدمة جديدة."
                            } else {
                                "يمكنك اعتماد العمل النهائي واستلام الملفات، أو طلب تعديلات دقيقة ليتم تنفيذها فوراً."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { showApproveDialog = true },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorSuccess)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("موافق ✅", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showReviseDialog = true },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ColorWarning)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Black)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("طلب تعديل ✏️", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Rating prompt if completed
                        if (project.status == ProjectStatus.DONE) {
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("تقييمك للمشروع:", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                                if (project.rating != null) {
                                    Row {
                                        repeat(project.rating) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = ColorWarning, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                } else {
                                    OutlinedButton(onClick = { showRatingDialog = true }) {
                                        Text("أضف تقييمك ⭐")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Timeline Events
            if (project.timeline.isNotEmpty()) {
                item {
                    Text("الخط الزمني للأحداث ⏱️", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                }
                items(project.timeline) { event ->
                    TimelineItemCard(event = event)
                }
            }

            // 6. Feedback Log
            item {
                Text("سجل الملاحظات والقرارات (${projectFeedbacks.size})", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            }
            if (projectFeedbacks.isEmpty()) {
                item {
                    Text("لا توجد طلبات تعديل سابقة لهذا المشروع.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(projectFeedbacks) { fb ->
                    FeedbackHistoryCard(
                        feedback = fb
                    )
                }
            }
        }
    }

    if (lightboxUrl != null) {
        LightboxModal(imageUrl = lightboxUrl!!, title = lightboxTitle, onDismiss = { lightboxUrl = null })
    }

    // Approve Dialog
    if (showApproveDialog) {
        ApproveDialog(
            projectTitle = project.title,
            onConfirm = {
                repository.submitFeedback(project.id, project.title, "approve", "تم اعتماد المشروع واستلام الملفات.")
                showApproveDialog = false
                Toast.makeText(context, "تم تسجيل اعتماد المشروع بنجاح 🎉", Toast.LENGTH_LONG).show()
            },
            onDismiss = { showApproveDialog = false },
            isSubmitting = false
        )
    }

    // Revise Dialog
    if (showReviseDialog) {
        ReviseDialog(
            projectTitle = project.title,
            onConfirm = { text ->
                repository.submitFeedback(project.id, project.title, "revise", text)
                showReviseDialog = false
                Toast.makeText(context, "تم إرسال طلب التعديل بنجاح 🚀", Toast.LENGTH_LONG).show()
            },
            onDismiss = { showReviseDialog = false },
            isSubmitting = false
        )
    }

    // Rating Dialog
    if (showRatingDialog) {
        var ratingVal by remember { mutableIntStateOf(5) }
        var reviewText by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showRatingDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("تقييم المشروع ⭐", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            IconButton(onClick = { ratingVal = i }) {
                                Icon(
                                    imageVector = if (i <= ratingVal) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    tint = ColorWarning,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("رأيك وتعليقك على الخدمة والتنفيذ") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = { showRatingDialog = false }, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                        Button(
                            onClick = {
                                repository.rateProject(project.id, ratingVal, reviewText)
                                showRatingDialog = false
                                Toast.makeText(context, "شكراً لتقييمك الرائع! ❤️", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("حفظ التقييم")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineItemCard(event: TimelineEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(ColorPrimaryOrange)
            )
            Box(modifier = Modifier.width(2.dp).height(38.dp).background(MaterialTheme.colorScheme.outline))
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(event.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(event.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
