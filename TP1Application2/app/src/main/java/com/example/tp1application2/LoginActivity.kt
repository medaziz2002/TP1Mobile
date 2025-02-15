package com.example.tp1application2


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.login1)

        mAuth = FirebaseAuth.getInstance()

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)

        val loginButton: Button = findViewById(R.id.login)
        val backButton: Button = findViewById(R.id.back)

        loginButton.setOnClickListener { login() }
        backButton.setOnClickListener {
            startActivity(Intent(this, LandingPageActivity::class.java))
        }

        // Set the default language to French
        setLocale("fr")  // Change to "en" for English or any other locale
    }

    private fun login() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val userUI = Intent(this, MainActivity::class.java)

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        startActivity(userUI)
                        Toast.makeText(this, getString(R.string.successful_login), Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, getString(R.string.email_not_verified), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.error_incorrect_email_password, task.exception?.message), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
    }
}
