package com.example.newsappv2.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.newsappv2.R
import com.example.newsappv2.navigation.NavigationDestination
import com.example.newsappv2.util.ThemeMode
import com.example.newsappv2.viewmodel.SettingsViewModel

object SettingsDestination: NavigationDestination {
    override val route = "settings"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val targetLangCode by viewModel.currentTargetLanguageCode.collectAsState()
    val uiLangCode by viewModel.currentUiLanguageCode.collectAsState()
    val currentTheme by viewModel.currentThemeMode.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val userAvatarUri by viewModel.userAvatarUri.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }

    // Діалогове вікно
    if (showEditDialog) {
        EditProfileDialog(
            currentName = userName,
            currentAvatarUri = userAvatarUri,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newAvatarUri ->
                viewModel.updateProfile(newName, newAvatarUri)
                showEditDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_screen)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- ШАПКА ПРОФІЛЮ ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAvatarUri.isNotBlank()) {
                        AsyncImage(
                            model = userAvatarUri,
                            contentDescription = stringResource(R.string.user_avatar_desc),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName.ifBlank { stringResource(R.string.guest_name) },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.user_profile),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // КНОПКА РЕДАГУВАННЯ
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_profile),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 24.dp))

            // --- НАЛАШТУВАННЯ МОВИ ІНТЕРФЕЙСУ ---
            Text(
                text = stringResource(R.string.interface_language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LanguageDropdown(
                label = stringResource(R.string.interface_language),
                currentLanguageCode = uiLangCode,
                availableLanguages = viewModel.availableLanguages,
                onLanguageSelected = { viewModel.setUiLanguage(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- НАЛАШТУВАННЯ МОВИ ПЕРЕКЛАДУ НОВИН ---
            Text(
                text = stringResource(R.string.news_translation_language),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.select_the_language),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            LanguageDropdown(
                label = stringResource(R.string.target_language),
                currentLanguageCode = targetLangCode,
                availableLanguages = viewModel.availableLanguages,
                onLanguageSelected = { viewModel.setTargetLanguage(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- НАЛАШТУВАННЯ ТЕМИ ---
            Text(
                text = stringResource(R.string.theme_mode),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                val options = listOf(ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK)
                val labels = listOf(
                    stringResource(R.string.theme_system),
                    stringResource(R.string.theme_light),
                    stringResource(R.string.theme_dark)
                )

                options.forEachIndexed { index, mode ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        onClick = { viewModel.setThemeMode(mode) },
                        selected = mode == currentTheme
                    ) {
                        Text(labels[index])
                    }
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentAvatarUri: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    val context = LocalContext.current
    var tempName by remember { mutableStateOf(currentName) }
    var tempAvatarUri by remember { mutableStateOf<Uri?>(if (currentAvatarUri.isNotBlank()) Uri.parse(currentAvatarUri) else null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)
            tempAvatarUri = uri
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_profile)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (tempAvatarUri != null) {
                        AsyncImage(
                            model = tempAvatarUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.click_to_add_photo),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text(stringResource(R.string.enter_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(tempName.ifBlank { "Гість" }, tempAvatarUri?.toString() ?: "") }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

// ... LanguageDropdown залишається без змін ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown(
    label: String,
    currentLanguageCode: String,
    availableLanguages: Map<String, String>,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentLangName = availableLanguages.entries
        .find { it.value == currentLanguageCode }?.key ?: stringResource(R.string.guest_name) // Fallback

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentLangName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableLanguages.forEach { (languageName, languageCode) ->
                DropdownMenuItem(
                    text = { Text(languageName) },
                    onClick = {
                        onLanguageSelected(languageCode)
                        expanded = false
                    }
                )
            }
        }
    }
}