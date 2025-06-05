package com.example.epic.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.epic.AdAwayApplication
import com.example.epic.R

fun Activity.getAdAwayApplication() = this.application as AdAwayApplication

@SuppressLint("ClickableViewAccessibility")
fun EditText.setDrawableClickListener(drawableIndex: Int, onClick: () -> Unit) {
    setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd =
                compoundDrawablesRelative[drawableIndex] // [ 0 start, 1 top, 2 end, 3 bottom]
            if (drawableEnd != null) {
                val touchX = event.x.toInt()
                val drawableWidth = drawableEnd.intrinsicWidth
                val viewWidth = width
                val paddingEnd = paddingEnd

                if (touchX >= viewWidth - paddingEnd - drawableWidth) {
                    onClick()
                    return@setOnTouchListener true
                }
            }
        }
        false
    }
}

fun Context.setupEULAAndPrivacyText(fullText: String, textView: TextView, onEULAClick: () -> Unit, onPrivacyClick: () -> Unit) {
    val eulaText = getString(R.string.eula)
    val privacyText = getString(R.string.privacy_policy)

    val spannable = SpannableStringBuilder(fullText)

    val eulaStart = fullText.indexOf(eulaText)
    spannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            onEULAClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = getColor(R.color.red)
            ds.isUnderlineText = false
        }
    }, eulaStart, eulaStart + eulaText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    val privacyStart = fullText.indexOf(privacyText)
    spannable.setSpan(object : ClickableSpan() {
        override fun onClick(widget: View) {
            onPrivacyClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = getColor(R.color.red)
            ds.isUnderlineText = false
        }
    }, privacyStart, privacyStart + privacyText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    with(textView) {
        text = spannable
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }
}