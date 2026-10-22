package com.example

import com.example.config.AgencyConfig
import com.example.data.model.DiscountCoupon
import com.example.data.model.FileKind
import com.example.data.model.ProjectStatus
import com.example.data.model.ProjectType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testGovernoratesListContains27Provinces() {
        assertEquals(27, AgencyConfig.EGYPT_GOVERNORATES.size)
        assertTrue(AgencyConfig.EGYPT_GOVERNORATES.contains("القاهرة"))
        assertTrue(AgencyConfig.EGYPT_GOVERNORATES.contains("الإسكندرية"))
        assertTrue(AgencyConfig.EGYPT_GOVERNORATES.contains("الجيزة"))
    }

    @Test
    fun testFileKindDetection() {
        assertEquals(FileKind.IMAGE, FileKind.detectFromUrl("https://res.cloudinary.com/demo/image/upload/sample.jpg"))
        assertEquals(FileKind.VIDEO, FileKind.detectFromUrl("https://res.cloudinary.com/demo/video/upload/sample.mp4"))
        assertEquals(FileKind.PDF, FileKind.detectFromUrl("https://example.com/brief.pdf"))
        assertEquals(FileKind.LINK, FileKind.detectFromUrl("https://myagency-portfolio.com"))
    }

    @Test
    fun testCouponDiscountCalculation() {
        val percentageCoupon = DiscountCoupon(
            code = "WELCOME20",
            discountPercent = 20.0,
            expiresAt = "2026-12-31"
        )
        // 20% of 1000 is 200
        val discount = 1000.0 * (percentageCoupon.discountPercent / 100.0)
        assertEquals(200.0, discount, 0.01)
        val finalPrice = 1000.0 - discount
        assertEquals(800.0, finalPrice, 0.01)
    }

    @Test
    fun testProjectStatusOrdering() {
        assertTrue(ProjectStatus.NEW.stepIndex < ProjectStatus.WORKING.stepIndex)
        assertTrue(ProjectStatus.WORKING.stepIndex < ProjectStatus.REVIEW.stepIndex)
        assertTrue(ProjectStatus.REVIEW.stepIndex < ProjectStatus.DONE.stepIndex)
    }

    @Test
    fun testProjectTypeEnumKeys() {
        assertEquals(ProjectType.AI_VIDEO, ProjectType.fromKey("ai_video"))
        assertEquals(ProjectType.DESIGN, ProjectType.fromKey("design"))
        assertEquals(ProjectType.VIDEO, ProjectType.fromKey("video"))
        assertEquals(ProjectType.WEB, ProjectType.fromKey("web"))
        assertEquals(ProjectType.STORE, ProjectType.fromKey("store"))
    }
}
