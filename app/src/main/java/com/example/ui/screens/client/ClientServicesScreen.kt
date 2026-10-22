package com.example.ui.screens.client

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.config.AgencyConfig
import com.example.data.model.*
import com.example.data.repository.AgencyRepository
import com.example.ui.components.LightboxModal
import com.example.ui.components.TypeBadge
import com.example.ui.theme.*
import java.net.URLEncoder

@Composable
fun ClientServicesScreen(
    user: UserProfile,
    repository: AgencyRepository
) {
    val context = LocalContext.current
    val services by repository.services.collectAsState()
    val portfolio by repository.portfolio.collectAsState()
    val coupons by repository.coupons.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Services, 1: Portfolio
    val selectedServiceIds = remember { mutableStateListOf<String>() }
    var couponInput by remember { mutableStateOf("") }
    var appliedCoupon by remember { mutableStateOf<DiscountCoupon?>(null) }
    var couponError by remember { mutableStateOf<String?>(null) }
    var orderNotes by remember { mutableStateOf("") }

    // Portfolio filters
    var selectedPortfolioFilter by remember { mutableStateOf<ProjectType?>(null) }
    var lightboxUrl by remember { mutableStateOf<String?>(null) }
    var lightboxTitle by remember { mutableStateOf("") }

    val selectedServices = services.filter { selectedServiceIds.contains(it.id) }
    val subtotal = selectedServices.sumOf { it.price }
    val discountAmount = if (appliedCoupon != null) subtotal * (appliedCoupon!!.discountPercent / 100.0) else 0.0
    val finalTotal = (subtotal - discountAmount).coerceAtLeast(0.0)

    fun sendOrderToWhatsApp() {
        if (selectedServices.isEmpty()) {
            Toast.makeText(context, "يرجى اختيار خدمة واحدة على الأقل.", Toast.LENGTH_SHORT).show()
            return
        }

        // Save order in local repository
        val order = repository.createServiceOrder(
            clientUid = user.uid,
            clientName = user.fullName,
            clientPhone = user.phonePrimary,
            services = selectedServices,
            couponCode = appliedCoupon?.code,
            notes = orderNotes.ifBlank { null }
        )

        val servicesListText = selectedServices.mapIndexed { idx, s ->
            "${idx + 1}. *${s.name}* - ${s.price.toInt()} ${AgencyConfig.CURRENCY}"
        }.joinToString("\n")

        val message = """
            مرحباً فريق MY AGENCY 👋
            أرغب في طلب خدمات جديدة:
            
            👤 *الاسم:* ${user.fullName}
            📱 *الهاتف:* ${user.phonePrimary}
            📍 *العنوان:* ${user.governorate} - ${user.city}
            
            📦 *الخدمات المختارة:*
            $servicesListText
            
            ${if (appliedCoupon != null) "🏷️ *كوبون الخصم:* ${appliedCoupon!!.code} (خصم ${appliedCoupon!!.discountPercent.toInt()}%)" else ""}
            💰 *الإجمالي النهائي:* ${finalTotal.toInt()} ${AgencyConfig.CURRENCY}
            
            📝 *ملاحظات إضافية:*
            ${if (orderNotes.isNotBlank()) orderNotes else "لا توجد ملاحظات خاصة"}
            
            رقم الطلب في المنظومة: #${order.id}
        """.trimIndent()

        try {
            val url = "https://wa.me/${AgencyConfig.WHATSAPP_PRIMARY}?text=${URLEncoder.encode(message, "UTF-8")}"
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            Toast.makeText(context, "تم حفظ الطلب وتحويلك للواتساب للتأكيد 🚀", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "تم حفظ الطلب بنجاح، تعذر فتح تطبيق واتساب.", Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Switcher Tab
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("باقات وخدمات الوكالة ⚡") })
            Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("معرض أعمالنا 🎨") })
        }

        if (activeTab == 0) {
            // SERVICES CATALOG
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                item {
                    Text(
                        text = "اختر الخدمات والمواصفات المطلوبة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "يمكنك اختيار أكثر من خدمة واستخدام كوبون الخصم لاحتساب الإجمالي لحظياً",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(services) { srv ->
                    val isSelected = selectedServiceIds.contains(srv.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                if (isSelected) selectedServiceIds.remove(srv.id) else selectedServiceIds.add(srv.id)
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (isSelected) BrandGradient else Brush.horizontalGradient(listOf(BorderDark, ColorSurfaceElevated)),
                            width = if (isSelected) 1.5.dp else 1.dp
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (isSelected) selectedServiceIds.remove(srv.id) else selectedServiceIds.add(srv.id)
                                }
                            )

                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(srv.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                if (srv.description.isNotBlank()) {
                                    Text(srv.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("${srv.price.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                    if (srv.oldPrice != null && srv.oldPrice > srv.price) {
                                        Text("${srv.oldPrice.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.bodySmall.copy(textDecoration = TextDecoration.LineThrough), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Code Box
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("كود الخصم (Coupon)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it.uppercase() },
                                    placeholder = { Text("مثال: WELCOME20") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Button(
                                    onClick = {
                                        val match = coupons.find { it.code.equals(couponInput.trim(), ignoreCase = true) }
                                        if (match != null) {
                                            appliedCoupon = match
                                            couponError = null
                                            Toast.makeText(context, "تم تطبيق خصم ${match.discountPercent.toInt()}% 🎉", Toast.LENGTH_SHORT).show()
                                        } else {
                                            couponError = "كود الخصم غير صالح أو منتهي."
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("تطبيق")
                                }
                            }
                            if (appliedCoupon != null) {
                                Text("تم تطبيق الكود: ${appliedCoupon!!.code} بنجاح!", color = ColorSuccess, style = MaterialTheme.typography.labelSmall)
                            }
                            if (couponError != null) {
                                Text(couponError!!, color = ColorDanger, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Order Notes Field
                item {
                    OutlinedTextField(
                        value = orderNotes,
                        onValueChange = { orderNotes = it },
                        label = { Text("أي تفاصيل أو متطلبات خاصة بالطلب؟") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Total Summary & Submit Button
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder().copy(brush = BrandGradient)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("المجموع الفرعي:", style = MaterialTheme.typography.bodySmall)
                                Text("${subtotal.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (discountAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("الخصم (${appliedCoupon?.discountPercent?.toInt()}%):", style = MaterialTheme.typography.bodySmall, color = ColorSuccess)
                                    Text("-${discountAmount.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.bodySmall, color = ColorSuccess)
                                }
                            }
                            HorizontalDivider()
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("الإجمالي المطلوب:", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${finalTotal.toInt()} ${AgencyConfig.CURRENCY}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                            }

                            Button(
                                onClick = { sendOrderToWhatsApp() },
                                enabled = selectedServices.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("إرسال وتأكيد الطلب عبر واتساب 🚀", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // PORTFOLIO GALLERY
            val filterOptions = listOf(
                Pair(null, "الكل"),
                Pair(ProjectType.DESIGN, "تصميم"),
                Pair(ProjectType.AI_VIDEO, "AI فيديو"),
                Pair(ProjectType.WEB, "مواقع ويب"),
                Pair(ProjectType.STORE, "متاجر")
            )

            val filteredPortfolio = remember(portfolio, selectedPortfolioFilter) {
                if (selectedPortfolioFilter == null) portfolio
                else portfolio.filter { it.type == selectedPortfolioFilter }
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { (type, label) ->
                    val isSelected = selectedPortfolioFilter == type
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPortfolioFilter = type },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(filteredPortfolio) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                lightboxUrl = item.thumb ?: item.url
                                lightboxTitle = item.title
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                                AsyncImage(
                                    model = item.thumb ?: item.url,
                                    contentDescription = item.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp)) {
                                    TypeBadge(type = item.type)
                                }
                            }
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(10.dp),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }

    if (lightboxUrl != null) {
        LightboxModal(imageUrl = lightboxUrl!!, title = lightboxTitle, onDismiss = { lightboxUrl = null })
    }
}
