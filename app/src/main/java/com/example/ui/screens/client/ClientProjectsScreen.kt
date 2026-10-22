package com.example.ui.screens.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Project
import com.example.data.model.ProjectStatus
import com.example.data.model.UserProfile
import com.example.data.repository.AgencyRepository
import com.example.ui.components.CustomProgressBar
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StatusBadge
import com.example.ui.components.TypeBadge
import com.example.ui.theme.*

@Composable
fun ClientProjectsScreen(
    user: UserProfile,
    repository: AgencyRepository,
    onOpenProject: (String) -> Unit,
    onNavigateToServices: () -> Unit
) {
    val projects by repository.projects.collectAsState()
    val myProjects = remember(projects, user.uid) { projects.filter { it.clientUid == user.uid } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<ProjectStatus?>(null) }

    val filteredProjects = remember(myProjects, searchQuery, selectedStatusFilter) {
        myProjects.filter { proj ->
            val matchesSearch = searchQuery.isBlank() || proj.title.contains(searchQuery, ignoreCase = true) ||
                    (proj.description?.contains(searchQuery, ignoreCase = true) == true)
            val matchesStatus = selectedStatusFilter == null || proj.status == selectedStatusFilter
            matchesSearch && matchesStatus
        }
    }

    val statusFilters = listOf(
        Pair(null, "الكل (${myProjects.size})"),
        Pair(ProjectStatus.REVIEW, "بانتظار مراجعتك (${myProjects.count { it.status == ProjectStatus.REVIEW }})"),
        Pair(ProjectStatus.WORKING, "قيد التنفيذ (${myProjects.count { it.status == ProjectStatus.WORKING }})"),
        Pair(ProjectStatus.DONE, "تم الاعتماد (${myProjects.count { it.status == ProjectStatus.DONE }})"),
        Pair(ProjectStatus.NEW, "جديد")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "مشاريعي 📁",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "متابعة تطور الأعمال، مراحل الإنجاز، والمعاينات الفورية",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في أسماء المشاريع أو الوصف...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Filter Pills
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statusFilters) { (st, label) ->
                    val isSelected = selectedStatusFilter == st
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = st },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        if (filteredProjects.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.FolderOpen,
                    title = if (myProjects.isEmpty()) "لا توجد مشاريع مضافة حتى الآن" else "لا توجد نتائج مطابقة لبحثك",
                    message = if (myProjects.isEmpty()) "يمكنك طلب خدمات جديدة لبدء مشروعك الأول فوراً." else "جرّب تغيير كلمات البحث أو فلتر الحالة.",
                    actionLabel = if (myProjects.isEmpty()) "طلب خدمة جديدة ⚡" else null,
                    onAction = if (myProjects.isEmpty()) onNavigateToServices else null
                )
            }
        } else {
            items(filteredProjects) { proj ->
                ClientProjectItemCard(
                    project = proj,
                    onClick = { onOpenProject(proj.id) }
                )
            }
        }
    }
}

@Composable
fun ClientProjectItemCard(
    project: Project,
    onClick: () -> Unit
) {
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

            Text(project.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

            if (!project.description.isNullOrBlank()) {
                Text(project.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }

            CustomProgressBar(progress = project.progress)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Text("${project.files.size} ملفات", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("عرض التفاصيل", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
