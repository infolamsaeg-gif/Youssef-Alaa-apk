package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.config.AgencyConfig
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AgencyRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("my_agency_full_v2_prefs", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val scope = CoroutineScope(Dispatchers.IO)

    // Current Session
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // State collections
    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _feedbacks = MutableStateFlow<List<ClientFeedback>>(emptyList())
    val feedbacks: StateFlow<List<ClientFeedback>> = _feedbacks.asStateFlow()

    private val _invoices = MutableStateFlow<List<Invoice>>(emptyList())
    val invoices: StateFlow<List<Invoice>> = _invoices.asStateFlow()

    private val _tickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val tickets: StateFlow<List<SupportTicket>> = _tickets.asStateFlow()

    private val _orders = MutableStateFlow<List<ServiceOrder>>(emptyList())
    val orders: StateFlow<List<ServiceOrder>> = _orders.asStateFlow()

    private val _services = MutableStateFlow<List<ServiceItem>>(emptyList())
    val services: StateFlow<List<ServiceItem>> = _services.asStateFlow()

    private val _coupons = MutableStateFlow<List<DiscountCoupon>>(emptyList())
    val coupons: StateFlow<List<DiscountCoupon>> = _coupons.asStateFlow()

    private val _portfolio = MutableStateFlow<List<PortfolioItem>>(emptyList())
    val portfolio: StateFlow<List<PortfolioItem>> = _portfolio.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _faqs = MutableStateFlow<List<FAQItem>>(emptyList())
    val faqs: StateFlow<List<FAQItem>> = _faqs.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _isDarkMode.value = prefs.getBoolean(KEY_THEME_DARK, true)

        _users.value = readList(KEY_USERS, UserProfile::class.java, SeedData.getSeedUsers())
        _projects.value = readList(KEY_PROJECTS, Project::class.java, SeedData.getSeedProjects())
        _feedbacks.value = readList(KEY_FEEDBACKS, ClientFeedback::class.java, SeedData.getSeedFeedbacks())
        _invoices.value = readList(KEY_INVOICES, Invoice::class.java, SeedData.getSeedInvoices())
        _tickets.value = readList(KEY_TICKETS, SupportTicket::class.java, SeedData.getSeedTickets())
        _orders.value = readList(KEY_ORDERS, ServiceOrder::class.java, SeedData.getSeedOrders())
        _services.value = readList(KEY_SERVICES, ServiceItem::class.java, SeedData.getSeedServices())
        _coupons.value = readList(KEY_COUPONS, DiscountCoupon::class.java, SeedData.getSeedCoupons())
        _portfolio.value = readList(KEY_PORTFOLIO, PortfolioItem::class.java, SeedData.getSeedPortfolio())
        _notifications.value = readList(KEY_NOTIFICATIONS, AppNotification::class.java, SeedData.getSeedNotifications())
        _announcements.value = readList(KEY_ANNOUNCEMENTS, Announcement::class.java, SeedData.getSeedAnnouncements())
        _faqs.value = readList(KEY_FAQS, FAQItem::class.java, SeedData.getSeedFAQs())
        _auditLogs.value = readList(KEY_AUDIT_LOGS, AuditLog::class.java, SeedData.getSeedAuditLogs())

        // Auto-restore session if saved
        val savedUid = prefs.getString(KEY_LOGGED_IN_UID, null)
        if (!savedUid.isNullOrBlank()) {
            _currentUser.value = _users.value.find { it.uid == savedUid }
        } else {
            // Default active demo client
            _currentUser.value = _users.value.find { it.uid == "client_demo_1" }
        }
    }

    private fun <T> readList(key: String, clazz: Class<T>, fallback: List<T>): List<T> {
        val json = prefs.getString(key, null)
        if (json.isNullOrBlank()) return fallback
        return try {
            val type = Types.newParameterizedType(List::class.java, clazz)
            moshi.adapter<List<T>>(type).fromJson(json) ?: fallback
        } catch (e: Exception) {
            fallback
        }
    }

    private fun saveData() {
        scope.launch {
            try {
                writeList(KEY_USERS, UserProfile::class.java, _users.value)
                writeList(KEY_PROJECTS, Project::class.java, _projects.value)
                writeList(KEY_FEEDBACKS, ClientFeedback::class.java, _feedbacks.value)
                writeList(KEY_INVOICES, Invoice::class.java, _invoices.value)
                writeList(KEY_TICKETS, SupportTicket::class.java, _tickets.value)
                writeList(KEY_ORDERS, ServiceOrder::class.java, _orders.value)
                writeList(KEY_SERVICES, ServiceItem::class.java, _services.value)
                writeList(KEY_COUPONS, DiscountCoupon::class.java, _coupons.value)
                writeList(KEY_PORTFOLIO, PortfolioItem::class.java, _portfolio.value)
                writeList(KEY_NOTIFICATIONS, AppNotification::class.java, _notifications.value)
                writeList(KEY_ANNOUNCEMENTS, Announcement::class.java, _announcements.value)
                writeList(KEY_FAQS, FAQItem::class.java, _faqs.value)
                writeList(KEY_AUDIT_LOGS, AuditLog::class.java, _auditLogs.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun <T> writeList(key: String, clazz: Class<T>, list: List<T>) {
        val type = Types.newParameterizedType(List::class.java, clazz)
        val json = moshi.adapter<List<T>>(type).toJson(list)
        prefs.edit().putString(key, json).apply()
    }

    // Theme Management
    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        prefs.edit().putBoolean(KEY_THEME_DARK, isDark).apply()
    }

    // Authentication
    fun login(email: String, pass: String): Result<UserProfile> {
        val cleanEmail = email.trim().lowercase()

        // Admin fast check
        if (cleanEmail == AgencyConfig.ADMIN_EMAIL.lowercase() &&
            (pass == AgencyConfig.ADMIN_DEFAULT_PASS || pass == "admin")
        ) {
            var adminUser = _users.value.find { it.email.lowercase() == cleanEmail }
            if (adminUser == null) {
                adminUser = UserProfile(
                    uid = "admin_root",
                    email = AgencyConfig.ADMIN_EMAIL,
                    fullName = "مدير وكالة MY AGENCY",
                    phonePrimary = "01508664132",
                    phoneSecondary = "01151735927",
                    isAdmin = true,
                    adminRole = AdminRole.OWNER
                )
                _users.value = listOf(adminUser) + _users.value
            }
            _currentUser.value = adminUser
            prefs.edit().putString(KEY_LOGGED_IN_UID, adminUser.uid).apply()
            return Result.success(adminUser)
        }

        val found = _users.value.find { it.email.lowercase() == cleanEmail }
        return if (found != null) {
            if (!found.isActive) {
                return Result.failure(Exception("هذا الحساب معطّل مؤقتاً، يرجى مراجعة إدارة الوكالة."))
            }
            val updated = found.copy(lastSeenAt = System.currentTimeMillis())
            updateUserInternal(updated)
            _currentUser.value = updated
            prefs.edit().putString(KEY_LOGGED_IN_UID, updated.uid).apply()
            Result.success(updated)
        } else {
            Result.failure(Exception("البريد الإلكتروني أو كلمة المرور غير صحيحة."))
        }
    }

    fun register(
        fullName: String,
        phonePrimary: String,
        phoneSecondary: String,
        isPrimaryWhatsApp: Boolean,
        governorate: String,
        city: String,
        detailedAddress: String,
        email: String,
        password: String
    ): Result<UserProfile> {
        val cleanEmail = email.trim().lowercase()
        if (_users.value.any { it.email.lowercase() == cleanEmail }) {
            return Result.failure(Exception("هذا البريد الإلكتروني مسجّل بالفعل مسبقاً."))
        }
        if (phonePrimary.trim() == phoneSecondary.trim()) {
            return Result.failure(Exception("يجب أن يكون رقم الهاتف الثاني مختلفاً عن الأول."))
        }

        val newUid = generateRandomId("usr_")
        val newUser = UserProfile(
            uid = newUid,
            email = cleanEmail,
            fullName = fullName.trim(),
            phonePrimary = phonePrimary.trim(),
            phoneSecondary = phoneSecondary.trim(),
            isPrimaryWhatsApp = isPrimaryWhatsApp,
            governorate = governorate,
            city = city.trim(),
            detailedAddress = detailedAddress.trim(),
            createdAt = System.currentTimeMillis(),
            lastSeenAt = System.currentTimeMillis()
        )

        _users.value = listOf(newUser) + _users.value
        _currentUser.value = newUser
        prefs.edit().putString(KEY_LOGGED_IN_UID, newUid).apply()

        // Welcome Notification
        val notif = AppNotification(
            toUid = newUid,
            title = "مرحباً بك في MY AGENCY! 🎉",
            message = "تم إنشاء حسابك بنجاح، يسعدنا تنفيذ مشاريعك الإبداعية.",
            type = "welcome"
        )
        _notifications.value = listOf(notif) + _notifications.value

        addAuditLog("انضمام عميل جديد: ${newUser.fullName} ($cleanEmail)")
        saveData()
        return Result.success(newUser)
    }

    fun loginWithGoogleAccount(email: String, displayName: String, avatarUrl: String?): UserProfile {
        val cleanEmail = email.trim().lowercase()
        var user = _users.value.find { it.email.lowercase() == cleanEmail }

        if (user == null) {
            val newUid = generateRandomId("usr_g_")
            user = UserProfile(
                uid = newUid,
                email = cleanEmail,
                fullName = displayName,
                phonePrimary = "01000000000",
                phoneSecondary = "01100000000",
                avatarUrl = avatarUrl,
                createdAt = System.currentTimeMillis(),
                lastSeenAt = System.currentTimeMillis()
            )
            _users.value = listOf(user) + _users.value
        } else {
            user = user.copy(lastSeenAt = System.currentTimeMillis())
            updateUserInternal(user)
        }

        _currentUser.value = user
        prefs.edit().putString(KEY_LOGGED_IN_UID, user.uid).apply()
        saveData()
        return user
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove(KEY_LOGGED_IN_UID).apply()
    }

    fun updateProfile(updated: UserProfile) {
        updateUserInternal(updated)
        if (_currentUser.value?.uid == updated.uid) {
            _currentUser.value = updated
        }
        saveData()
    }

    fun deleteAccount(uid: String) {
        _users.value = _users.value.filterNot { it.uid == uid }
        if (_currentUser.value?.uid == uid) {
            logout()
        }
        saveData()
    }

    private fun updateUserInternal(user: UserProfile) {
        _users.value = _users.value.map { if (it.uid == user.uid) user else it }
    }

    // Projects CRUD
    fun addOrUpdateProject(project: Project) {
        val existingIndex = _projects.value.indexOfFirst { it.id == project.id }
        _projects.value = if (existingIndex >= 0) {
            _projects.value.toMutableList().apply { set(existingIndex, project.copy(updatedAt = System.currentTimeMillis())) }
        } else {
            listOf(project) + _projects.value
        }
        saveData()
    }

    fun deleteProject(projectId: String) {
        _projects.value = _projects.value.filterNot { it.id == projectId }
        saveData()
    }

    fun submitFeedback(
        projectId: String,
        projectTitle: String,
        type: String,
        text: String,
        fileUrl: String? = null
    ): Result<Unit> {
        val user = _currentUser.value ?: return Result.failure(Exception("غير مسجل"))
        val fb = ClientFeedback(
            clientUid = user.uid,
            projectId = projectId,
            projectTitle = projectTitle,
            type = type,
            text = text.take(1000).trim(),
            fileUrl = fileUrl,
            from = user.fullName,
            at = System.currentTimeMillis(),
            handled = false
        )
        _feedbacks.value = listOf(fb) + _feedbacks.value

        // If approved, update project status to DONE
        if (type == "approve") {
            val proj = _projects.value.find { it.id == projectId }
            if (proj != null) {
                addOrUpdateProject(proj.copy(status = ProjectStatus.DONE, progress = 100))
            }
        }

        saveData()
        return Result.success(Unit)
    }

    fun rateProject(projectId: String, rating: Int, review: String) {
        val proj = _projects.value.find { it.id == projectId } ?: return
        val updated = proj.copy(rating = rating.coerceIn(1, 5), reviewText = review.trim())
        addOrUpdateProject(updated)
    }

    // Invoices & Payments
    fun submitPaymentReceipt(
        invoiceId: String,
        amount: Double,
        method: String,
        receiptUrl: String,
        notes: String?
    ): Result<Unit> {
        val inv = _invoices.value.find { it.id == invoiceId }
            ?: return Result.failure(Exception("الفاتورة غير موجودة"))

        val receipt = PaymentReceipt(
            amount = amount,
            method = method,
            receiptUrl = receiptUrl,
            notes = notes,
            verified = false,
            timestamp = System.currentTimeMillis()
        )

        val updatedPayments = inv.payments + receipt
        val updatedInv = inv.copy(payments = updatedPayments)
        val idx = _invoices.value.indexOfFirst { it.id == invoiceId }
        _invoices.value = _invoices.value.toMutableList().apply { set(idx, updatedInv) }
        saveData()
        return Result.success(Unit)
    }

    fun verifyPayment(invoiceId: String, receiptId: String, accept: Boolean) {
        val inv = _invoices.value.find { it.id == invoiceId } ?: return
        val updatedPayments = inv.payments.map {
            if (it.id == receiptId) it.copy(verified = accept) else it
        }
        val totalVerified = updatedPayments.filter { it.verified }.sumOf { it.amount }
        val newStatus = when {
            totalVerified >= inv.total -> InvoiceStatus.PAID
            totalVerified > 0 -> InvoiceStatus.PARTIAL
            else -> inv.status
        }
        val updated = inv.copy(payments = updatedPayments, paid = totalVerified, status = newStatus)
        val idx = _invoices.value.indexOfFirst { it.id == invoiceId }
        _invoices.value = _invoices.value.toMutableList().apply { set(idx, updated) }
        saveData()
    }

    // Support Chat
    fun sendMessage(ticketId: String, text: String, isFromAdmin: Boolean, imageUrl: String? = null) {
        val ticket = _tickets.value.find { it.id == ticketId } ?: return
        val user = _currentUser.value
        val msg = ChatMessage(
            senderId = user?.uid ?: "sys",
            senderName = user?.fullName ?: "الدعم الفني",
            text = text.trim(),
            imageUrl = imageUrl,
            isFromAdmin = isFromAdmin,
            timestamp = System.currentTimeMillis()
        )
        val updatedTicket = ticket.copy(messages = ticket.messages + msg)
        val idx = _tickets.value.indexOfFirst { it.id == ticketId }
        _tickets.value = _tickets.value.toMutableList().apply { set(idx, updatedTicket) }
        saveData()
    }

    fun createTicket(clientUid: String, clientName: String, subject: String, firstMessage: String): SupportTicket {
        val newTicket = SupportTicket(
            clientUid = clientUid,
            clientName = clientName,
            subject = subject.trim(),
            messages = listOf(
                ChatMessage(
                    senderId = clientUid,
                    senderName = clientName,
                    text = firstMessage.trim(),
                    isFromAdmin = false,
                    timestamp = System.currentTimeMillis()
                )
            )
        )
        _tickets.value = listOf(newTicket) + _tickets.value
        saveData()
        return newTicket
    }

    // Service Orders
    fun createServiceOrder(
        clientUid: String,
        clientName: String,
        clientPhone: String,
        services: List<ServiceItem>,
        couponCode: String?,
        notes: String?
    ): ServiceOrder {
        var total = services.sumOf { it.price }
        if (!couponCode.isNullOrBlank()) {
            val coupon = _coupons.value.find { it.code.equals(couponCode.trim(), ignoreCase = true) }
            if (coupon != null) {
                total -= (total * (coupon.discountPercent / 100.0))
            }
        }

        val order = ServiceOrder(
            clientUid = clientUid,
            clientName = clientName,
            clientPhone = clientPhone,
            services = services,
            couponCode = couponCode,
            totalPrice = total.coerceAtLeast(0.0),
            notes = notes
        )
        _orders.value = listOf(order) + _orders.value
        addAuditLog("طلب خدمة جديد من: $clientName بقيمة $total ج.م")
        saveData()
        return order
    }

    // Notifications
    fun markAllNotificationsRead(toUid: String) {
        _notifications.value = _notifications.value.map {
            if (it.toUid == toUid) it.copy(read = true) else it
        }
        saveData()
    }

    private fun addAuditLog(action: String) {
        val user = _currentUser.value
        val log = AuditLog(
            actor = user?.fullName ?: "النظام",
            action = action,
            target = "المنظومة",
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }

    companion object {
        private const val KEY_THEME_DARK = "key_theme_dark"
        private const val KEY_LOGGED_IN_UID = "key_logged_in_uid"
        private const val KEY_USERS = "key_users_json"
        private const val KEY_PROJECTS = "key_projects_json"
        private const val KEY_FEEDBACKS = "key_feedbacks_json"
        private const val KEY_INVOICES = "key_invoices_json"
        private const val KEY_TICKETS = "key_tickets_json"
        private const val KEY_ORDERS = "key_orders_json"
        private const val KEY_SERVICES = "key_services_json"
        private const val KEY_COUPONS = "key_coupons_json"
        private const val KEY_PORTFOLIO = "key_portfolio_json"
        private const val KEY_NOTIFICATIONS = "key_notifs_json"
        private const val KEY_ANNOUNCEMENTS = "key_announcements_json"
        private const val KEY_FAQS = "key_faqs_json"
        private const val KEY_AUDIT_LOGS = "key_audit_logs_json"
    }
}
