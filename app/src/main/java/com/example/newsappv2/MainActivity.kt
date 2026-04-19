package com.example.newsappv2

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity // ❗️ ЗМІНЕНО ІМПОРТ
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.newsappv2.ui.NewsApp
import com.example.newsappv2.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.uiLangCode.collect { langCode ->
                    val appLocale = LocaleListCompat.forLanguageTags(langCode)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            }
        }

        setContent {
            NewsApp()
        }
    }
}