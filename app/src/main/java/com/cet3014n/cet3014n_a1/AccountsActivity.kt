package com.cet3014n.cet3014n_a1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class AccountsActivity : AppCompatActivity() {

    private lateinit var accountsTitleTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var resetPasswordButton: Button
    private lateinit var viewVouchersButton: Button
    private lateinit var logoutButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
         const val USER_PREFS_NAME = "UserPrefs" // Preference file name for user data
         const val LOGGED_IN_USER_KEY = "loggedInUser" // Key to store User object as JSON
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.accounts_activity)

        accountsTitleTextView = findViewById(R.id.accountsTitleTextView)
        userNameTextView = findViewById(R.id.userNameTextView)
        userEmailTextView = findViewById(R.id.userEmailTextView)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        viewVouchersButton = findViewById(R.id.viewVouchersButton)
        logoutButton = findViewById(R.id.logoutButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        sharedPreferences = getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE) // Initialize SharedPreferences

        // Set up Bottom Navigation
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)

        // **Load User Profile Data from SharedPreferences**
        loadUserProfile()

        // Button Click Listeners (Placeholder Toasts for now)
        resetPasswordButton.setOnClickListener {
            Toast.makeText(this, "Reset Password Clicked (Functionality to be implemented)", Toast.LENGTH_SHORT).show()
            // In a real app: Start password reset flow (e.g., open reset password activity or dialog)
        }

        viewVouchersButton.setOnClickListener {
            startActivity(Intent(this, VouchersActivity::class.java)) // Start VouchersActivity
        }

        logoutButton.setOnClickListener {
            startActivity(Intent(this, LogoutActivity::class.java))
        }
    }

    private fun loadUserProfile() {
        val gson = Gson()
        val userJson = sharedPreferences.getString(LOGGED_IN_USER_KEY, null) // Retrieve User JSON string
        if (!userJson.isNullOrBlank()) {
            val user = gson.fromJson(userJson, User::class.java) // Deserialize JSON to User object
            displayUserProfile(user)
        } else {
            // No user data found in SharedPreferences, handle accordingly (e.g., show error, redirect to login)
            userNameTextView.text = "Name: Not Logged In" // Or show a message like "Not logged in"
            userEmailTextView.text = "Email: N/A"
            Toast.makeText(this, "User data not found. Please log in.", Toast.LENGTH_LONG).show()
            // In a real app: You might want to redirect to the LoginActivity here.
        }
    }

    private fun displayUserProfile(user: User) {
        userNameTextView.text = "Name: ${user.name ?: "N/A"}" // Assuming User data class has 'name' property
        userEmailTextView.text = "Email: ${user.email}"
    }
}