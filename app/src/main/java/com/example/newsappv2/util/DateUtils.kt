package com.example.newsappv2.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.newsappv2.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun String.formatAsDate(): String {
    return try {
        val zoned = ZonedDateTime.parse(this)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", Locale("us"))
        zoned.format(formatter)
    } catch (e: Exception) {
        stringResource(R.string.unknown_when_published)
    }
}