package com.trainpnr.engine

object PnrEngine {
    private val pnrRegex = Regex("""\b(\d{10})\b""")

    fun normalize(input: String): String = input.filter { it.isDigit() }.take(10)

    fun isValid(pnr: String): Boolean = pnr.length == 10 && pnr.all { it.isDigit() }

    /** Extract a 10-digit PNR from pasted SMS or free text. */
    fun parseFromText(text: String): String? {
        val trimmed = text.trim()
        if (isValid(normalize(trimmed))) return normalize(trimmed)
        return pnrRegex.find(trimmed)?.groupValues?.get(1)
    }

    val statusGuide = listOf(
        "CNF" to "Confirmed — seat is confirmed for travel.",
        "RAC" to "Reservation Against Cancellation — you may get a berth if someone cancels.",
        "WL" to "Waitlist — ticket not confirmed yet; moves up as others cancel.",
        "GNWL" to "General Waitlist — most common waitlist on long-distance trains.",
        "PQWL" to "Pooled Quota Waitlist — waitlist under a pooled quota for a station group.",
        "RLWL" to "Remote Location Waitlist — waitlist for intermediate stations."
    )

    val checkSteps = listOf(
        "Open the IRCTC website or app and sign in (or use guest PNR enquiry).",
        "Go to PNR Status / My Trips and enter your 10-digit PNR number.",
        "Alternatively, visit NTES (National Train Enquiry System) or call 139.",
        "Note chart preparation time — final berth is often confirmed after charting.",
        "Save frequently used PNRs in this app for quick reference."
    )
}
