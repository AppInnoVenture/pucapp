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
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.enableEdgeToEdge
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

class TextBookActivity : BaseActivity() {

    private val prefix: String by lazy { intent?.getStringExtra("prefix").toString() }
    private lateinit var descr: String
    private lateinit var head: String
    private val yt = "https://www.youtube.com/playlist?list="
    private var shortcut: Boolean = false
    private var isDarkMode: Boolean = false
    private var dual: Boolean = false
    private val img: Int by lazy { intent.getIntExtra("img", -1) }
    private lateinit var textItems: List<TextItem>
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
        descr = intent.getStringExtra("desc").toString()
        
        head = "$descr ${getString(R.string.tab_text)}"
        dual = intent.getBooleanExtra("dual", false)

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

        if ("dual" in prefix) {
            textItems = listOf(
                TextItem(getString(R.string.btn_eng), prefix.replace("dual", "e"),0,0,0,0),
                TextItem(getString(R.string.btn_kan), prefix.replace("dual", "k"),0,0,0,0)
            )
            dual = true
        } else
	        listItems()
       
        
        val itemAdapterLan = AdapterText(textItems) { item ->
            if ("dual" in prefix) {
                val intent = Intent(this, TextBookActivity::class.java)
                intent.putExtra("desc", descr + " " + item.descLan)
                intent.putExtra("prefix", item.extra)
                intent.putExtra("img", img)
                intent.putExtra("dual", true)
                startActivity(intent)
            } else if ("YouTube" in item.descLan)
                startActivity(Intent(Intent.ACTION_VIEW, item.extra.toUri()))
            else if ("Error" in item.descLan)
                startActivity(Intent(Intent.ACTION_VIEW, "https://t.me/karnataka_kea".toUri()))
            else {
                val pdfFile = File(filesDir, item.extra)
                if (pdfFile.exists() or isInternetAvailable())
                    openPdfViewer(item)
                else
                    showRetrySnackbar(item)
            }
        }

        binding.recyclerViewLan.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewLan.adapter = itemAdapterLan
        if (savedInstanceState == null) {
            binding.recyclerViewLan.layoutAnimation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
            if (basePreferences.getBoolean("dwd", false)) {
                val file = File(filesDir, basePreferences.getString("dwdPdf", null).toString())
                if (file.exists()) file.delete()
                basePreferences.edit { putBoolean("dwd", false) }
            }
        }
        onBackPressedDispatcher.addCallback(this) {
            if (shortcut) {
                basePreferences.edit { putInt("frag", 1) }
                startActivity(Intent(this@TextBookActivity, MainActivity::class.java).apply {
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
        "    अपठित\n",
        "    पद्य भाग\n",
        "    स्थूलवाचन\n",
        "    ಪ್ರಾಯೋಗಿಕ ವಿಭಾಗ\n"
    )
}
    private fun openPdfViewer(item: TextItem) {
        val intent = Intent(this, PdfTextActivity::class.java)
        intent.putExtra("desc", patterns.find { item.descLan.startsWith(it) }?.let { item.descLan.drop(it.length) } ?: item.descLan)
        intent.putExtra("prefix", item.extra)
        intent.putExtra("dual", dual)
        intent.putExtra("s1", item.s1)
        intent.putExtra("s2", item.s2)
        intent.putExtra("e1", item.e1)
        intent.putExtra("e2", item.e2)
        startActivity(intent)
    }

    private fun showRetrySnackbar(item: TextItem) {
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
            basePreferences.edit { putInt("frag", 1) }
            startActivity(Intent(this@TextBookActivity, MainActivity::class.java).apply {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra("pinShortcut", false)
            })
        } else
            onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_lan, menu)

        if ("dual" in prefix) menu?.get(2)?.isVisible = false
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
                val filesToDelete = textItems.map { File(filesDir, it.extra) }.filter { it.exists() }.toSet()

                handleFileDeletion(filesToDelete, getString(R.string.alertLanMsg), binding.root)
                return true
            }

            R.id.lan_shortcut -> {
                val intent = Intent(this, TextBookActivity::class.java)
                intent.action = Intent.ACTION_MAIN
                intent.putExtra("prefix",prefix)
                intent.putExtra("desc", descr)
                intent.putExtra("img", img)
                intent.putExtra("shortcut", true)
                intent.putExtra("dual", dual)
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

    private fun tbNo(): Int {
        return when (prefix) {
            "ara" -> 223
            "tam" -> 225
            "tel" -> 290
            "mal" -> 207
            else -> 241
        }
    }

    private fun ytLink(): String {
        return when (prefix) {
            "ara" -> "${yt}PLwgSxJHx2xAC2i8TVPIdj1tRqFLveg0qg"
            "tam" -> "${yt}PLwgSxJHx2xAAc69ZHygFmZ4RmCNCCySJd"
            "tel" -> "${yt}PLwgSxJHx2xAA04hpX5dxZwvPEURGdVV0N"
            "mal" -> "${yt}PLwgSxJHx2xAAO_9F2_3KwKTBtnUAvdRe-"
            else -> "${yt}PLwgSxJHx2xADX0C7KrDika-ZSLFgYZCdp"
        }
    }
    private fun descText(): String {
    return when (prefix) {
        "ara" -> "شاهد محاضرات الفيديو على YouTube"
        "tam" -> "YouTube இல் வீடியோ விரிவுரைகளைப் பார்க்கவும்"
        "tel" -> "YouTube లో వీడియో ఉపన్యాసాలు చూడండి"
        "mal" -> "YouTube-ൽ വീഡിയോ പ്രഭാഷണങ്ങൾ കാണുക"
        else -> "YouTube پر ویڈیو لیکچرز دیکھیں"
    }
    }

    private fun listItems() {
        

            // Dummy Placeholder
            // In main app it initialises a list containing different chapters of particular subject, simplified here to keep it private; happy to demo it in an interview! 
            
}