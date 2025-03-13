package com.cet3014n.cet3014n_a1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val etEmail = findViewById<EditText>(R.id.login_email)
        val etPassword = findViewById<EditText>(R.id.login_password)
        val btnLogin = findViewById<Button>(R.id.login_button)
        val btnRegister = findViewById<Button>(R.id.login_registerButton)
        val btnForgotPassword = findViewById<Button>(R.id.login_forgotPasswordButton)

        // Check if already logged in
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (prefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val loggedInUser = validateLogin(email, password) // Modified to return User object

            if (loggedInUser != null) { // Check if login was successful (User object returned)
                prefs.edit().putBoolean("is_logged_in", true).apply()

                // **Save User object to SharedPreferences**
                val gson = Gson()
                val userJson = gson.toJson(loggedInUser) // Serialize User object to JSON
                val userPrefs = getSharedPreferences(AccountsActivity.USER_PREFS_NAME, Context.MODE_PRIVATE) // Use AccountsActivity's PREFS_NAME
                userPrefs.edit().putString(AccountsActivity.LOGGED_IN_USER_KEY, userJson).apply() // Save JSON string

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    private fun validateLogin(email: String, password: String): User? { // Modified return type to User?
        try {
            val inputStream = assets.open("users.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val userType = object : TypeToken<List<User>>() {}.type
            val users: List<User> = gson.fromJson(jsonString, userType)

            // Return the User object if login is successful, otherwise return null
            return users.find { it.email == email && it.password == password }

        } catch (e: Exception) {
            e.printStackTrace() // Log the error for debugging
            Toast.makeText(this, "Error reading user data", Toast.LENGTH_SHORT).show()
            return null // Indicate login failure due to error
        }
    }
}