package com.example.data.repository

import com.example.config.AgencyConfig
import com.example.data.model.*

object SeedData {

    fun getSeedUsers(): List<UserProfile> = listOf(
        UserProfile(
            uid = "client_demo_1",
            email = "demo@myagency.com",
            fullName = "م. أحمد عبد الرحمن (شركة الأفق)",
            phonePrimary = "01012345678",
            phoneSecondary = "01198765432",
            isPrimaryWhatsApp = true,
            governorate = "القاهرة",
            city = "التجمع الخامس",
            detailedAddress = "شارع التسعين الشمالي، مجمع الأعمال مبنى B",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&q=80",
            createdAt = 1729000000000L,
            lastSeenAt = System.currentTimeMillis(),
            tags = listOf("عميل VIP", "عقد سنوي")
        ),
        UserProfile(
            uid = "admin_root",
            email = AgencyConfig.ADMIN_EMAIL,
            fullName = "مدير وكالة MY AGENCY",
            phonePrimary = "01508664132",
            phoneSecondary = "01151735927",
            governorate = "الجيزة",
            city = "الشيخ زايد",
            detailedAddress = "المقر الإداري لـ MY AGENCY",
            isAdmin = true,
            adminRole = AdminRole.OWNER
        )
    )

    fun getSeedServices(): List<ServiceItem> = listOf(
        ServiceItem(
            id = "srv_ai_video",
            name = "فيديو إعلاني بالذكاء الاصطناعي (AI Video)",
            price = 75.0,
            oldPrice = 100.0,
            description = "فيديو سينمائي 30 ثانية عالي الجودة بحجم ريلز وسوشيال ميديا مع تعليق صوتي ومؤثرات صوتية هوليوودية.",
            iconName = "video"
        ),
        ServiceItem(
            id = "srv_logo",
            name = "تصميم لوجو احترافي (Logo Identity)",
            price = 50.0,
            oldPrice = 100.0,
            description = "3 نماذج مميزة وفريدة غير مكررة مع تسليم كافة الملفات المصدرية ودليل الألوان والخطوط.",
            iconName = "design"
        ),
        ServiceItem(
            id = "srv_social_logo",
            name = "باقة سوشيال ميديا متكاملة + لوجو",
            price = 100.0,
            oldPrice = 200.0,
            description = "هوية بصرية متكاملة بالإضافة إلى 12 تصميم إعلاني تفاعلي لمنصات إنستغرام وتيك توك وفيسبوك.",
            iconName = "design"
        ),
        ServiceItem(
            id = "srv_personal_web",
            name = "تصميم وبرمجة موقع شخصي (Portfolio)",
            price = 150.0,
            oldPrice = 300.0,
            description = "موقع تعريفي فائق السرعة متوافق مع كافة الشاشات مع دومين واستضافة وحماية SSL مجاناً.",
            iconName = "web"
        ),
        ServiceItem(
            id = "srv_ecommerce",
            name = "متجر إلكتروني متكامل (E-Commerce Store)",
            price = 350.0,
            oldPrice = 500.0,
            description = "متجر بيع وشراء متكامل مع ربط بوابات الدفع، شركات الشحن بالمحافظات، وتنبيهات الطلبات عبر واتساب.",
            iconName = "store"
        )
    )

    fun getSeedCoupons(): List<DiscountCoupon> = listOf(
        DiscountCoupon(code = "WELCOME20", discountPercent = 20.0, expiresAt = "2026-11-01"),
        DiscountCoupon(code = "AGENCY10", discountPercent = 10.0, expiresAt = "2026-12-31")
    )

