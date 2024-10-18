package com.example.notesapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var resetButton: Button
    private lateinit var backButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.emailEditText)
        resetButton = findViewById(R.id.resetButton)
        backButton = findViewById(R.id.backButton)

        resetButton.setOnClickListener {
            resetPassword()
        }

        backButton.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }

    private fun resetPassword() {
        val email = emailEditText.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(this, "Masukkan email Anda", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "If this email is registered, you will receive a password reset link.", Toast.LENGTH_SHORT).show()
                    finish() // Kembali ke halaman login
                } else {
                    Toast.makeText(this, "Gagal mengirim email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
