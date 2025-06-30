package com.example.newsappv2.util

import androidx.annotation.DrawableRes
import com.example.newsappv2.R

enum class Category(val categoryName: String, @DrawableRes val categoryImage: Int) {
    BUSINESS("Business", R.drawable.business_center_24px),
    ENTERTAINMENT("Entertainment", R.drawable.comedy_mask_24px),
    GENERAL("General", R.drawable.article_24px),
    HEALTH("Health", R.drawable.health_and_safety_24px),
    SCIENCE("Science", R.drawable.biotech_24px),
    SPORTS("Sports", R.drawable.exercise_24px),
    TECHNOLOGY("Technology", R.drawable.network_intel_node_24px);

    companion object {
        fun fromString(value: String): Category? =
            entries.find { it.name.equals(value,ignoreCase = true) }
    }
}