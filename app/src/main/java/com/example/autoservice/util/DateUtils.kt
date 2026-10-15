package com.example.autoservice.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateUtils {
    private val zone: ZoneId
        get() = ZoneId.systemDefault()

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale("ru"))
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("ru"))
    private val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale("ru"))

    fun startOfDay(offsetDays: Int = 0): Long =
        LocalDate.now(zone)
            .plusDays(offsetDays.toLong())
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

    fun endOfDay(offsetDays: Int = 0): Long =
        LocalDate.now(zone)
            .plusDays(offsetDays.toLong())
            .plusDays(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli() - 1

    fun monthStart(): Long =
        LocalDate.now(zone)
            .withDayOfMonth(1)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

    fun weekAgo(): Long =
        LocalDate.now(zone)
            .minusDays(6)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

    fun formatDate(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(dateFormatter)

    fun formatDateTime(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(dateTimeFormatter)

    fun monthName(millis: Long): String {
        val value = Instant.ofEpochMilli(millis).atZone(zone).format(monthFormatter)
        return value.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecase(Locale("ru")) else char.toString()
        }
    }

    fun daysUntil(millis: Long): Int {
        val today = LocalDate.now(zone)
        val dueDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
        return (dueDate.toEpochDay() - today.toEpochDay()).toInt()
    }

    fun parseDate(text: String): Long? =
        try {
            LocalDate.parse(text.trim(), dateFormatter)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()
        } catch (_: DateTimeParseException) {
            null
        }
}