    fun getSeedProjects(): List<Project> = listOf(
        Project(
            id = "p_101",
            clientUid = "client_demo_1",
            title = "تصميم هوية بصرية كاملة ودليل العلامة التجارية",
            type = ProjectType.DESIGN,
            status = ProjectStatus.REVIEW,
            progress = 85,
            deadline = "2026-10-30",
            price = 2500.0,
            description = "تصميم الشعار الأساسي والبديل، بطاقات العمل، أوراق المراسلات، وأكياس التغليف وتطبيقات السوشيال ميديا.",
            files = listOf(
                ProjectFile(
                    name = "معاينة الشعار المعتمد عالي الدقة",
                    url = "https://images.unsplash.com/photo-1626785774573-4b799315345d?w=1000&q=85",
                    kind = FileKind.IMAGE
                ),
                ProjectFile(
                    name = "تطبيقات الهوية والموكاب التجاري",
                    url = "https://images.unsplash.com/photo-1542744094-24638eff58bb?w=1000&q=85",
                    kind = FileKind.IMAGE
                ),
                ProjectFile(
                    name = "كتيب إرشادات الهوية Brand Guidelines (PDF)",
                    url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    kind = FileKind.PDF
                ),
                ProjectFile(
                    name = "مجلد الملفات المفتوحة والمصدرية على Google Drive",
                    url = "https://drive.google.com",
                    kind = FileKind.LINK
                )
            ),
            timeline = listOf(
                TimelineEvent(title = "بدء المشروع", description = "تم استلام متطلبات الهوية من العميل والبدء في الاسكتشات."),
                TimelineEvent(title = "اعتماد الاسكتش المبدئي", description = "تمت الموافقة على فكرة المونوجرام الهندسي."),
                TimelineEvent(title = "رفع النماذج الرقمية", description = "تم رفع النماذج بدقة كاملة للمراجعة والاعتماد.")
            )
        ),
        Project(
            id = "p_102",
            clientUid = "client_demo_1",
            title = "إنتاج فيديو إعلاني ريلز بالذكاء الاصطناعي (AI Video)",
            type = ProjectType.AI_VIDEO,
            status = ProjectStatus.WORKING,
            progress = 60,
            deadline = "2026-11-05",
            price = 1500.0,
            description = "إنتاج فيديو ترويجي إبداعي سينمائي للمنتجات الجديدة باستخدام تقنيات توليد الحركة والتعليق الصوتي الفصيح.",
            files = listOf(
                ProjectFile(
                    name = "المسودة الأولى للفيديو الإعلاني",
                    url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    kind = FileKind.VIDEO
                ),
                ProjectFile(
                    name = "الاسكريبت الصوتي واللوحة البصرية Storyboard",
                    url = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
                    kind = FileKind.PDF
                )
            ),
            timeline = listOf(
                TimelineEvent(title = "كتابة السيناريو", description = "تم تجهيز فكرة الإعلان واعتمادها."),
                TimelineEvent(title = "توليد المشاهد بالذكاء الاصطناعي", description = "جاري تحسين الإضاءة وتزامن الصوت.")
            )
        ),
        Project(
            id = "p_103",
            clientUid = "client_demo_1",
            title = "برمجة وتطوير متجر إلكتروني سريع ومتجاوب",
            type = ProjectType.STORE,
            status = ProjectStatus.DONE,
            progress = 100,
            deadline = "2026-10-15",
            price = 7000.0,
            description = "متجر متكامل يدعم الدفع الإلكتروني وشحن المحافظات وتتبع الطلبات.",
            files = listOf(
                ProjectFile(
                    name = "رابط المتجر الحي على النطاق الرسمي",
                    url = "https://example.com",
                    kind = FileKind.LINK
                )
            ),
            timeline = listOf(
                TimelineEvent(title = "تسليم المتجر", description = "تم نشر المتجر واختبار بوابات الدفع بنجاح.")
            ),
            rating = 5,
            reviewText = "خدمة فوق الممتازة ودعم فني متميز وسرعة عالية في التنفيذ! شكراً لفريق MY AGENCY."
        )
    )

