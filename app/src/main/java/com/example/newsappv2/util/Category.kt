package com.example.newsappv2.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.newsappv2.R

enum class Category(
    val apiValue: String,
    @StringRes val titleResId: Int,
    @DrawableRes val categoryImage: Int
) {
    BUSINESS("business", R.string.category_business, R.drawable.business_center_24px),
    ENTERTAINMENT("entertainment", R.string.category_entertainment, R.drawable.comedy_mask_24px),
    GENERAL("general", R.string.category_general, R.drawable.article_24px),
    HEALTH("health", R.string.category_health, R.drawable.health_and_safety_24px),
    SCIENCE("science", R.string.category_science, R.drawable.biotech_24px),
    SPORTS("sports", R.string.category_sports, R.drawable.exercise_24px),
    TECHNOLOGY("technology", R.string.category_technology, R.drawable.network_intel_node_24px);

    companion object {
        fun fromString(value: String): Category? =
            entries.find { it.apiValue.equals(value, ignoreCase = true) }
    }
}