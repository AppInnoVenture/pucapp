package com.puc.pyp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.puc.pyp.Item
import com.puc.pyp.Language
import com.puc.pyp.R
import com.puc.pyp.YearItem

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ItemCard(
    item: Item,
    onClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(5.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick(item)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                model = item.img,
                contentDescription = item.description,
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.extraLarge),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun YearItemCard(
    item: YearItem,
    onClick: (YearItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick(item)
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = item.descLan,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = remember {
        listOf(
            Language("System Default", "sys", "System Default"),
            Language("Kannada", "kn", "ಕನ್ನಡ"),
            Language("English", "en", "English"),
            Language("Hindi", "hi", "हिन्दी"),
            Language("Telugu", "te", "తెలుగు"),
            Language("Tamil", "ta", "தமிழ்"),
            Language("Marathi", "mr", "मराठी"),
            Language("Tulu", "tcy", "ತುಳು"),
            Language("Malayalam", "ml", "മലയാളം"),
            Language("Urdu", "ur", "اردو"),
            Language("Arabic", "ar", "العربية"),
            Language("French", "fr", "Français")
        )
    }
    val languageNames = languages.map { it.displayName }.toTypedArray()
    var selectedLanguageCode by remember { mutableStateOf(currentLanguage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.nav_kan), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                languageNames.forEachIndexed { index, name ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguageCode = languages[index].code }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguageCode == languages[index].code,
                            onClick = { selectedLanguageCode = languages[index].code },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF2196F3) // Custom blue for selected radio button
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedLanguageCode != currentLanguage) {
                    onLanguageSelected(selectedLanguageCode)
                }
                onDismiss()
            }) {
                Text(stringResource(R.string.dialog_button_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_button_cancel))
            }
        }
    )
}