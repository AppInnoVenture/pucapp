package com.puc.pyp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.net.toUri
import com.puc.pyp.BaseActivity
import com.puc.pyp.R
import com.puc.pyp.ui.components.LanguageSelectionDialog
import com.puc.pyp.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val tabNotes = stringResource(R.string.tab_notes)
    val tabText = stringResource(R.string.tab_text)
    val tabUp = stringResource(R.string.tab_up)

    val tabs = remember {
        listOf(
            tabNotes,
            tabText,
            tabUp
        )
    }
    val pagerState = rememberPagerState(
        initialPage = (context as BaseActivity).basePreferences.getInt("frag", 0),
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size }
    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            (context).basePreferences.edit { putInt("frag", page) }
        }
    }
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = (context).basePreferences.getString("language", "sys") ?: "sys",
            onLanguageSelected = { code ->
                (context).basePreferences.edit { putString("language", code) }
                context.recreate()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onItemClick = { id ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    when (id) {
                        R.id.nav_home -> scope.launch { drawerState.close() }
                        R.id.nav_telegram -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://t.me/karnataka_kea".toUri()))
                        R.id.nav_telegram2 -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://t.me/karnataka_neet".toUri()))
                        R.id.nav_share -> context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shareApp))
                            type = "text/plain"
                        }, "Share app via"))
                        R.id.kcet -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.kea.pyp".toUri()))
                        R.id.nav_1puc -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.first.puc".toUri()))
                        R.id.nav_pucqp -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.puc.notes".toUri()))
                        R.id.nav_dark -> {
                            (context).basePreferences.edit {
                                putBoolean("dark_mode", !(context.basePreferences.getBoolean("dark_mode", false)))
                            }
                            context.recreate()
                        }
                        R.id.nav_apps -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/search?q=pub:AppInnoVenture".toUri()))
                        R.id.nav_delete -> {
                            scope.launch {
                                delay(250)
                                val pdfFiles = context.filesDir.listFiles { file -> file.extension == "pdf" }?.toSet()
                                (context).handleFileDeletion(pdfFiles, context.getString(R.string.alertMsg), context.findViewById(android.R.id.content))
                            }
                        }
                        R.id.nav_lang -> {
                            scope.launch {
                                delay(250)
                                showLanguageDialog = true
                            }
                        }
                        R.id.nav_privacy -> context.startActivity(Intent(Intent.ACTION_VIEW, "https://sites.google.com/view/pucpyp/home".toUri()))
                        R.id.nav_exit -> {
                            Toast.makeText(context, R.string.exitToast, Toast.LENGTH_SHORT).show()
                            context.finish()
                        }
                        R.id.nav_mail -> {
                            try {
                                context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:".toUri()
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("ayg0702@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
                                }, "Send Email"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "Failed to send email: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 48.dp), // Offset navigation icon
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "PUC",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimary // Changed back to onPrimary for consistency
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                painterResource(R.drawable.ic_menu),
                                contentDescription = stringResource(R.string.nav_open)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            content = { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(padding)
                ) {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                height = 3.5.dp,
                                color = Color(0xFF4CAF50) // Custom green indicator
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                text = {
                                    Text(
                                        title,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onTertiary
                                    )
                                },
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        pageSpacing = 8.dp,
                        beyondViewportPageCount = 1,
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapAnimationSpec = spring(stiffness = Spring.StiffnessLow)
                        )
                    ) { page ->
                        when (page) {
                            0 -> StudyMaterialScreen()
                            1 -> TextScreen()
                            2 -> UpdatesScreen()
                        }
                    }
                }
            }
        )
    }

    BackHandler(drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

}

@Composable
fun DrawerContent(onItemClick: (Int) -> Unit) {
    val context = LocalContext.current
    val isDarkMode = (context as BaseActivity).basePreferences.getBoolean("dark_mode", false)
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.75F)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 48.dp) // Add bottom padding for navigation bar
    ) {
        Image(
            painter = painterResource(R.drawable.header),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(224.dp)
                .background(White)
                .padding(15.dp)
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_home), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_home) },
            icon = { Icon(painterResource(R.drawable.baseline_home_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_tg), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_telegram) },
            icon = { Icon(painterResource(R.drawable.baseline_send_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_tg2), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_telegram2) },
            icon = { Icon(painterResource(R.drawable.baseline_send_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_share), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_share) },
            icon = { Icon(painterResource(R.drawable.baseline_share_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(if (isDarkMode) stringResource(R.string.nav_light) else stringResource(R.string.nav_dark), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_dark) },
            icon = { Icon(painterResource(R.drawable.baseline_light_mode_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_kan), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_lang) },
            icon = { Icon(painterResource(R.drawable.baseline_language_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_1puc), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_1puc) },
            icon = { Icon(painterResource(R.drawable.baseline_get_app_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_kcet), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.kcet) },
            icon = { Icon(painterResource(R.drawable.baseline_get_app_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_pucqp), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_pucqp) },
            icon = { Icon(painterResource(R.drawable.baseline_get_app_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_more), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_apps) },
            icon = { Icon(painterResource(R.drawable.baseline_apps_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_contact), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_mail) },
            icon = { Icon(painterResource(R.drawable.baseline_email_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_delete), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_delete) },
            icon = { Icon(painterResource(R.drawable.baseline_delete_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_privacy), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_privacy) },
            icon = { Icon(painterResource(R.drawable.baseline_privacy_tip_24), contentDescription = null) }
        )
        NavigationDrawerItem(
            label = { Text(stringResource(R.string.nav_exit), style = MaterialTheme.typography.labelSmall) },
            selected = false,
            onClick = { onItemClick(R.id.nav_exit) },
            icon = { Icon(painterResource(R.drawable.baseline_exit_to_app_24), contentDescription = null) }
        )
    }
}