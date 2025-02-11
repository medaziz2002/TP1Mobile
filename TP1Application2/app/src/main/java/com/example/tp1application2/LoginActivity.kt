package com.example.tp1application2


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        mAuth = FirebaseAuth.getInstance()

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)

        val loginButton: Button = findViewById(R.id.login)
        val backButton: Button = findViewById(R.id.back)

        loginButton.setOnClickListener { login() }

    }

    private fun login() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val userUI = Intent(this, MainActivity::class.java)

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs !", Toast.LENGTH_SHORT).show()
            return
        }

        mAuth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    if (user != null && user.isEmailVerified) {
                        startActivity(userUI)
                        Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Utilisateur non activé. Veuillez vérifier vos e-mails.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Email ou mot de passe incorrect. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