    fun getSeedInvoices(): List<Invoice> = listOf(
        Invoice(
            id = "INV-2026-001",
            clientUid = "client_demo_1",
            clientName = "م. أحمد عبد الرحمن (شركة الأفق)",
            items = listOf(
                InvoiceItem(title = "دفعة مقدمة - تصميم الهوية البصرية", quantity = 1, unitPrice = 1500.0),
                InvoiceItem(title = "إنتاج فيديو ترويجي AI", quantity = 1, unitPrice = 750.0)
            ),
            subtotal = 2250.0,
            discount = 250.0,
            tax = 0.0,
            total = 2000.0,
            paid = 1000.0,
            status = InvoiceStatus.PARTIAL,
            dueDate = "2026-10-31",
            payments = listOf(
                PaymentReceipt(
                    amount = 1000.0,
                    method = "فودافون كاش",
                    receiptUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=600&q=80",
                    notes = "تحويل دفعة أولى عبر المحفظة",
                    verified = true,
                    timestamp = System.currentTimeMillis() - 86400000L
                )
            )
        ),
        Invoice(
            id = "INV-2026-002",
            clientUid = "client_demo_1",
            clientName = "م. أحمد عبد الرحمن (شركة الأفق)",
            items = listOf(
                InvoiceItem(title = "الدفعة الختامية لمتجر الأفق الإلكتروني", quantity = 1, unitPrice = 3500.0)
            ),
            subtotal = 3500.0,
            discount = 0.0,
            tax = 0.0,
            total = 3500.0,
            paid = 3500.0,
            status = InvoiceStatus.PAID,
            dueDate = "2026-10-15",
            payments = listOf(
                PaymentReceipt(
                    amount = 3500.0,
                    method = "إنستاباي (InstaPay)",
                    receiptUrl = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=600&q=80",
                    notes = "سداد الفاتورة بالكامل عبر إنستاباي",
                    verified = true,
                    timestamp = System.currentTimeMillis() - 400000000L
                )
            )
        )
    )

    fun getSeedTickets(): List<SupportTicket> = listOf(
        SupportTicket(
            id = "tkt_01",
            clientUid = "client_demo_1",
            clientName = "م. أحمد عبد الرحمن (شركة الأفق)",
            subject = "استفسار بخصوص تسليم ملفات الفيكتور المفتوحة",
            status = TicketStatus.OPEN,
            messages = listOf(
                ChatMessage(
                    senderId = "client_demo_1",
                    senderName = "م. أحمد عبد الرحمن",
                    text = "السلام عليكم، هل ملفات الشعار المفتوحة تشمل صيغ Illustrator و SVG للتطريز؟",
                    isFromAdmin = false,
                    timestamp = System.currentTimeMillis() - 7200000L
                ),
                ChatMessage(
                    senderId = "admin_root",
                    senderName = "دعم MY AGENCY",
                    text = "وعليكم السلام يا فندم! نعم بالتأكيد، يتم تسليم ملفات AI, EPS, SVG, PDF بالإضافة لنسخ PNG شفافة بأعلى دقة جاهزة لكافة الاستخدامات.",
                    isFromAdmin = true,
                    timestamp = System.currentTimeMillis() - 3600000L
                )
            )
        )
    )

    fun getSeedFeedbacks(): List<ClientFeedback> = listOf(
        ClientFeedback(
            clientUid = "client_demo_1",
            projectId = "p_101",
            projectTitle = "تصميم هوية بصرية كاملة ودليل العلامة التجارية",
            type = "revise",
            text = "النماذج ممتازة جداً، نرجو فقط تجربة تدرج لوني أفتح قليلاً في كروت العمل لتتناسب مع الطباعة الورقية الفاخرة.",
            from = "م. أحمد عبد الرحمن",
            at = System.currentTimeMillis() - 86400000L,
            handled = false
        )
    )

    fun getSeedOrders(): List<ServiceOrder> = listOf(
        ServiceOrder(
            id = "ord_01",
            clientUid = "client_demo_1",
            clientName = "م. أحمد عبد الرحمن",
            clientPhone = "01012345678",
            services = listOf(
                ServiceItem(
                    id = "srv_ai_video",
                    name = "فيديو إعلاني بالذكاء الاصطناعي (AI Video)",
                    price = 75.0,
                    oldPrice = 100.0
                )
            ),
            couponCode = "WELCOME20",
            totalPrice = 60.0,
            notes = "مطلوب الإعلان لحملة إطلاق العطور الجديدة الأسبوع القادم",
            status = OrderStatus.CONTACTED
        )
    )

