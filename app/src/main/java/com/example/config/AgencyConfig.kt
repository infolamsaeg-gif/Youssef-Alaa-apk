package com.example.config

/**
 * إعدادات MY AGENCY الموحدة وفقاً لوثيقة المواصفات الكاملة
 */
object AgencyConfig {
    const val AGENCY_NAME = "MY AGENCY"
    const val AGENCY_SLOGAN = "استوديو التصميم والمونتاج والذكاء الاصطناعي وتطوير الويب"
    const val AGENCY_DESCRIPTION = "تصميم جرافيك، مونتاج وفيديو، AI video، مواقع ويب، ومتاجر إلكترونية احترافية"

    // أرقام التواصل والواتساب
    const val WHATSAPP_PRIMARY = "201508664132"
    const val WHATSAPP_SECONDARY = "201151735927"
    const val CALL_PHONE = "01508664132"
    const val CALL_PHONE_ALT = "01151735927"

    // حساب الأدمن المعتمد
    const val ADMIN_EMAIL = "info.lamsa.eg@gmail.com"
    const val ADMIN_DEFAULT_PASS = "admin123456"

    // العروض والعملة
    const val DEFAULT_OFFER_ENDS_AT = "2026-11-01"
    const val CURRENCY = "ج.م"

    // طرق الدفع
    const val VODAFONE_CASH_NUMBER = "01508664132"
    const val INSTAPAY_HANDLE = "myagency@instapay"
    const val BANK_ACCOUNT_NAME = "MY AGENCY FOR DIGITAL SERVICES"
    const val BANK_IBAN = "EG380002000100000012345678901"

    // ساعات العمل والدعم
    const val WORKING_HOURS = "يومياً من 10:00 صباحاً حتى 11:00 مساءً (بتوقيت القاهرة)"
    const val AVG_RESPONSE_TIME = "خلال 15 إلى 30 دقيقة"

    // إعدادات Cloudinary (Unsigned Preset)
    const val CLOUDINARY_CLOUD_NAME = "my-agency"
    const val CLOUDINARY_UPLOAD_PRESET = "my_agency_unsigned"
    const val CLOUDINARY_MAX_FILE_SIZE_MB = 25

    // محافظات مصر الـ 27 الرسمية
    val EGYPT_GOVERNORATES = listOf(
        "القاهرة", "الجيزة", "الإسكندرية", "الدقهلية", "الشرقية",
        "القليوبية", "الغربية", "المنوفية", "البحيرة", "كفر الشيخ",
        "دمياط", "بورسعيد", "الإسماعيلية", "السويس", "شمال سيناء",
        "جنوب سيناء", "بني سويف", "الفيوم", "المنيا", "أسيوط",
        "سوهاج", "قنا", "الأقصر", "أسوان", "البحر الأحمر",
        "الوادي الجديد", "مطروح"
    )
}
