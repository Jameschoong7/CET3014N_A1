package com.cet3014n.cet3014n_a1

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReceiptActivity : AppCompatActivity() {

    private lateinit var orderIdTextView: TextView
    private lateinit var paymentMethodTextView: TextView
    private lateinit var finalAmountTextView: TextView
    private lateinit var paymentTimeTextView: TextView
    private lateinit var orderItemsTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bottomNavigationView: BottomNavigationView
    private val ORDER_PREFS_NAME = "OrderPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.receipt_activity)

        orderIdTextView = findViewById(R.id.receiptOrderIdTextView)
        paymentMethodTextView = findViewById(R.id.receiptPaymentMethodTextView)
        finalAmountTextView = findViewById(R.id.receiptFinalAmountTextView)
        paymentTimeTextView = findViewById(R.id.receiptPaymentTimeTextView)
        orderItemsTextView = findViewById(R.id.receiptOrderItemsTextView)
        sharedPreferences = getSharedPreferences(ORDER_PREFS_NAME, MODE_PRIVATE)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val orderId = intent.getStringExtra("orderId")
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)
        if (!orderId.isNullOrBlank()) {
            val paymentMethod = sharedPreferences.getString("paymentMethod", "")
            val finalAmount = sharedPreferences.getString("finalAmount", "")
            val paymentTime = sharedPreferences.getString("paymentTime", "")
            val orderItemsJson = sharedPreferences.getString("orderItems", "")

            orderIdTextView.text = "Order ID: $orderId"
            paymentMethodTextView.text = "Payment Method: $paymentMethod"
            finalAmountTextView.text = "Total Paid: $finalAmount"
            paymentTimeTextView.text = "Payment Time: $paymentTime"

            // Deserialize order items from JSON
            val gson = Gson()
            val cartListType = object : TypeToken<List<CartItem>>() {}.type
            val orderItemsList: List<CartItem> = gson.fromJson(orderItemsJson, cartListType) ?: emptyList()

            // Format the order items for display
            val itemsText = StringBuilder()
            for (item in orderItemsList) {
                itemsText.append("${item.menuItem.name} (Milk: ${item.milkOption}, Sugar: ${item.sugarLevel})\n")
            }
            orderItemsTextView.text = "Ordered Items:\n${itemsText.toString()}"

        } else {
            // Handle case where order ID is not passed
            orderIdTextView.text = "Error: Order ID not found."
        }
    }
}