package com.cet3014n.cet3014n_a1

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TrackOrderActivity : AppCompatActivity() {

    private lateinit var orderIdTextView: TextView
    private lateinit var orderStatusTextView: TextView
    private lateinit var noOrderTextView: TextView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences // Add SharedPreferences
    private lateinit var finishOrderButton: Button
    private val ORDER_PREFS_NAME = "OrderPrefs" // Define a separate prefs name for order tracking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_order_activity)

        orderIdTextView = findViewById(R.id.orderIdTextView)
        noOrderTextView = findViewById(R.id.noOrderTextView)
        orderStatusTextView = findViewById(R.id.orderStatusTextView)
        finishOrderButton = findViewById(R.id.finishOrderButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        sharedPreferences = getSharedPreferences(ORDER_PREFS_NAME, MODE_PRIVATE)

        // Set up Bottom Navigation
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)

        // **Attempt to retrieve orderId from SharedPreferences FIRST**
        val savedOrderId = sharedPreferences.getString("orderId", null)
        val savedOrderStatus = sharedPreferences.getString("orderStatus", null)

        if (!savedOrderId.isNullOrBlank() && !savedOrderStatus.isNullOrBlank()) {
            // Order ID and Status found in SharedPreferences, display tracking info
            orderIdTextView.visibility = View.VISIBLE
            orderStatusTextView.visibility = View.VISIBLE
            noOrderTextView.visibility = View.GONE

            orderIdTextView.text = "Order ID: $savedOrderId"
            orderStatusTextView.text = "Status: $savedOrderStatus"

        } else {
            // No order ID in SharedPreferences or Intent, display "No Order Placed"
            orderIdTextView.visibility = View.GONE
            orderStatusTextView.visibility = View.GONE
            noOrderTextView.visibility = View.VISIBLE
        }



        // **Set up Finish Order Button Click Listener (AlertDialog remains the same)**
        finishOrderButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Finish Order?")
                .setMessage("Are you sure you want to finish this order? You will not be able to track it anymore.")
                .setPositiveButton("Finish") { dialog, which ->
                    // User confirmed "Finish", proceed to clear order data
                    clearOrderDataAndShowNoOrderMessage()
                    // **Update Finish Button State initially**
                    updateFinishButtonState() // Call it here in onCreate
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    // User cancelled, just dismiss the dialog
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
    private fun updateFinishButtonState() {
        val savedOrderId = sharedPreferences.getString("orderId", null)
        if (!savedOrderId.isNullOrBlank()) {
            // Order ID exists, enable and make button visible
            finishOrderButton.isEnabled = true
            finishOrderButton.alpha = 1.0f // Fully visible
        } else {
            // No order ID, disable and grey out button
            finishOrderButton.isEnabled = false
            finishOrderButton.alpha = 0.5f // Greyed out (adjust alpha as needed)
        }
    }
    private fun clearOrderDataAndShowNoOrderMessage() {
        // Clear order ID and status from SharedPreferences
        val editor = sharedPreferences.edit()
        editor.remove("orderId")
        editor.remove("orderStatus")
        editor.apply()

        // Update UI to show "No order placed" message
        orderIdTextView.visibility = View.GONE
        orderStatusTextView.visibility = View.GONE
        noOrderTextView.visibility = View.VISIBLE
    }
}