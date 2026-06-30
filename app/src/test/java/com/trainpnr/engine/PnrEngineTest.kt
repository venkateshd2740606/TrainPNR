package com.trainpnr.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PnrEngineTest {
    @Test
    fun validPnr() {
        assertTrue(PnrEngine.isValid("1234567890"))
        assertFalse(PnrEngine.isValid("123456789"))
        assertFalse(PnrEngine.isValid("12345678901"))
    }

    @Test
    fun normalizeStripsNonDigits() {
        assertEquals("1234567890", PnrEngine.normalize("123-456-7890"))
    }

    @Test
    fun parseFromSms() {
        val sms = "Your PNR 9876543210 for train 12345 is confirmed."
        assertEquals("9876543210", PnrEngine.parseFromText(sms))
        assertNull(PnrEngine.parseFromText("no pnr here"))
    }
}
