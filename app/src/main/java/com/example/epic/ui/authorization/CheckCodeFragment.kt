package com.example.epic.ui.authorization

import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.epic.R
import com.example.epic.common.EMPTY_STRING
import com.example.epic.common.PRIVACY_POLICY
import com.example.epic.common.TERMS
import com.example.epic.common.openExternalUrl
import com.example.epic.common.orDefault
import com.example.epic.common.setDrawableClickListener
import com.example.epic.common.setVisibilityTransitionAnimation
import com.example.epic.common.setupEULAAndPrivacyText
import com.example.epic.coroutine.subscribe
import com.example.epic.databinding.CheckCodeFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.base.viewmodel.UiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class CheckCodeFragment :
    BaseFragmentNC<CheckCodeViewModel, CheckCodeFragmentLayoutBinding>(R.layout.check_code_fragment_layout) {
    override val viewModel: CheckCodeViewModel by viewModel()
    override val binding: CheckCodeFragmentLayoutBinding by viewBinding()
    private var countDownTimer: CountDownTimer? = null

    override fun initListeners() {
        with(binding) {
            code.doAfterTextChanged {
                if (codeError.isVisible) {
                    viewModel.updateState(UiState.READY)
                }
                viewModel.updateCleanupCodeVisibility(code.text?.isNotEmpty().orDefault())
            }
            code.setDrawableClickListener(2) {
                code.setText(EMPTY_STRING)
            }
            resendCodeButton.setOnClickListener {
                viewModel.sendCode()
            }
        }
        setupLoginListener()
    }

    override fun initView() {
        requireContext().setupEULAAndPrivacyText(
            fullText = getString(R.string.by_logging_in_you_agree_to_our_eula_and_privacy_policy),
            textView = binding.eulaAndPolicy,
            onEULAClick = { requireContext().openExternalUrl(TERMS) },
            onPrivacyClick = { requireContext().openExternalUrl(PRIVACY_POLICY) }
        )
        setWelcomeText()
        startCountdown()
        viewModel.updateState(UiState.READY)
    }


    private fun startCountdown() {
        binding.resendCodeButton.setVisibilityTransitionAnimation(false)

        // TODO: create timer class
        countDownTimer?.cancel() // cancel existing timer if any

        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.timerText.text = "Resend code in ${secondsLeft}s"
            }

            override fun onFinish() {
                binding.timerText.text = "You can resend the code."
                binding.resendCodeButton.setVisibilityTransitionAnimation(true)
            }
        }.start()
    }


    override fun collectFlows() {
        with(viewModel) {
            isCodeSend.subscribe(lifecycleScope) {
                if (it != null) {
                    startCountdown()
                }
            }
            isLoginSuccess.subscribe(lifecycleScope) {
                if (it != null) {
                    popUpToFragment(
                        R.id.action_checkCodeFragment_to_tabFragment,
                        null,
                        R.id.authFragment
                    )
                }
            }
            codeCleanupVisible.subscribe(lifecycleScope) {
                val drawable = if (it) AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_cleanup_24
                ) else null
                binding.code.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, null, drawable, null
                )
            }
        }
    }

    override fun onSuccess() {
        binding.loginButton.isEnabled = true
        binding.codeError.isVisible = false
    }

    override fun onLoading() {
        binding.loginButton.isEnabled = false
        binding.codeError.isVisible = false
    }

    override fun onError(message: String, code: Int) {
        when (code) {
            LOGIN_ERROR_CODE -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                binding.loginButton.isEnabled = true
            }

            EMAIL_ERROR_CODE -> {
                binding.codeError.apply {
                    text = message
                    isVisible = true
                }
                binding.loginButton.isEnabled = true
            }
        }
    }

    private fun setupLoginListener() {
        with(binding.loginButton) {
            setOnClickListener {
                val code = binding.code.text.toString()
                viewModel.checkCode(code)
            }
        }
    }

    private fun setWelcomeText() {
        val fullText = getString(R.string.check_your_email)
        val spannable = SpannableString(fullText)

        val checkYourEnd = fullText.indexOf(getString(R.string.email))
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            checkYourEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val emailStart = checkYourEnd
        val emailEnd = fullText.length
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red)),
            emailStart,
            emailEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.welcomeText.text = spannable

    }

    override fun onDestroyView() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onDestroyView()
    }
}