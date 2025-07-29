package com.puc.pyp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.puc.pyp.ui.screens.MainScreen
import com.puc.pyp.ui.theme.PUCPassingPackageTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity() {

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                basePreferences.edit {
                    putBoolean("notification_permission_permanently_denied", !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val isDarkMode = basePreferences.getBoolean("dark_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )

        val shortcut = intent.getIntExtra("shortcut", 7)
        if (shortcut != 7)
            basePreferences.edit { putInt("frag", shortcut) }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PUCPassingPackageTheme {
                MainScreen()
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            val shortcutManager = getSystemService(ShortcutManager::class.java)
            lifecycleScope.launch(Dispatchers.IO) {
                if (shortcutManager?.dynamicShortcuts.isNullOrEmpty()) {
                    val shortcut1 = ShortcutInfo.Builder(this@MainActivity, "shortcut_1")
                        .setShortLabel(getString(R.string.tab_notes))
                        .setLongLabel(getString(R.string.tab_notes))
                        .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.note))
                        .setIntent(Intent(this@MainActivity, MainActivity::class.java).apply {
                            action = Intent.ACTION_MAIN
                            putExtra("shortcut", 0)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        .build()

                    val shortcut2 = ShortcutInfo.Builder(this@MainActivity, "shortcut_2")
                        .setShortLabel(getString(R.string.tab_text))
                        .setLongLabel(getString(R.string.tab_text))
                        .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.book))
                        .setIntent(Intent(this@MainActivity, MainActivity::class.java).apply {
                            action = Intent.ACTION_MAIN
                            putExtra("shortcut", 1)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        .build()

                    val shortcut3 = ShortcutInfo.Builder(this@MainActivity, "shortcut_3")
                        .setShortLabel(getString(R.string.tab_up))
                        .setLongLabel(getString(R.string.tab_up))
                        .setIcon(Icon.createWithResource(this@MainActivity, R.drawable.up))
                        .setIntent(Intent(this@MainActivity, MainActivity::class.java).apply {
                            action = Intent.ACTION_MAIN
                            putExtra("shortcut", 2)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        .build()

                    withContext(Dispatchers.Main) {
                        shortcutManager?.dynamicShortcuts = listOf(shortcut3, shortcut2, shortcut1)
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
                !basePreferences.getBoolean("notification_permission_permanently_denied", false)
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}