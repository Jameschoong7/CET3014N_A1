package com.cet3014n.cet3014n_a1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_activity)

        val etEmailOrPhone = findViewById<EditText>(R.id.resetPassword_emailOrPhone)
        val btnReset = findViewById<Button>(R.id.resetPassword_resetButton)

        btnReset.setOnClickListener {
            val input = etEmailOrPhone.text.toString()
            if (input.isNotEmpty()) {
                Toast.makeText(this, "Reset instructions sent to $input", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                etEmailOrPhone.error = "Please enter your email or phone number"
            }
        }
    }
}