package id.indosw.datetimepicker.lib.dialog

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import id.indosw.datetimepicker.lib.R

class BottomSheetHelper(private val context: Context, private val layoutId: Int) {
    private var view: View? = null
    private var listener: Listener? = null
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var windowManager: WindowManager? = null
    private var focusable = false
    private fun init() {
        handler.postDelayed({
            if (context is Activity) {
                windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                view = LayoutInflater.from(context).inflate(layoutId, null, true)

                // Don't let it grab the input focus if focusable is false
                val flags = if (focusable) 0 else WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                var layoutParams = WindowManager.LayoutParams( // Shrink the window to wrap the content rather than filling the screen
                        WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                        flags,  // Make the underlying application window visible through any transparent parts
                        PixelFormat.TRANSLUCENT)
                if ((layoutParams.softInputMode
                                and WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION) == 0) {
                    val nl = WindowManager.LayoutParams()
                    nl.copyFrom(layoutParams)
                    nl.softInputMode = nl.softInputMode or WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION
                    layoutParams = nl
                }
                windowManager!!.addView(view, layoutParams)
                view!!.findViewById<View>(R.id.bottom_sheet_background)
                        .setOnClickListener { hide() }
                view!!.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        view!!.viewTreeObserver.removeOnPreDrawListener(this)
                        if (listener != null) {
                            listener!!.onLoaded(view)
                        }
                        animateBottomSheet()
                        return false
                    }
                })
            }
        }, 100)
    }

    fun setListener(listener: Listener?): BottomSheetHelper {
        this.listener = listener
        return this
    }

    fun setFocusable(focusable: Boolean) {
        this.focusable = focusable
    }

    fun display() {
        init()
    }

    fun hide() {
        handler.postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, view!!.height.toFloat())
            objectAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view!!.visibility = View.GONE
                    if (listener != null) {
                        listener!!.onClose()
                    }
                    remove()
                }
            })
            objectAnimator.start()
        }, 200)
    }

    fun dismiss() {
        remove()
    }

    private fun remove() {
        if (view!!.windowToken != null) windowManager!!.removeView(view)
    }

    private fun animateBottomSheet() {
        val objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view!!.height.toFloat(), 0f)
        objectAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (listener != null) {
                    listener!!.onOpen()
                }
            }
        })
        objectAnimator.start()
    }

    interface Listener {
        fun onOpen()
        fun onLoaded(view: View?)
        fun onClose()
    }

}