package com.example.data.model

import java.security.SecureRandom

enum class ProjectType(val key: String, val titleAr: String) {
    DESIGN("design", "تصميم جرافيك"),
    VIDEO("video", "مونتاج وفيديو"),
    AI_VIDEO("ai_video", "AI Video ذكاء اصطناعي"),
    WEB("web", "موقع ويب"),
    STORE("store", "متجر إلكتروني"),
    OTHER("other", "خدمة أخرى");

    companion object {
        fun fromKey(key: String): ProjectType =
            entries.find { it.key.equals(key, ignoreCase = true) } ?: OTHER
    }
}

enum class ProjectStatus(val key: String, val titleAr: String, val stepIndex: Int) {
    NEW("new", "طلب جديد", 0),
    WORKING("working", "قيد التنفيذ", 1),
    REVIEW("review", "بانتظار مراجعتك", 2),
    DONE("done", "تم الاعتماد", 3);

    companion object {
        fun fromKey(key: String): ProjectStatus =
            entries.find { it.key.equals(key, ignoreCase = true) } ?: NEW
    }
}

enum class FileKind(val key: String, val titleAr: String) {
    IMAGE("image", "صورة"),
    VIDEO("video", "فيديو"),
    PDF("pdf", "مستند PDF"),
    LINK("link", "رابط خارجي");

    companion object {
        fun detectFromUrl(url: String): FileKind {
            val lower = url.lowercase()
            return when {
                lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") ||
                        lower.endsWith(".webp") || lower.endsWith(".gif") || lower.contains("image") -> IMAGE
                lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".webm") ||
                        lower.endsWith(".mkv") || lower.contains("video") -> VIDEO
                lower.endsWith(".pdf") -> PDF
                else -> LINK
            }
        }
    }
}

enum class InvoiceStatus(val key: String, val titleAr: String) {
    PENDING("pending", "مستحقة"),
    PARTIAL("partial", "مدفوعة جزئياً"),
    PAID("paid", "مدفوعة بالكامل"),
    OVERDUE("overdue", "متأخرة")
}

enum class TicketStatus(val key: String, val titleAr: String) {
    OPEN("open", "مفتوحة"),
    IN_PROGRESS("in_progress", "قيد المعالجة"),
    CLOSED("closed", "مغلقة")
}

enum class OrderStatus(val key: String, val titleAr: String) {
    NEW("new", "طلب جديد"),
    CONTACTED("contacted", "تم التواصل"),
    ACCEPTED("accepted", "تم القبول والبدء"),
    REJECTED("rejected", "مرفوض")
}

enum class AdminRole(val key: String, val titleAr: String) {
    OWNER("owner", "مالك (صلاحيات كاملة)"),
    MANAGER("manager", "مدير (بدون الإعدادات المالية)"),
    SUPPORT("support", "دعم فني وعملاء")
}

data class UserProfile(
    val uid: String,
    val email: String,
    val fullName: String,
    val phonePrimary: String, // 01XXXXXXXXX
    val phoneSecondary: String, // 01XXXXXXXXX
    val isPrimaryWhatsApp: Boolean = true,
    val governorate: String = "القاهرة",
    val city: String = "",
    val detailedAddress: String = "",
    val avatarUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastSeenAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val tags: List<String> = emptyList(),
    val adminNotes: String? = null,
    val isAdmin: Boolean = false,
    val adminRole: AdminRole = AdminRole.SUPPORT
)

data class ProjectFile(
    val id: String = generateRandomId("f_"),
    val name: String,
    val url: String,
    val kind: FileKind = FileKind.detectFromUrl(url)
)

