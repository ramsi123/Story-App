package com.example.submissionintermediateandroid.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.submissionintermediateandroid.R
import com.example.submissionintermediateandroid.data.Result
import com.example.submissionintermediateandroid.databinding.ActivityRegisterBinding
import com.example.submissionintermediateandroid.ui.viewmodels.RegisterViewModel
import com.example.submissionintermediateandroid.ui.viewmodels.ViewModelFactory
import com.example.submissionintermediateandroid.util.dataStore

class RegisterActivity : AppCompatActivity() {

    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this, application.dataStore)
    }
    private lateinit var binding: ActivityRegisterBinding

    private var name = ""
    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        registerViewModel.userRegister.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)

                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.signup_title_alert))
                            setMessage(getString(R.string.signup_message_alert, email))
                            setPositiveButton(getString(R.string.alert_dialog_positive_button)) { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)

                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.alert_dialog_error_title))
                            setMessage(result.error)
                            setPositiveButton(getString(R.string.alert_dialog_positive_button)) { _, _ -> }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val titleTextView = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(400)
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(400)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(400)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(400)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(400)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(400)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(400)
        val signUpButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(
                titleTextView,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signUpButton
            )
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            name = binding.edRegisterName.text.toString()
            email = binding.edRegisterEmail.text.toString()
            password = binding.edRegisterPassword.text.toString()
            registerViewModel.register(name, email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}