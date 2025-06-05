package com.example.epic.ui.authorization

import android.graphics.Typeface
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
import com.example.epic.common.ValidatorUtils
import com.example.epic.common.openExternalUrl
import com.example.epic.common.setDrawableClickListener
import com.example.epic.common.setupEULAAndPrivacyText
import com.example.epic.coroutine.subscribe
import com.example.epic.databinding.AuthFragmentLayoutBinding
import com.example.epic.ui.base.fragment.BaseFragmentNC
import com.example.epic.ui.base.viewmodel.UiState
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthFragment :
    BaseFragmentNC<AuthViewModel, AuthFragmentLayoutBinding>(R.layout.auth_fragment_layout) {
    override val viewModel: AuthViewModel by viewModel()
    override val binding: AuthFragmentLayoutBinding by viewBinding()


    override fun initListeners() {
        with(binding) {
            email.doAfterTextChanged {
                if (emailError.isVisible) {
                    viewModel.updateState(UiState.READY)
                }
                viewModel.updateCleanupEmailVisibility(email.text?.isNotEmpty() != false)
            }
            email.setDrawableClickListener(2) {
                email.setText(EMPTY_STRING)
            }
        }
        setupLoginListener()
    }

    override fun initView() {
        requireContext().setupEULAAndPrivacyText(
            fullText = getString(R.string.by_logging_in_you_agree_to_our_eula_and_privacy_policy),
            textView = binding.eulaAndPolicy,
            onEULAClick = { requireContext().openExternalUrl(TERMS) },
            onPrivacyClick = {
                requireContext().openExternalUrl(PRIVACY_POLICY)
            }
        )
        viewModel.updateState(UiState.READY)
        setWelcomeText()
    }

    override fun collectFlows() {
        with(viewModel) {
            isLoginSuccess.subscribe(lifecycleScope) {
                    navigateFragment(R.id.action_authFragment_to_checkCodeFragment)
            }
            isEmailCleanupVisible.subscribe(lifecycleScope) {
                val drawable = if (it) AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_cleanup_24
                ) else null
                binding.email.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        R.drawable.ic_mail_24
                    ), null, drawable, null
                )
            }
        }
    }

    override fun onSuccess() {
        binding.loginButton.isEnabled = true
        binding.emailError.isVisible = false
    }

    override fun onLoading() {
        binding.loginButton.isEnabled = false
        binding.emailError.isVisible = false
    }

    override fun onError(message: String, code: Int) {
        when (code) {
            LOGIN_ERROR_CODE -> {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                binding.loginButton.isEnabled = true
            }

            EMAIL_ERROR_CODE -> {
                binding.emailError.apply {
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
                val email = binding.email.text.toString()
                when {
                    !ValidatorUtils.validateEmail(email) -> {
                        viewModel.updateState(
                            UiState.ERROR(
                                getString(R.string.invalid_email),
                                EMAIL_ERROR_CODE
                            )
                        )
                    }
                    else -> viewModel.login(email)
                }
            }
        }
    }

    private  fun setWelcomeText(){
        val fullText = getString(R.string.welcome_to_avlyx)
        val spannable = SpannableString(fullText)

        val checkYourEnd = fullText.indexOf(getString(R.string.app_name))
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            checkYourEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val emailEnd = fullText.length
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red)),
            checkYourEnd,
            emailEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.welcomeText.text = spannable

    }
}