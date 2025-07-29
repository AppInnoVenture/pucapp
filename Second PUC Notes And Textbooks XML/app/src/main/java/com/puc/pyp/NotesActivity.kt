package com.puc.pyp

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.puc.pyp.databinding.ActivityLanBinding
import com.puc.pyp.databinding.ToolbarLanBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class NotesActivity: BaseActivity() {
    private var dual = false
    private val pre: String by lazy { intent?.getStringExtra("prefix").toString() }
    private lateinit var desc: String
    private lateinit var head: String
    private var shortcut: Boolean = false
    private var isDarkMode: Boolean = false
    private var kan: Boolean = false
    private var belowLayout: Boolean = false
    private var dualPage: Boolean = false
    private val img: Int by lazy { intent.getIntExtra("img", -1) }
    private lateinit var notesitems: List<YearItem>
    private lateinit var binding: ActivityLanBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        isDarkMode = basePreferences.getBoolean("dark_mode", false)

        shortcut = intent.getBooleanExtra("shortcut", false)
        if (shortcut)
            if (isDarkMode)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = setContentView(this, R.layout.activity_lan)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        desc = intent.getStringExtra("desc").toString()
        head = "$desc ${getString(R.string.tab_notes)}"
        val yt = "https://www.youtube.com/playlist?list="

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (isDarkMode) {
            binding.toolbar.navigationIcon?.setTint(Color.BLACK)
            binding.toolbar.overflowIcon?.setTint(Color.BLACK)
        }

        val customTitleViewBinding: ToolbarLanBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.toolbar_lan, null, false)
        binding.toolbar.addView(
            customTitleViewBinding.root, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        customTitleViewBinding.customToolbarTitle.apply {
            text = head
            isSelected = true
        }

        if ("du" in pre) {
            notesitems = listOf(
                YearItem(getString(R.string.btn_eng), pre.replace("du", "e")),
                YearItem(getString(R.string.btn_kan), pre.replace("du", "k"))
            )
            dual = true
        } else {
            // Dummy Placeholder
            // In main app it initialises a list containing different chapters of particular subject, simplified here to keep it private; happy to demo it in an interview! 
            
        }

        val itemAdapterLan = AdapterLan(notesitems) { item ->
            if (dual) {
                val intent = Intent(this, NotesActivity::class.java)
                intent.putExtra("desc", desc + " " + item.descLan)
                intent.putExtra("img", img)
                intent.putExtra("prefix", item.extra)
                startActivity(intent)
            } else if ("YouTube" in item.descLan)
                startActivity(Intent(Intent.ACTION_VIEW, item.extra.toUri()))
            else {
                val fileName = item.extra.substring(0, 3)
                if (File(filesDir, "$fileName.pdf").exists() or kan or isInternetAvailable()) {
                    dualPage = fileName in dualPrefixes
                    openPdfViewer(item)
                } else
                    showRetrySnackbar(item)
        }
      }
        binding.recyclerViewLan.layoutManager = LinearLayoutManager(this).apply {
            isItemPrefetchEnabled = true
        }
        binding.recyclerViewLan.adapter = itemAdapterLan
        if (savedInstanceState == null) {
            binding.recyclerViewLan.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
            if (basePreferences.getBoolean("dwd", false)) {
                val file = File(filesDir, basePreferences.getString("dwdPdf", null).toString())
                if (file.exists()) file.delete()
                basePreferences.edit { putBoolean("dwd", false) }
            }
            if (basePreferences.getBoolean("isFirstLaunch", true)) {
                File(filesDir, "bus.pdf").delete()
                File(filesDir, "eco.pdf").delete()
                File(filesDir, "edu.pdf").delete()
                basePreferences.edit { putBoolean("isFirstLaunch", false) }
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            if (shortcut) {
                basePreferences.edit { putInt("frag", 0) }
                startActivity(Intent(this@NotesActivity, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("pinShortcut", false)
                })
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    companion object {
        private val patterns = listOf(
            "    ಪದ್ಯಭಾಗ\n",
            "    ಗದ್ಯಭಾಗ\n",
            "    ದೀರ್ಘಗದ್ಯ\n",
            "    गद्य भाग\n",
            "    मध्य कालीन कविता\n",
            "    आधुिनक कविता\n",
            "    अपठित\n"
        )
        private val dualPrefixes = setOf("acc", "bio", "che", "cse", "phy", "sta")
    }

    private fun openPdfViewer(item: YearItem) {
        val intent = Intent(this, PdfViewerActivity::class.java)
        intent.putExtra("desc",
            patterns.find { item.descLan.startsWith(it) }?.let { item.descLan.drop(it.length) } ?: item.descLan)
        intent.putExtra("name", name)
        intent.putExtra("kan", kan)
        intent.putExtra("prefix", item.extra)
        intent.putExtra("diff", "notes")
        startActivity(intent)
    }

    private fun showRetrySnackbar(item: YearItem) {
        Snackbar.make(binding.root, R.string.noNetRetry, Snackbar.LENGTH_LONG).setAction(R.string.snack_retry) {
            if (isInternetAvailable())
                openPdfViewer(item)
            else
                lifecycleScope.launch {
                    delay(300)
                    showRetrySnackbar(item)
                }
        }.show()
    }


    override fun onSupportNavigateUp(): Boolean {
        if (shortcut) {
                basePreferences.edit { putInt("frag", 0) }
                startActivity(Intent(this@NotesActivity, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("pinShortcut", false)
                })
            } else {
            onBackPressedDispatcher.onBackPressed()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lan, menu)
        
        if (dual or kan) 
            menu?.get(2)?.isVisible = false
            
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.lan_share -> {
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.shareApp))
                    type = "text/plain" }, "Share app via"))
                return true
            }

            R.id.lan_exit -> {
                Toast.makeText(this, R.string.exitToast, Toast.LENGTH_SHORT).show()
                finishAffinity()
                return true
            }

            R.id.lan_delete -> {
                val filesToDelete = notesitems.map { File(filesDir, it.extra.substring(0, 3) + ".pdf") }.filter { it.exists() }.toSet()

                handleFileDeletion(filesToDelete, getString(R.string.alertLanMsg), binding.root)
                return true
            }

            R.id.lan_shortcut -> {
                val intent = Intent(this, NotesActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                intent.putExtra("prefix",pre)
                intent.putExtra("desc", desc)
                intent.putExtra("img", img)
                intent.putExtra("shortcut", true)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

                if (Build.VERSION.SDK_INT > 25) {
                    val shortcutManager = getSystemService(ShortcutManager::class.java)

                    val shortcut = ShortcutInfo.Builder(this, head)
                        .setShortLabel(head)
                        .setLongLabel(head)
                        .setIcon(Icon.createWithResource(this, img))
                        .setIntent(intent)
                        .build()

                    shortcutManager.requestPinShortcut(shortcut, null)
                    Toast.makeText(this, R.string.lanShortcutToast, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Shortcut creation not supported on your phone", Toast.LENGTH_SHORT).show()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
    
}