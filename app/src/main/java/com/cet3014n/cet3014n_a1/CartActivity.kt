package com.cet3014n.cet3014n_a1

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CartActivity : AppCompatActivity() {

    private lateinit var pickupRadioButton: RadioButton
    private lateinit var deliveryRadioButton: RadioButton
    private lateinit var deliveryDetailsLayout: LinearLayout
    private lateinit var deliveryAddressEditText: EditText
    private lateinit var deliveryPhoneEditText: EditText
    private lateinit var saveDetailsCheckBox: CheckBox
    private lateinit var cartListView: ListView
    private lateinit var orderTotalPriceTextView: TextView
    private lateinit var orderTaxTextView: TextView
    private lateinit var voucherEditText: EditText
    private lateinit var applyVoucherButton: Button
    private lateinit var orderFinalPriceTextView: TextView
    private lateinit var paymentButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var cartItems: ArrayList<CartItem> // To hold cart items passed from MenuActivity
    private lateinit var cartAdapter: CartAdapter // You'll need to create CartAdapter

    private lateinit var sharedPreferences: SharedPreferences
    private val DELIVERY_DETAILS_PREFS_NAME = "DeliveryDetailsPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_activity)

        pickupRadioButton = findViewById(R.id.pickupRadioButton)
        deliveryRadioButton = findViewById(R.id.deliveryRadioButton)
        deliveryDetailsLayout = findViewById(R.id.deliveryDetailsLayout)
        deliveryAddressEditText = findViewById(R.id.deliveryAddressEditText)
        deliveryPhoneEditText = findViewById(R.id.deliveryPhoneEditText)
        saveDetailsCheckBox = findViewById(R.id.saveDetailsCheckBox)
        cartListView = findViewById(R.id.cartListView)
        orderTotalPriceTextView = findViewById(R.id.orderTotalPriceTextView)
        orderTaxTextView = findViewById(R.id.orderTaxTextView)
        voucherEditText = findViewById(R.id.voucherEditText)
        applyVoucherButton = findViewById(R.id.applyVoucherButton)
        orderFinalPriceTextView = findViewById(R.id.orderFinalPriceTextView)
        paymentButton = findViewById(R.id.paymentButton)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        sharedPreferences = getSharedPreferences(DELIVERY_DETAILS_PREFS_NAME, MODE_PRIVATE)

        // Get cart items from intent
        cartItems = intent.getParcelableArrayListExtra<CartItem>("cartItems") ?: ArrayList()

        // Setup Cart ListView Adapter (you'll need to create CartAdapter)
        cartAdapter = CartAdapter(this, cartItems) // Assuming you'll create CartAdapter
        cartListView.adapter = cartAdapter

        // Set up Radio Group for Pickup/Delivery
        pickupRadioButton.setOnCheckedChangeListener { _, isChecked ->
            deliveryDetailsLayout.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
        deliveryRadioButton.setOnCheckedChangeListener { _, isChecked ->
            deliveryDetailsLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        // Load saved delivery details
        loadDeliveryDetails()

        // Calculate and display order summary
        updateOrderSummary()

        applyVoucherButton.setOnClickListener {
            applyVoucher() // Implement voucher logic
        }

        paymentButton.setOnClickListener {
            processPayment() // Implement payment simulation
        }

        // Bottom Navigation setup (same as in MenuActivity, but handle Cart action here)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_category -> {
                    // Go back to MenuActivity
                    finish() // Just go back to MenuActivity
                    true
                }
                R.id.action_cart -> {
                    // Already in CartActivity, do nothing or refresh if needed
                    true
                }
                R.id.action_settings -> {
                    // Handle settings action - navigate to SettingsActivity if you have one
                    Toast.makeText(this, "Settings Clicked (from Cart)", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        // Set "Cart" item as selected initially in BottomNavigationView (if you want Cart to be highlighted when CartActivity starts)
        bottomNavigationView.selectedItemId = R.id.action_cart
    }

    private fun loadDeliveryDetails() {
        if (sharedPreferences.contains("deliveryAddress")) {
            deliveryRadioButton.isChecked = true // Assume delivery if details are saved
            deliveryDetailsLayout.visibility = View.VISIBLE
            deliveryAddressEditText.setText(sharedPreferences.getString("deliveryAddress", ""))
            deliveryPhoneEditText.setText(sharedPreferences.getString("deliveryPhone", ""))
            saveDetailsCheckBox.isChecked = true
        }
    }

    private fun saveDeliveryDetails() {
        val editor = sharedPreferences.edit()
        if (saveDetailsCheckBox.isChecked) {
            editor.putString("deliveryAddress", deliveryAddressEditText.text.toString())
            editor.putString("deliveryPhone", deliveryPhoneEditText.text.toString())
        } else {
            editor.remove("deliveryAddress")
            editor.remove("deliveryPhone")
        }
        editor.apply()
    }

    private fun updateOrderSummary() {
        var totalPrice = 0.0
        for (cartItem in cartItems) {
            totalPrice += cartItem.menuItem.price
        }
        val tax = totalPrice * 0.05 // 5% tax simulation
        val finalPrice = totalPrice + tax

        orderTotalPriceTextView.text = "Total Price: $${String.format("%.2f", totalPrice)}"
        orderTaxTextView.text = "Tax (5%): $${String.format("%.2f", tax)}"
        orderFinalPriceTextView.text = "Final Price: $${String.format("%.2f", finalPrice)}"
    }

    private fun applyVoucher() {
        val voucherCode = voucherEditText.text.toString()
        // In a real app, you'd validate voucher code against a backend
        if (voucherCode == "DISCOUNT10") {
            var finalPrice = orderFinalPriceTextView.text.toString().removePrefix("Final Price: $").toDoubleOrNull() ?: 0.0
            finalPrice *= 0.9 // 10% discount
            orderFinalPriceTextView.text = "Final Price: $${String.format("%.2f", finalPrice)}"
            Toast.makeText(this, "Voucher applied!", Toast.LENGTH_SHORT).show()
        } else if (voucherCode.isNotEmpty()) {
            Toast.makeText(this, "Invalid voucher code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPayment() {
        saveDeliveryDetails() // Save details before payment

        val deliveryType = if (deliveryRadioButton.isChecked) "Delivery" else "Pickup"
        val finalAmount = orderFinalPriceTextView.text.toString()

        // In a real app, you would integrate with a payment gateway here
        // For simulation, just show a confirmation message
        val confirmationMessage = "Payment of $finalAmount processed for $deliveryType order. " +
                if (deliveryType == "Delivery") "Delivery to: ${deliveryAddressEditText.text}" else "Ready for pickup."
        Toast.makeText(this, confirmationMessage, Toast.LENGTH_LONG).show()

        // Optionally clear cart after successful "payment" in simulation
        cartItems.clear()
        cartAdapter.notifyDataSetChanged()
        updateOrderSummary()
    }
}