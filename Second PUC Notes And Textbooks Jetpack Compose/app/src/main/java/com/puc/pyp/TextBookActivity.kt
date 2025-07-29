package com.puc.pyp

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.puc.pyp.ui.theme.PUCPassingPackageTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TextItemCard(
    item: TextItem,
    onClick: (TextItem) -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(durationMillis = 400)
        ) + androidx.compose.animation.fadeIn(animationSpec = tween(durationMillis = 400))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick(item) },
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
}

@Suppress("UNUSED_PARAMETER")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextBookScreen(
    prefix: String,
    descr: String,
    name: String,
    img: Int,
    dual: Boolean,
    isDarkMode: Boolean,
    shortcut: Boolean,
    onBackPressed: () -> Unit,
    onNavigateUp: () -> Unit,
    onShare: () -> Unit,
    onExit: () -> Unit,
    onDelete: () -> Unit,
    onShortcut: () -> Unit,
    onItemClick: (TextItem) -> Unit,
    itemsProvider: List<TextItem>
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    val textItems = remember(prefix) {
        if ("dual" in prefix) {
            listOf(
                TextItem(context.getString(R.string.btn_eng), prefix.replace("dual", "e"), 0, 0, 0, 0),
                TextItem(context.getString(R.string.btn_kan), prefix.replace("dual", "k"), 0, 0, 0, 0)
            )
        } else {
            itemsProvider
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 48.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$descr ${context.getString(R.string.tab_text)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            painterResource(R.drawable.ic_back),
                            contentDescription = null,
                            tint = if (isDarkMode) Color.Black else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            painterResource(R.drawable.ic_more_vert),
                            contentDescription = null
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.nav_share)) },
                            onClick = {
                                onShare()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.baseline_share_24),
                                    contentDescription = null
                                )
                            }
                        )
                        if (!dual) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.lan_delete)) },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        painterResource(R.drawable.baseline_delete_24),
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.lan_shortcut)) },
                            onClick = {
                                onShortcut()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.baseline_shortcut_24),
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.nav_exit)) },
                            onClick = {
                                onExit()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    painterResource(R.drawable.baseline_exit_to_app_24),
                                    contentDescription = null
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                itemsIndexed(textItems, key = { index, item -> item.descLan + index }) { index, item ->
                    var isVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay((index * 600).toLong()) // 15% of 4000ms = 600ms per item
                        isVisible = true
                    }
                    TextItemCard(
                        item = item,
                        onClick = onItemClick,
                        isVisible = isVisible
                    )
                }
            }
        }
    )
}
class TextBookActivity : BaseActivity() {
    private val prefix: String by lazy { intent?.getStringExtra("prefix").toString() }
    private val img: Int by lazy { intent.getIntExtra("img", -1) }
    private val shortcut: Boolean by lazy { intent.getBooleanExtra("shortcut", false) }
    private val isDarkMode: Boolean by lazy { basePreferences.getBoolean("dark_mode", false) }
    private val descr: String by lazy { intent.getStringExtra("desc").toString() }
    private val name: String by lazy { intent.getStringExtra("name").toString() }
    private val dual: Boolean by lazy { intent.getBooleanExtra("dual", false) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (shortcut) {
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PUCPassingPackageTheme(darkTheme = isDarkMode) {
                TextBookScreen(
                    prefix = prefix,
                    descr = descr,
                    name = name,
                    img = img,
                    dual = dual,
                    isDarkMode = isDarkMode,
                    shortcut = shortcut,
                    onBackPressed = {
                        if (shortcut) {
                            startActivity(Intent(this, MainActivity::class.java).apply {
                                putExtra("shortcut", 1)
                            })
                            finish()
                        } else {
                            finish()
                        }
                    },
                    onNavigateUp = {
                        if (shortcut) {
                            startActivity(Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                putExtra("shortcut", 1)
                            })
                        } else {
                            finish()
                        }
                    },
                    onShare = {
                        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.shareApp))
                            type = "text/plain"
                        }, "Share app via"))
                    },
                    onExit = {
                        Toast.makeText(this, R.string.exitToast, Toast.LENGTH_SHORT).show()
                        finishAffinity()
                    },
                    onDelete = {
                        val filesToDelete = listItems().map { File(filesDir, it.extra) }.filter { it.exists() }.toSet()
                        handleFileDeletion(filesToDelete, getString(R.string.alertLanMsg), findViewById(android.R.id.content))
                    },
                    onShortcut = {
                        val intent = Intent(this, TextBookActivity::class.java).apply {
                            action = Intent.ACTION_MAIN
                            putExtra("prefix", prefix)
                            putExtra("desc", descr)
                            putExtra("img", img)
                            putExtra("shortcut", true)
                            putExtra("name", name)
                            putExtra("dual", dual)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }

                        if (Build.VERSION.SDK_INT > 25) {
                            val shortcutManager = getSystemService(ShortcutManager::class.java)
                            val shortcut = ShortcutInfo.Builder(this, "$descr ${getString(R.string.tab_text)}")
                                .setShortLabel("$descr ${getString(R.string.tab_text)}")
                                .setLongLabel("$descr ${getString(R.string.tab_text)}")
                                .setIcon(Icon.createWithResource(this, img))
                                .setIntent(intent)
                                .build()
                            shortcutManager.requestPinShortcut(shortcut, null)
                            Toast.makeText(this, R.string.lanShortcutToast, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Shortcut creation not supported on your phone", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onItemClick = { item ->
                        if ("dual" in prefix) {
                            val intent = Intent(this, TextBookActivity::class.java).apply {
                                putExtra("desc", "$descr ${item.descLan}")
                                putExtra("prefix", item.extra)
                                putExtra("img", img)
                                putExtra("name", name)
                                putExtra("dual", true)
                            }
                            startActivity(intent)
                        } else if (item.extra.startsWith("https")) {
                            startActivity(Intent(Intent.ACTION_VIEW, item.extra.toUri()))
                        } else {
                            val pdfFile = File(filesDir, item.extra)
                            if (pdfFile.exists() || isInternetAvailable()) {
                                openPdfViewer(item)
                            } else {
                                lifecycleScope.launch {
                                    showRetrySnackbar(item)
                                }
                            }
                        }
                    },
                    itemsProvider = this.listItems()
                )
            }
        }
    }

    private fun openPdfViewer(item: TextItem) {
        val intent = Intent(this, PdfTextActivity::class.java).apply {
            val patterns = listOf(
                "    ಪದ್ಯಭಾಗ\n",
                "    ಗದ್ಯಭಾಗ\n",
                "    ದೀರ್ಘಗದ್ಯ\n",
                "    गद्य भाग\n",
                "    मध्य कालीन कविता\n",
                "    आधुिनक कविता\n",
                "    अपठित\n",
                "    पद्य भाग\n",
                "    स्थूलवाचन\n",
                "    ಪ್ರಾಯೋಗಿಕ ವಿಭಾಗ\n"
            )
            putExtra("desc", patterns.find { item.descLan.startsWith(it) }?.let { item.descLan.drop(it.length) } ?: item.descLan)
            putExtra("prefix", item.extra)
            putExtra("name", name)
            putExtra("dual", dual)
            putExtra("s1", item.s1)
            putExtra("s2", item.s2)
            putExtra("e1", item.e1)
            putExtra("e2", item.e2)
        }
        startActivity(intent)
    }

    private fun showRetrySnackbar(item: TextItem) {
        Snackbar.make(findViewById(android.R.id.content), R.string.noNetRetry, Snackbar.LENGTH_LONG)
            .setAction(R.string.snack_retry) {
                if (isInternetAvailable()) {
                    openPdfViewer(item)
                } else {
                    lifecycleScope.launch {
                        delay(300)
                        showRetrySnackbar(item)
                    }
                }
            }.show()
    }

    private fun listItems() = listOf(
           // Dummy Placeholder
            // In main app it initialises a list containing different chapters of particular subject, simplified here to keep it private; happy to demo it in an interview! 
            
}