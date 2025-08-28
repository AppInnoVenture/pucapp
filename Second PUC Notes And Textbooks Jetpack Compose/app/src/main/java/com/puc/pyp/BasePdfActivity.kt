package com.puc.pyp

import android.graphics.Color
import android.os.Build
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BasePdfActivity : BaseActivity() {

    protected var isFullScreen = false
    protected var isRotationLocked = false

    @Suppress("DEPRECATION")
    protected fun toggleFullScreen() {
        val rootView = getRootView()

        TransitionManager.beginDelayedTransition(rootView as RelativeLayout, AutoTransition().setDuration(500))
        if (isFullScreen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                window.statusBarColor = Color.BLACK
                window.navigationBarColor = Color.BLACK
            }
            getDescPdf().isVisible = true
            isFullScreen = false
            ViewCompat.requestApplyInsets(rootView)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let {
                    it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
                window.statusBarColor = Color.TRANSPARENT
                window.navigationBarColor = Color.TRANSPARENT
            }
            getDescPdf().isVisible = false
            isFullScreen = true
        }
        rootView.requestLayout()
    }

    
    private fun rotateAntiClockwise() {
               
    // Dummy Placeholder
    // In main app it determines current orientation and changes the orientation by 90° , simplified here to keep it private; happy to demo it in an interview! 
   
        isRotationLocked = true
        Toast.makeText(this,  R.string.toast_rotated_anticlockwise, Toast.LENGTH_SHORT).show()
    }

    private fun rotateClockwise() {
     // Dummy Placeholder
    // In main app it determines current orientation and changes the orientation by -90°, simplified here to keep it private; happy to demo it in an interview! 
    

        isRotationLocked = true
        Toast.makeText(this, R.string.toast_rotated_clockwise, Toast.LENGTH_SHORT).show()
    }

    protected fun showKeyboardForDialog(dialog: AlertDialog, editText: TextInputEditText) {
        val keyboardJob = lifecycleScope.launch {
            delay(300)
            if (!dialog.isShowing || isFinishing) return@launch
            editText.apply {
                isFocusable = true
                isFocusableInTouchMode = true
                requestFocus()
                selectAll()
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
        dialog.setOnDismissListener {
            keyboardJob.cancel()
        }
    }

    abstract fun getRootView(): View
    abstract fun getDescPdf(): View
}