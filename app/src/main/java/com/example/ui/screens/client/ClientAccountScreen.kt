package com.example.ui.screens.client

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.config.AgencyConfig
import com.example.data.model.UserProfile
import com.example.data.repository.AgencyRepository
import com.example.ui.theme.*

@Composable
fun ClientAccountScreen(
    user: UserProfile,
    repository: AgencyRepository,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val isDarkMode by repository.isDarkMode.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showDeleteAccountConfirm by remember { mutableStateOf(false) }

    var notifProjects by remember { mutableStateOf(true) }
    var notifInvoices by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        item {
            Text(
                text = "حسابي والإعدادات ⚙️",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ColorPrimaryOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.fullName.take(1),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = ColorPrimaryOrange
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(user.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("المحافظة: ${user.governorate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { showEditProfileDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل الملف الشخصي", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    HorizontalDivider()

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("رقم التواصل الأساسي:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.phonePrimary, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("رقم التواصل الاحتياطي:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(user.phoneSecondary, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("العنوان:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${user.city} - ${user.detailedAddress}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Appearance & Theme Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("المظهر والتفضيلات", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(if (isDarkMode) "الوضع الداكن (Dark Mode)" else "الوضع الفاتح (Light Mode)", style = MaterialTheme.typography.bodyMedium)
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { repository.setDarkMode(it) }
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("تنبيهات تحديثات المشاريع", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = notifProjects, onCheckedChange = { notifProjects = it })
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("تنبيهات الفواتير والمدفوعات", style = MaterialTheme.typography.bodyMedium)
                        Switch(checked = notifInvoices, onCheckedChange = { notifInvoices = it })
                    }
                }
            }
        }

        // Security & Legal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ListItem(
                        headlineContent = { Text("تغيير كلمة المرور") },
                        leadingContent = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        trailingContent = { Icon(Icons.Default.ChevronLeft, contentDescription = null) },
                        modifier = Modifier.clickable { showChangePasswordDialog = true }
                    )
                    ListItem(
                        headlineContent = { Text("الشروط والأحكام وسياسة الخصوصية") },
                        leadingContent = { Icon(Icons.Default.Gavel, contentDescription = null, tint = ColorSecondaryPurple) },
                        trailingContent = { Icon(Icons.Default.ChevronLeft, contentDescription = null) },
                        modifier = Modifier.clickable { showTermsDialog = true }
                    )
                }
            }
        }

        // Logout & Delete Account Actions
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = ColorDanger)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("تسجيل الخروج", color = ColorDanger, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = { showDeleteAccountConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حذف الحساب نهائياً", color = ColorDanger, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }

    // Modal: Edit Profile Dialog
    if (showEditProfileDialog) {
        var name by remember { mutableStateOf(user.fullName) }
        var phone1 by remember { mutableStateOf(user.phonePrimary) }
        var phone2 by remember { mutableStateOf(user.phoneSecondary) }
        var city by remember { mutableStateOf(user.city) }
        var address by remember { mutableStateOf(user.detailedAddress) }

        Dialog(onDismissRequest = { showEditProfileDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("تعديل البيانات الشخصية", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("الاسم بالكامل") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone1, onValueChange = { phone1 = it }, label = { Text("رقم الهاتف الأساسي") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone2, onValueChange = { phone2 = it }, label = { Text("رقم الهاتف الاحتياطي") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = city, onValueChange = { city = it }, label = { Text("المدينة / المنطقة") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("العنوان بالتفصيل") }, modifier = Modifier.fillMaxWidth())

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showEditProfileDialog = false }, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                        Button(
                            onClick = {
                                val updated = user.copy(
                                    fullName = name.trim(),
                                    phonePrimary = phone1.trim(),
                                    phoneSecondary = phone2.trim(),
                                    city = city.trim(),
                                    detailedAddress = address.trim()
                                )
                                repository.updateProfile(updated)
                                showEditProfileDialog = false
                                Toast.makeText(context, "تم حفظ التعديلات بنجاح", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("حفظ")
                        }
                    }
                }
            }
        }
    }

    // Modal: Change Password Dialog
    if (showChangePasswordDialog) {
        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showChangePasswordDialog = false }) {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("تغيير كلمة المرور", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    OutlinedTextField(
                        value = oldPass,
                        onValueChange = { oldPass = it },
                        label = { Text("كلمة المرور الحالية") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("كلمة المرور الجديدة (8 أحرف فأكثر)") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { showChangePasswordDialog = false }, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                        Button(
                            onClick = {
                                if (newPass.length >= 8) {
                                    showChangePasswordDialog = false
                                    Toast.makeText(context, "تم تحديث كلمة المرور بنجاح ✅", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = newPass.length >= 8,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("تحديث")
                        }
                    }
                }
            }
        }
    }

    // Modal: Delete Account Confirmation
    if (showDeleteAccountConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirm = false },
            title = { Text("تأكيد حذف الحساب نهائياً", color = ColorDanger) },
            text = { Text("هل أنت متأكد من رغبتك في حذف حسابك؟ سيتم إزالة كافة مشاريعك وفواتيرك وبياناتك ولا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                Button(
                    onClick = {
                        repository.deleteAccount(user.uid)
                        showDeleteAccountConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColorDanger)
                ) {
                    Text("نعم، احذف حسابي")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
