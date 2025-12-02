package com.redcom1988.srw.util

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import java.time.Instant as jInstant

@OptIn(ExperimentalTime::class)
fun formatLastUpdated(updatedAt: Instant?): String {
    if (updatedAt == null) return "Unknown"

    val now = Clock.System.now()
    val duration = now - updatedAt

    return when {
        duration.inWholeMinutes < 1 -> "Just now" // TODO String Resource
        duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}m ago" // TODO String Resource
        duration.inWholeHours < 24 -> "${duration.inWholeHours}h ago" // TODO String Resource
        duration.inWholeDays < 7 -> "${duration.inWholeDays}d ago" // TODO String Resource
        else -> {
            // Convert to java.time for formatting
            val javaInstant = jInstant.ofEpochMilli(updatedAt.toEpochMilliseconds())
            val localDateTime = javaInstant.atZone(ZoneId.systemDefault()).toLocalDateTime()
            localDateTime.format(DateTimeFormatter.ofPattern("MMM dd"))
        }
    }
}