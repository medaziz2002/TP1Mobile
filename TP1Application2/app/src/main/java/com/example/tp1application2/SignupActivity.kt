package com.example.tp1application2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class SignupActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup1)

        mAuth = FirebaseAuth.getInstance()

        usernameEditText = findViewById(R.id.username)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        confirmPasswordEditText = findViewById(R.id.cpassword)

        val back: Button = findViewById(R.id.back)
        val signUpButton: Button = findViewById(R.id.signup)

        signUpButton.setOnClickListener {
            signUp()
        }
        back.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun signUp() {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val intent = Intent(this, LoginActivity::class.java)

        // Check if any fields are empty
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show()
        } else if (password == confirmPassword) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = mAuth.currentUser

                        if (user != null) {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build()

                            // Update user profile with username
                            user.updateProfile(profileUpdates).addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    // Send email verification
                                    user.sendEmailVerification().addOnCompleteListener { emailTask ->
                                        if (emailTask.isSuccessful) {
                                            Toast.makeText(this, getString(R.string.successful_signup, user.email), Toast.LENGTH_SHORT).show()

                                            // Save user data to Firebase Realtime Database
                                            val userRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)

                                            // Create a map with user data
                                            val userMap = mapOf(
                                                "username" to username,
                                                "email" to email,
                                                "role" to "user",
                                                "events" to mapOf<String, Any>() // Initialize empty events node
                                            )

                                            userRef.setValue(userMap).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    // Data successfully saved
                                                    Toast.makeText(this, getString(R.string.user_data_saved), Toast.LENGTH_SHORT).show()

                                                    // Redirect to login page
                                                    startActivity(intent)
                                                } else {
                                                    // Error saving user data
                                                    Log.e("Firebase", "Error saving user data: ${task.exception?.message}")
                                                    Toast.makeText(this, getString(R.string.failed_to_save_user_data, task.exception?.message), Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(this, getString(R.string.failed_to_send_email_verification, emailTask.exception?.message), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, getString(R.string.failed_to_update_username, updateTask.exception?.message), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Log.e("Firebase", "User is null after sign up.")
                            Toast.makeText(this, getString(R.string.user_is_null_after_signup), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.signup_failed, task.exception?.message), Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show()
        }
    }
}
