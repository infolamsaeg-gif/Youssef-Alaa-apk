package com.example.ui.screens.client

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.config.AgencyConfig
import com.example.data.model.*
import com.example.data.repository.AgencyRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.*

@Composable
fun ClientInvoicesScreen(
    user: UserProfile,
    repository: AgencyRepository
) {
    val context = LocalContext.current
    val invoices by repository.invoices.collectAsState()
    val myInvoices = remember(invoices, user.uid) { invoices.filter { it.clientUid == user.uid } }

    var selectedInvoiceForReceipt by remember { mutableStateOf<Invoice?>(null) }
    var selectedInvoiceForPrint by remember { mutableStateOf<Invoice?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Title & Header
        item {
            Text(
                text = "الفواتير والمدفوعات 💳",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "متابعة حسابات مشاريعك، طرق الدفع المتاحة، ورفع إيصالات التحويل",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Payment Methods Instructions Card
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
                    Text(
                        text = "طرق الدفع المعتمدة في مصر 🇪🇬",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Vodafone Cash
                    PaymentMethodRow(
                        title = "فودافون كاش (Vodafone Cash)",
                        detail = AgencyConfig.VODAFONE_CASH_NUMBER,
                        instruction = "تحويل على رقم المحفظة ثم الضغط على نسخ",
                        onCopy = {
                            copyToClipboard(context, "Vodafone Cash", AgencyConfig.VODAFONE_CASH_NUMBER)
                        }
                    )

                    // InstaPay
                    PaymentMethodRow(
                        title = "إنستاباي (InstaPay)",
                        detail = AgencyConfig.INSTAPAY_HANDLE,
                        instruction = "تحويل مباشر ولحظي عبر عنوان الدفع IPA",
                        onCopy = {
                            copyToClipboard(context, "InstaPay", AgencyConfig.INSTAPAY_HANDLE)
                        }
                    )

                    // Bank Account
                    PaymentMethodRow(
                        title = "التحويل البنكي (Bank Transfer)",
                        detail = AgencyConfig.BANK_IBAN,
                        instruction = "الحساب: ${AgencyConfig.BANK_ACCOUNT_NAME}",
                        onCopy = {
                            copyToClipboard(context, "IBAN", AgencyConfig.BANK_IBAN)
                        }
                    )
                }
            }
        }

        // Invoices List
        if (myInvoices.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.ReceiptLong,
                    title = "لا توجد فواتير مسجلة لحسابك حالياً",
                    message = "يتم إصدار الفواتير الرسمية تلقائياً عند طلب الخدمات أو بدء المشاريع."
                )
            }
        } else {
            items(myInvoices) { inv ->
                InvoiceCard(
                    invoice = inv,
                    onUploadReceipt = { selectedInvoiceForReceipt = inv },
                    onPrintInvoice = { selectedInvoiceForPrint = inv }
                )
            }
        }
    }

    // Modal: Upload Payment Receipt
    if (selectedInvoiceForReceipt != null) {
        UploadReceiptDialog(
            invoice = selectedInvoiceForReceipt!!,
            onDismiss = { selectedInvoiceForReceipt = null },
            onSubmit = { amount, method, url, notes ->
                repository.submitPaymentReceipt(
                    invoiceId = selectedInvoiceForReceipt!!.id,
                    amount = amount,
                    method = method,
                    receiptUrl = url,
                    notes = notes
                )
                selectedInvoiceForReceipt = null
                Toast.makeText(context, "تم رفع إيصال الدفع بنجاح وجاري المراجعة ✅", Toast.LENGTH_LONG).show()
            }
        )
    }

    // Modal: View / Print Invoice
    if (selectedInvoiceForPrint != null) {
        PrintableInvoiceDialog(
            invoice = selectedInvoiceForPrint!!,
            onDismiss = { selectedInvoiceForPrint = null }
        )
    }
}

