package com.example.epic.common

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.transition.Fade
import androidx.transition.TransitionManager

private const val DEFAULT_DURATION = 300L

fun View.animationWidth(end: Int, duration: Long = DEFAULT_DURATION) {
    animationWidth(
        start = measuredWidth,
        end = end,
        duration = duration
    )
}

fun View.animationWidth(start: Int, end: Int, duration: Long = DEFAULT_DURATION) {
    valueOfIntAnimation(start, end, duration) { updateLayoutParams { width = it } }
}

fun View.animationHeight(end: Int, duration: Long = DEFAULT_DURATION) {
    animationHeight(
        start = measuredHeight,
        end = end,
        duration = duration
    )
}

fun View.animationHeight(start: Int, end: Int, duration: Long = DEFAULT_DURATION) {
    valueOfIntAnimation(start, end, duration) { updateLayoutParams { height = it } }
}

fun View.animationPaddingLeft(end: Int, duration: Long = DEFAULT_DURATION) {
    animationPaddingLeft(
        start = paddingLeft,
        end = end,
        duration = duration
    )
}

fun View.animationPaddingLeft(start: Int, end: Int, duration: Long = DEFAULT_DURATION) {
    valueOfIntAnimation(start, end, duration) { updatePadding(left = it) }
}

fun View.animationPaddingTop(end: Int, duration: Long = DEFAULT_DURATION) {
    animationPaddingTop(
        start = paddingTop,
        end = end,
        duration = duration
    )
}

fun View.animationPaddingTop(start: Int, end: Int, duration: Long = DEFAULT_DURATION) {
    valueOfIntAnimation(start, end, duration) { updatePadding(top = it) }
}

fun View.animationPaddingRight(end: Int, duration: Long = DEFAULT_DURATION) {
    animationPaddingRight(
        start = paddingRight,
        end = end,
        duration = duration
    )
}

fun View.animationPaddingRight(start: Int, end: Int, duration: Long = DEFAULT_DURATION) {
    valueOfIntAnimation(start, end, duration) { updatePadding(right = it) }
}

@SuppressLint("Recycle")
fun valueOfIntAnimation(start: Int, end: Int, duration: Long, action: (Int) -> Unit) {
    ValueAnimator.ofInt(start, end)
        .default(duration, action)
}

@SuppressLint("Recycle")
fun valueOfFloatAnimation(start: Float, end: Float, duration: Long, action: (Float) -> Unit) {
    ValueAnimator.ofFloat(start, end)
        .default(duration, action)
}

private fun <T> ValueAnimator.default(duration: Long, action: (T) -> Unit) = apply {
    addUpdateListener { action.invoke(it.animatedValue as T) }
}.setDuration(duration)
    .start()

fun View.setVisibilityAnimation(
    visible: Boolean,
    duration: Long = DEFAULT_DURATION,
    isHideInVisible: Boolean = false,
    endActionFun: () -> Unit = { }
) {
    val alpha = if (visible) 1.0F else 0.0F
    animate().alpha(alpha)
        .setDuration(duration)
        .withStartAction {
            if (visible) {
                visibility = View.VISIBLE
            }
        }.withEndAction {
            if (!visible) {
                visibility = if (isHideInVisible) {
                    View.INVISIBLE
                } else {
                    View.GONE
                }
            }

            endActionFun.invoke()
        }
}

fun View.setVisibilityTransitionAnimation(
    visible: Boolean,
    duration: Long = DEFAULT_DURATION,
    isHideInVisible: Boolean = false
) {
    with(Fade()) {
        this.duration = duration

        addTarget(this@setVisibilityTransitionAnimation)

        TransitionManager.beginDelayedTransition(
            this@setVisibilityTransitionAnimation.parent as ViewGroup,
            this
        )
    }

    if (visible) {
        isVisible = true
    } else {
        if (isHideInVisible) {
            isInvisible = true
        } else {
            isVisible = false
        }
    }
}

fun View.translationRotateHalfCircle(
    isVisible: Boolean? = null,
    onAnimationStart: () -> Unit = { },
    onAnimationEnd: () -> Unit = { },
    duration: Long = DEFAULT_DURATION,
) {
    val rotation = if (isVisible ?: (rotation > 0F)) 0F else 180F
    animate()
        .setInterpolator(AccelerateDecelerateInterpolator())
        .rotation(rotation)
        .setDuration(duration)
        .setListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    onAnimationStart.invoke()
                }

                override fun onAnimationEnd(animation: Animator) {
                    onAnimationEnd.invoke()
                }

                override fun onAnimationCancel(animation: Animator) {
                    this@translationRotateHalfCircle.rotation = rotation
                }

                override fun onAnimationRepeat(animation: Animator) {}
            }
        )
}
