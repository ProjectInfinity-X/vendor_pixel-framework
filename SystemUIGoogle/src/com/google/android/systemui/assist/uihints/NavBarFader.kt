package com.google.android.systemui.assist.uihints

import android.animation.ObjectAnimator
import android.os.Handler
import android.util.Property
import android.view.View

import com.android.systemui.navigationbar.NavigationBarController
import com.android.systemui.navigationbar.NavigationBarControllerImpl
import com.android.systemui.navigationbar.views.NavigationBarView

import com.google.android.systemui.assist.uihints.NgaMessageHandler

class NavBarFader(
    private val navigationBarController: NavigationBarController,
    private val handler: Handler
) : NgaMessageHandler.NavBarVisibilityListener {

    private var targetAlpha: Float
    private val onTimeout: Runnable
    private var animator: ObjectAnimator

    init {
        val defaultNavigationBarView = (navigationBarController as NavigationBarControllerImpl).defaultNavigationBarView
        targetAlpha = defaultNavigationBarView?.alpha ?: 1.0f
        onTimeout = Runnable { onVisibleRequest(true) }
        animator = ObjectAnimator()
    }

    override fun onVisibleRequest(visible: Boolean) {
        val defaultNavigationBarView = navigationBarController.defaultNavigationBarView ?: return
        handler.removeCallbacks(onTimeout)

        if (!visible) {
            handler.postDelayed(onTimeout, 10000L)
        }

        val newAlpha = if (visible) 1.0f else 0.0f
        if (newAlpha == targetAlpha) return

        animator.cancel()

        val currentAlpha = defaultNavigationBarView.alpha
        targetAlpha = newAlpha

        val duration = (80 * Math.abs(newAlpha - currentAlpha)).toLong()
        animator = ObjectAnimator.ofFloat(defaultNavigationBarView, View.ALPHA, currentAlpha, newAlpha).apply {
            setDuration(duration)
            if (visible) {
                startDelay = 80L
            }
        }
        animator.start()
    }
}