    fun getSeedPortfolio(): List<PortfolioItem> = listOf(
        PortfolioItem(
            title = "هوية بصرية كاملة لشركة نكست تك التكنولوجية",
            type = ProjectType.DESIGN,
            url = "https://images.unsplash.com/photo-1600132806370-bf17e65e942f?w=800&q=80",
            thumb = "https://images.unsplash.com/photo-1600132806370-bf17e65e942f?w=800&q=80",
            isPinned = true
        ),
        PortfolioItem(
            title = "فيديو سينمائي ترويجي لماركة ملابس بالذكاء الاصطناعي",
            type = ProjectType.AI_VIDEO,
            url = "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=800&q=80",
            thumb = "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=800&q=80",
            isPinned = true
        ),
        PortfolioItem(
            title = "منصة ومتجر إلكتروني لمستحضرات التجميل الفاخرة",
            type = ProjectType.STORE,
            url = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80",
            thumb = "https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80"
        ),
        PortfolioItem(
            title = "موقع عقاري تفاعلي لحجز الشقق الفندقية والفيلات",
            type = ProjectType.WEB,
            url = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800&q=80",
            thumb = "https://images.unsplash.com/photo-1507238691740-187a5b1d37b8?w=800&q=80"
        )
    )

    fun getSeedNotifications(): List<AppNotification> = listOf(
        AppNotification(
            toUid = "client_demo_1",
            title = "تم تحديث مرحلة المشروع 🚀",
            message = "مشروع \"تصميم هوية بصرية كاملة\" أصبح الآن في مرحلة بانتظار مراجعتك.",
            type = "project",
            timestamp = System.currentTimeMillis() - 3600000L
        ),
        AppNotification(
            toUid = "client_demo_1",
            title = "رد جديد من الدعم الفني 💬",
            message = "قام فريق العمل بالرد على استفسارك في التذكرة رقم #tkt_01.",
            type = "support",
            timestamp = System.currentTimeMillis() - 7200000L
        )
    )

    fun getSeedAnnouncements(): List<Announcement> = listOf(
        Announcement(
            title = "🔥 عروض حصرية لفترة محدودة!",
            content = "خصومات تصل إلى 50% على باقات المونتاج وتطوير المتاجر الإلكترونية بمناسبة الموسم الجديد.",
            type = "banner"
        )
    )

    fun getSeedFAQs(): List<FAQItem> = listOf(
        FAQItem(
            question = "كيف أستلم الملفات المفتوحة والمصدرية لمشروعي؟",
            answer = "يتم رفع كافة الملفات بدقة كاملة في صفحة تفاصيل المشروع، ومع انتهاء المشروع وتأكيد الاعتماد يتم تفعيل روابط التحميل المباشرة وروابط مجلد Google Drive بكافة الصيغ (AI, PSD, PDF, MP4)."
        ),
        FAQItem(
            question = "ما هي وسائل الدفع المعتمدة لدى MY AGENCY؟",
            answer = "نوفر خيارات دفع متعددة ومريحة في مصر: محفظة فودافون كاش، تحويل إنستاباي InstaPay الفوري، والتحويلات البنكية المباشرة."
        ),
        FAQItem(
            question = "كم يستغرق تنفيذ المشروع والتعديلات؟",
            answer = "يتم توضيح موعد التسليم المتوقع في بطاقة كل مشروع. بالنسبة لطلبات التعديل، يقوم الفريق بمراجعتها وتنفيذها عادة خلال 24 إلى 48 ساعة كحد أقصى."
        ),
        FAQItem(
            question = "كيف يمكنني طلب تعديل على المشروع بعد المعاينة؟",
            answer = "من خلال صفحة المشروع، اضغط على زر 'طلب تعديل ✏️' واكتب الملاحظات بالتفصيل ويمكنك إرفاق صورة توضيحية، وسيقوم فريق العمل بتنفيذها فوراً."
        )
    )

    fun getSeedAuditLogs(): List<AuditLog> = listOf(
        AuditLog(actor = "النظام", action = "تثبيت النسخة الرسمية MY AGENCY v2.0", target = "النظام الأساسي"),
        AuditLog(actor = "الأدمن", action = "تحديث أسعار العروض الترويجية", target = "الخدمات والأسعار")
    )
}
