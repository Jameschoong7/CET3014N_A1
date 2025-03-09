package com.cet3014n.cet3014n_a1

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_activity)

        val etEmail = findViewById<EditText>(R.id.register_email)
        val etPassword = findViewById<EditText>(R.id.register_password)
        val etPhone = findViewById<EditText>(R.id.register_phone)
        val etSocialMedia = findViewById<EditText>(R.id.register_socialMedia)
        val etPaymentDetails = findViewById<EditText>(R.id.register_paymentDetails)
        val etDeliveryAddress = findViewById<EditText>(R.id.register_deliveryAddress)
        val btnRegister = findViewById<Button>(R.id.register_button)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val phone = etPhone.text.toString()
            val paymentDetails = etPaymentDetails.text.toString()
            val deliveryAddress = etDeliveryAddress.text.toString()

            if (validateInputs(email, password, phone, paymentDetails, deliveryAddress)) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fix the errors in the form", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(email: String, password: String, phone: String, paymentDetails: String, deliveryAddress: String): Boolean {
        var isValid = true

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            findViewById<EditText>(R.id.register_email).error = "Invalid email"
            isValid = false
        }
        if (password.length < 6) {
            findViewById<EditText>(R.id.register_password).error = "Password must be at least 6 characters"
            isValid = false
        }
        if (phone.length != 10 || !phone.all { it.isDigit() }) {
            findViewById<EditText>(R.id.register_phone).error = "Enter a 10-digit phone number"
            isValid = false
        }
        if (paymentDetails.isEmpty()) {
            findViewById<EditText>(R.id.register_paymentDetails).error = "Payment details required"
            isValid = false
        }
        if (deliveryAddress.isEmpty()) {
            findViewById<EditText>(R.id.register_deliveryAddress).error = "Delivery address required"
            isValid = false
        }

        return isValid
    }
}