package com.redcom1988.srw.util

object ErrorMessageUtil {

    fun translate(error: String): String {
        return when {
            error.contains("Unable to resolve host", ignoreCase = true) ||
            error.contains("No address associated with hostname", ignoreCase = true) ||
            error.contains("Failed to connect", ignoreCase = true) ||
            error.contains("Connection refused", ignoreCase = true) ||
            error.contains("Network is unreachable", ignoreCase = true) ||
            error.contains("no network", ignoreCase = true) -> "No internet connection"

            error.contains("timeout", ignoreCase = true) -> "Connection timed out"

            error.contains("401", ignoreCase = true) ||
            error.contains("unauthorized", ignoreCase = true) -> "Session expired. Please log in again"

            error.contains("403", ignoreCase = true) ||
            error.contains("forbidden", ignoreCase = true) -> "Access denied"

            error.contains("404", ignoreCase = true) ||
            error.contains("not found", ignoreCase = true) -> "Data not found"

            error.contains("500", ignoreCase = true) ||
            error.contains("server error", ignoreCase = true) -> "Server error. Please try again later"

            error.contains("certificate", ignoreCase = true) ||
            error.contains("ssl", ignoreCase = true) -> "Secure connection failed"

            else -> "Something went wrong. Please try again"
        }
    }
}