data class TimelineEvent(
    val id: String = generateRandomId("tl_"),
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Project(
    val id: String = generateRandomId("p_"),
    val clientUid: String,
    val title: String,
    val type: ProjectType = ProjectType.DESIGN,
    val status: ProjectStatus = ProjectStatus.WORKING,
    val progress: Int = 0,
    val deadline: String? = null,
    val price: Double = 0.0,
    val description: String? = null,
    val files: List<ProjectFile> = emptyList(),
    val timeline: List<TimelineEvent> = emptyList(),
    val rating: Int? = null, // 1-5
    val reviewText: String? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

data class ClientFeedback(
    val id: String = generateRandomId("fb_"),
    val clientUid: String,
    val projectId: String,
    val projectTitle: String,
    val type: String, // "approve" or "revise"
    val text: String,
    val fileUrl: String? = null,
    val from: String,
    val at: Long = System.currentTimeMillis(),
    val handled: Boolean = false
)

data class InvoiceItem(
    val title: String,
    val quantity: Int = 1,
    val unitPrice: Double
)

data class PaymentReceipt(
    val id: String = generateRandomId("rec_"),
    val amount: Double,
    val method: String, // Vodafone Cash, InstaPay, Bank
    val receiptUrl: String,
    val notes: String? = null,
    val verified: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class Invoice(
    val id: String = generateRandomId("INV-2026-"),
    val clientUid: String,
    val clientName: String,
    val items: List<InvoiceItem> = emptyList(),
    val subtotal: Double,
    val discount: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double,
    val paid: Double = 0.0,
    val status: InvoiceStatus = InvoiceStatus.PENDING,
    val dueDate: String,
    val createdAt: Long = System.currentTimeMillis(),
    val payments: List<PaymentReceipt> = emptyList()
)

data class ChatMessage(
    val id: String = generateRandomId("msg_"),
    val senderId: String,
    val senderName: String,
    val text: String,
    val imageUrl: String? = null,
    val isFromAdmin: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
)

data class SupportTicket(
    val id: String = generateRandomId("tkt_"),
    val clientUid: String,
    val clientName: String,
    val subject: String,
    val status: TicketStatus = TicketStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val messages: List<ChatMessage> = emptyList()
)

data class ServiceItem(
    val id: String = generateRandomId("srv_"),
    val name: String,
    val price: Double,
    val oldPrice: Double? = null,
    val description: String = "",
    val iconName: String = "design",
    val isVisible: Boolean = true
)

data class DiscountCoupon(
    val code: String,
    val discountPercent: Double,
    val expiresAt: String,
    val usedCount: Int = 0,
    val maxUses: Int = 100
)

data class ServiceOrder(
    val id: String = generateRandomId("ord_"),
    val clientUid: String,
    val clientName: String,
    val clientPhone: String,
    val services: List<ServiceItem>,
    val couponCode: String? = null,
    val totalPrice: Double,
    val notes: String? = null,
    val status: OrderStatus = OrderStatus.NEW,
    val createdAt: Long = System.currentTimeMillis()
)

data class PortfolioItem(
    val id: String = generateRandomId("port_"),
    val title: String,
    val type: ProjectType = ProjectType.DESIGN,
    val url: String,
    val thumb: String? = null,
    val isPinned: Boolean = false
)

data class AppNotification(
    val id: String = generateRandomId("notif_"),
    val toUid: String,
    val title: String,
    val message: String,
    val type: String, // project, invoice, support, promo
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
)

data class Announcement(
    val id: String = generateRandomId("ann_"),
    val title: String,
    val content: String,
    val type: String = "banner", // banner or modal
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class FAQItem(
    val id: String = generateRandomId("faq_"),
    val question: String,
    val answer: String,
    val category: String = "عام"
)

data class AuditLog(
    val id: String = generateRandomId("aud_"),
    val actor: String,
    val action: String,
    val target: String,
    val timestamp: Long = System.currentTimeMillis()
)

private val secureRandom = SecureRandom()

fun generateRandomId(prefix: String = ""): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    val sb = StringBuilder(prefix)
    repeat(8) {
        sb.append(chars[secureRandom.nextInt(chars.length)])
    }
    return sb.toString()
}
