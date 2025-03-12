package com.cet3014n.cet3014n_a1

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.UUID

class TrackOrderActivity : AppCompatActivity() {

    private lateinit var orderIdTextView: TextView
    private lateinit var orderStatusTextView: TextView
    private lateinit var noOrderTextView: TextView
    private lateinit var bottomNavigationView: BottomNavigationView // Add BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_order_activity)

        orderIdTextView = findViewById(R.id.orderIdTextView)
        noOrderTextView = findViewById(R.id.noOrderTextView)
        orderStatusTextView = findViewById(R.id.orderStatusTextView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView) // Initialize BottomNavigationView

        // Set up Bottom Navigation using the utility function
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)

        // **Retrieve orderId from Intent extras**
        val orderId = intent.getStringExtra("orderId")

        if (!orderId.isNullOrBlank()) {
            // Order ID exists, display tracking info
            orderIdTextView.visibility = View.VISIBLE // Make orderIdTextView visible
            orderStatusTextView.visibility = View.VISIBLE // Make orderStatusTextView visible
            noOrderTextView.visibility = View.GONE // Hide noOrderTextView

            orderIdTextView.text = "Order ID: $orderId"

            // Simulate order statuses
            val statuses = listOf(
                "Preparing Order",
                "Ready for Pickup",
                "Out for Delivery", // If you were to implement delivery
                "Order Completed"
            )
            // For simplicity, just display the first status for simulation
            orderStatusTextView.text = "Status: ${statuses.first()}"

        } else {
            // No order ID received, display "No Order Placed" message
            orderIdTextView.visibility = View.GONE // Hide orderIdTextView
            orderStatusTextView.visibility = View.GONE // Hide orderStatusTextView
            noOrderTextView.visibility = View.VISIBLE // Make noOrderTextView visible
        }
    }
}
