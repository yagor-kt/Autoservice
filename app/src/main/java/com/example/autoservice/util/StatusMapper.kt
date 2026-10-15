package com.example.autoservice.util

import androidx.annotation.StringRes
import com.example.autoservice.R

object StatusMapper {
    @StringRes
    fun labelRes(status: String): Int =
        when (status) {
            "ACCEPTED" -> R.string.status_accepted
            "DIAGNOSTICS" -> R.string.status_diagnostics
            "APPROVAL" -> R.string.status_approval
            "REPAIR" -> R.string.status_repair
            "READY" -> R.string.status_ready
            "CLOSED" -> R.string.status_closed
            else -> R.string.unknown_value
        }

    fun next(status: String): String? =
        when (status) {
            "ACCEPTED" -> "DIAGNOSTICS"
            "DIAGNOSTICS" -> "APPROVAL"
            "APPROVAL" -> "REPAIR"
            "REPAIR" -> "READY"
            "READY" -> "CLOSED"
            else -> null
        }
}