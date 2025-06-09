package com.puc.pyp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.puc.pyp.databinding.ActivityLanBinding
import com.puc.pyp.databinding.ToolbarLanBinding

class LanActivity : BaseActivity() {

    private lateinit var descr: String
    private lateinit var head: String
    private var shortcut: Boolean = false
    private var isDarkMode: Boolean = false
    private var img: Int = -1
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var lanitems: List<YearItem>
    private lateinit var binding: ActivityLanBinding

    override fun onCreate(savedInstanceState: Bundle?) {

    	sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        isDarkMode = sharedPreferences.getBoolean("dark_mode", false)

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
        descr = intent.getStringExtra("desc").toString()
        img = intent.getIntExtra("img", -1)
        head = "$descr Question Papers"

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.navigationIcon?.setTint(Color.BLACK)
        binding.toolbar.overflowIcon?.setTint(Color.BLACK)

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

        lanitems = listOf(
            YearItem("Question Papers section has been moved to a different app.\nClick this button to download the app from the Play Store.", "")
        )

        val itemAdapterLan = AdapterLan(lanitems) {
            startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.puc.notes".toUri()))
        }

        binding.recyclerViewLan.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewLan.adapter = itemAdapterLan

        onBackPressedDispatcher.addCallback(this) {
            if (shortcut) {
                sharedPreferences.edit { putInt("frag", 1) }
                startActivity(Intent(this@LanActivity, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("pinShortcut", false)
                })
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        if (shortcut) {
                sharedPreferences.edit { putInt("frag", 0) }
                startActivity(Intent(this@LanActivity, MainActivity::class.java).apply {
                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra("pinShortcut", false)
                })
            } else
            onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lan, menu)
        menu?.get(2)?.isVisible = false

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.lan_share -> {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, R.string.shareApp)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share app via"))
                return true
            }

            R.id.lan_exit -> {
                Toast.makeText(this, R.string.exitToast, Toast.LENGTH_SHORT).show()
                finishAffinity()
                return true
            }

            R.id.lan_shortcut -> {
                val intent = Intent(this, LanActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                intent.putExtra("desc", descr)
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