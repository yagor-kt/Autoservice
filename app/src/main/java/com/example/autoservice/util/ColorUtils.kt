package com.example.autoservice.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.autoservice.ui.theme.Error
import com.example.autoservice.ui.theme.Success
import com.example.autoservice.ui.theme.Warning

@Composable
fun statusColor(status: String): Color =
    when (status) {
        "ACCEPTED" -> Color(0xFF757575)
        "DIAGNOSTICS" -> Color(0xFF2196F3)
        "APPROVAL" -> Color(0xFFAB47BC)
        "REPAIR" -> Color(0xFFFF9800)
        "READY" -> Success
        "CLOSED" -> Color(0xFF424242)
        else -> MaterialTheme.colorScheme.secondary
    }

@Composable
fun reminderColor(daysUntil: Int): Color =
    when {
        daysUntil > 30 -> Success
        daysUntil in 8..30 -> Warning
        else -> Error
    }