@Composable
fun PaymentMethodRow(
    title: String,
    detail: String,
    instruction: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
            Text(detail, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
            Text(instruction, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onCopy) {
            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: Invoice,
    onUploadReceipt: () -> Unit,
    onPrintInvoice: () -> Unit
) {
    val (statusBg, statusText, statusIcon) = when (invoice.status) {
        InvoiceStatus.PAID -> Triple(ColorSuccess.copy(alpha = 0.15f), ColorSuccess, Icons.Default.CheckCircle)
        InvoiceStatus.PARTIAL -> Triple(ColorWarning.copy(alpha = 0.15f), ColorWarning, Icons.Default.Pending)
        InvoiceStatus.PENDING -> Triple(ColorPrimaryOrange.copy(alpha = 0.15f), ColorPrimaryOrange, Icons.Default.Schedule)
        InvoiceStatus.OVERDUE -> Triple(ColorDanger.copy(alpha = 0.15f), ColorDanger, Icons.Default.Warning)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(BorderDark, ColorSurfaceElevated)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(invoice.id, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("الاستحقاق: ${invoice.dueDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(statusIcon, contentDescription = null, tint = statusText, modifier = Modifier.size(14.dp))
                        Text(invoice.status.titleAr, color = statusText, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            // Items breakdown
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                invoice.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${item.unitPrice.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("الإجمالي: ${invoice.total.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("المسدد: ${invoice.paid.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.bodySmall, color = ColorSuccess)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onPrintInvoice, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("عرض")
                    }

                    if (invoice.status != InvoiceStatus.PAID) {
                        Button(
                            onClick = onUploadReceipt,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("رفع إيصال")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UploadReceiptDialog(
    invoice: Invoice,
    onDismiss: () -> Unit,
    onSubmit: (Double, String, String, String?) -> Unit
) {
    var amountText by remember { mutableStateOf("${(invoice.total - invoice.paid).toInt()}") }
    var selectedMethod by remember { mutableStateOf("فودافون كاش") }
    var receiptUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=600&q=80") }
    var notes by remember { mutableStateOf("") }

    val methods = listOf("فودافون كاش", "إنستاباي (InstaPay)", "تحويل بنكي")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("رفع إيصال سداد الفاتورة 🧾", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("المبلغ المحول (ج.م) *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Text("طريقة التحويل المستخدمة:", style = MaterialTheme.typography.labelSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    methods.forEach { method ->
                        val isSelected = selectedMethod == method
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMethod = method },
                            label = { Text(method, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = receiptUrl,
                    onValueChange = { receiptUrl = it },
                    label = { Text("رابط صورة الإيصال أو الرفع *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات ورقم المحول منه (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    shape = RoundedCornerShape(10.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0 && receiptUrl.isNotBlank()) {
                                onSubmit(amt, selectedMethod, receiptUrl.trim(), notes.trim().ifBlank { null })
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("تأكيد الإرسال")
                    }
                }
            }
        }
    }
}

@Composable
fun PrintableInvoiceDialog(
    invoice: Invoice,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(AgencyConfig.AGENCY_NAME, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                        Text("فاتورة إلكترونية معتمدة", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "إغلاق") }
                }

                HorizontalDivider()

                Text("رقم الفاتورة: ${invoice.id}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                Text("العميل: ${invoice.clientName}", style = MaterialTheme.typography.bodySmall)
                Text("تاريخ الاستحقاق: ${invoice.dueDate}", style = MaterialTheme.typography.bodySmall)

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(invoice.items) { item ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${item.title} (x${item.quantity})", style = MaterialTheme.typography.bodySmall)
                            Text("${(item.unitPrice * item.quantity).toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                HorizontalDivider()

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("المبلغ الإجمالي المطلوب:", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Text("${invoice.total.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                }

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("إغلاق الفاتورة")
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
    Toast.makeText(context, "تم نسخ $label: $text", Toast.LENGTH_SHORT).show()
}
