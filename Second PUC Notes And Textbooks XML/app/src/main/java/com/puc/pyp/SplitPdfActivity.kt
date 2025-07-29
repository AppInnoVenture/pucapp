package com.puc.pyp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.snackbar.Snackbar
import com.puc.pyp.databinding.ActivitySplitPdfBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class SplitPdfActivity : BaseActivity() {

    private val sharedPreferences: SharedPreferences by lazy { getSharedPreferences("pdfPrefs", MODE_PRIVATE) }
    private lateinit var pdfAbove: PDFView
    private lateinit var pdfBelow: PDFView
    private var pdf1: String? = null
    private var pdf2: String? = null
    private var s = 0
    private var s2: Int = 0
    private var e: Int = 0
    private var e2: Int = 0
    private var progress: Int = 0
    private var isNight = false
    private var fileName: String? = null
    private var isDownloading = false
    private lateinit var binding: ActivitySplitPdfBinding
    private lateinit var pdfFile: File
    private lateinit var anspdfFile: File
    private lateinit var progressBar: ProgressBar
    
    private val viewModel: DownloadViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = setContentView(this, R.layout.activity_split_pdf)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = ( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
        binding.root.requestLayout()

        progressBar = binding.ansprogressBar
        pdf1 = intent.getStringExtra("pdf1").toString()
        pdf2 = intent.getStringExtra("pdf2").toString()
        s = intent.getIntExtra("s", 0)
        e = intent.getIntExtra("e", 0)
        s2 = intent.getIntExtra("s2", 0)
        e2 = intent.getIntExtra("e2", 0)
        isNight = intent.getBooleanExtra("isNight", false)
        pdfAbove = binding.pdfAbove
        pdfBelow = binding.pdfBelow
        val handle = binding.handle
        val linearLayout = binding.linearLayout
        
        if (savedInstanceState != null) {
            isDownloading = savedInstanceState.getBoolean("isDownloading", false)
            
        }

        pdfFile = File(filesDir, pdf1)
        if ((fileName == null) or (fileName.startsWith("ans")))
            if (pdfFile.exists())
                openPdf()
            else
                downloadPdf(pdf1)

        anspdfFile = File(filesDir, pdf2)
        if ((fileName == null) or (!fileName.startsWith("ans")))
            if (anspdfFile.exists())
                openAnsPdf()
            else
                downloadPdf(pdf2)

        onBackPressedDispatcher.addCallback(this) {
            if (isDownloading){
                viewModel.stopDownload(this@SplitPdfActivity)
                
            }
            finish()
        }

        
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        handle.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
            
    // Dummy Placeholder
                }

                MotionEvent.ACTION_MOVE -> {
                       
    // Dummy Placeholder 
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                      
    // Dummy Placeholder  
                }

                else -> false
            }
        }

lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.downloadState.collect { state ->
                    isDownloading = state.workId != null
                    if (state.workId == null && state.state == null) {
                        isDownloading = false
                        return@collect
                    }

                    when (state.state) {
                        "running" -> {
                            // some private codes are removed, currently it shows only simplified version 
    
                        }

                        "complete", "already_exists" -> {
                            
                        }

                        "failed", "canceled" -> {
                            
                        }

                        else -> {
                            isDownloading = false
                            progressBar.visibility = View.GONE
                            progressInd.progress = 0
                            progressText.text = "0%"
                        }
                    }
                }
            }
        }
    }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt("progress", progress)
            putBoolean("isDownloading", isDownloading)
            putString("fileName", fileName)
        }
    }

    override fun onResume() {
        super.onResume()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onPause() {
        super.onPause()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        sharedPreferences.edit {
            putInt("$pdf1$s", pdfAbove.currentPage)
            putInt("$pdf2$s2", pdfBelow.currentPage) }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    private fun openPdf() {
        try {
            pdfAbove.alpha = 0f
        pdfAbove.fromFile(pdfFile).nightMode(isNight)
            .defaultPage(sharedPreferences.getInt("$pdf1$s", 0))
            .scrollHandle(DefaultScrollHandle(this))
            .onError { throwable ->
                handlerPdf(throwable = throwable, file = pdfFile)
            }.password(REDACTED).load()
        pdfAbove.animate().alpha(1f).setDuration(600).start()
        pdfAbove.setBackgroundColor(if (isNight) Color.BLACK else Color.WHITE)
    } catch (e: Exception) {
            Toast.makeText(this, R.string.errr, Toast.LENGTH_LONG).show()
        }
    }

    private fun openAnsPdf() {
        try {
            pdfBelow.alpha = 0f
        pdfBelow.fromFile(anspdfFile).nightMode(isNight)
            .defaultPage(sharedPreferences.getInt("$pdf2$s2", 0))
            .scrollHandle(DefaultScrollHandle(this))
            .onError { throwable ->
                handlerPdf(throwable = throwable, file = anspdfFile)
            }.password(REDACTED).load()
        pdfBelow.animate().alpha(1f).setDuration(600).start()
        pdfBelow.setBackgroundColor(if (isNight) Color.BLACK else Color.WHITE)
    } catch (e: Exception) {
            Toast.makeText(this, R.string.errr, Toast.LENGTH_LONG).show()
        }
    }

    private fun handlerPdf(throwable: Throwable, file: File) {
        if (throwable is IOException) {
            file.delete()
            Snackbar.make(binding.root, R.string.errdwd, Snackbar.LENGTH_LONG)
                .setAction(R.string.snack_yes) {
                    if (isInternetAvailable()) downloadPdf( file.name)
                    else Toast.makeText(this, R.string.noNet, Toast.LENGTH_SHORT).show()
                }.show()
        } else
            Toast.makeText(this, R.string.errr, Toast.LENGTH_SHORT).show()
    }

    private fun downloadPdf(dwdName: String) {
        if (isDownloading)
            return

        isDownloading = true
        fileName = dwdName

        if (!fileName.startsWith("ans")) progressBar = binding.progressBar

        Toast.makeText(this, R.string.dwding, Toast.LENGTH_SHORT).show()

        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0

        viewModel.startDownload(this, "$REDACTED$dwdName", dwdName)
    }

}