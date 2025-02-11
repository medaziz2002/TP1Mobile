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

class SignupActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

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
    }

    private fun signUp() {
        val username = usernameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()
        val intent = Intent(this, LoginActivity::class.java)

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields !", Toast.LENGTH_SHORT).show()
        } else if (password == confirmPassword) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = mAuth.currentUser

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(username)
                            .build()

                        user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                user.sendEmailVerification().addOnCompleteListener { emailTask ->
                                    if (emailTask.isSuccessful) {
                                        Toast.makeText(this, "Sign up successful! Email verification sent to ${user.email}", Toast.LENGTH_SHORT).show()
                                        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
                                        userRef.child("username").setValue(username)
                                        userRef.child("email").setValue(email)
                                        userRef.child("role").setValue("user")
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, "Failed to send email verification. ${emailTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Failed to update username. ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
        }
    }
}
