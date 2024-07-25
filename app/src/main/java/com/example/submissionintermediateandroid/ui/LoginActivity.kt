package com.example.submissionintermediateandroid.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
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
import com.example.submissionintermediateandroid.databinding.ActivityLoginBinding
import com.example.submissionintermediateandroid.ui.viewmodels.LoginViewModel
import com.example.submissionintermediateandroid.ui.viewmodels.ViewModelFactory
import com.example.submissionintermediateandroid.util.dataStore

class LoginActivity : AppCompatActivity() {

    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, application.dataStore)
    }
    private lateinit var binding: ActivityLoginBinding

    private var email = ""
    private var password = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
        setupAction()

        loginViewModel.userLogin.observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        val user = result.data

                        // save token
                        user.loginResult?.token?.let { token ->
                            loginViewModel.saveToken(token)
                        }

                        // create alert dialog
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.login_title_alert, user.loginResult?.name))
                            setMessage(getString(R.string.login_message_alert))
                            setPositiveButton(getString(R.string.alert_dialog_positive_button)) { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
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
        val messageTextView = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(400)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(400)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(400)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(400)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(400)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(400)

        AnimatorSet().apply {
            playSequentially(
                titleTextView,
                messageTextView,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                loginButton
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
        binding.loginButton.setOnClickListener {
            email = binding.edLoginEmail.text.toString()
            password = binding.edLoginPassword.text.toString()
            loginViewModel.login(email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}