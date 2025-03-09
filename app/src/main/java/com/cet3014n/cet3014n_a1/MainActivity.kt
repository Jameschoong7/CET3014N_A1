package com.cet3014n.cet3014n_a1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        if (!prefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        val btnBrowse = findViewById<Button>(R.id.start_browsing_button)
        btnBrowse.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        val btnLogout = findViewById<Button>(R.id.logout_button)
        btnLogout.setOnClickListener {
            startActivity(Intent(this, LogoutActivity::class.java))
        }
    }
}