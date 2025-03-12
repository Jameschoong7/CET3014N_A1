package com.cet3014n.cet3014n_a1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.UUID

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
    private lateinit var clearCartButton:Button
    private lateinit var sharedPreferences: SharedPreferences
    private val DELIVERY_DETAILS_PREFS_NAME = "DeliveryDetailsPrefs"


    private fun setupPaymentButtonListener() { // Extract button listener into a function for clarity
        paymentButton.setOnClickListener {
            // Show confirmation AlertDialog before processing payment
            AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Are you sure you want to proceed with the payment?")
                .setPositiveButton("Yes") { dialog, which ->
                    // User confirmed, proceed with payment
                    processPayment() // Call existing processPayment function
                    // Refresh CartActivity UI after payment (already handled in processPayment, but double-check)
                }
                .setNegativeButton("No") { dialog, which ->
                    // User cancelled, do nothing, just dismiss the dialog
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert) // Optional: Add an alert icon
                .show()
        }
    }
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

        clearCartButton=findViewById(R.id.clearCartButton)

        sharedPreferences = getSharedPreferences(DELIVERY_DETAILS_PREFS_NAME, MODE_PRIVATE)

        // Get cart items from intent
        cartItems = intent.getParcelableArrayListExtra<CartItem>("cartItems") ?: ArrayList()

        Log.d("CartActivity", "Cart Items received in CartActivity, size: ${cartItems.size}")
        for (item in cartItems) {
            Log.d("CartActivity", "  Item: ${item.menuItem.name}, Milk: ${item.milkOption}, Sugar: ${item.sugarLevel}")
        }

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
        setupPaymentButtonListener()

        clearCartButton.setOnClickListener {
            // **CLEAR CART LOGIC:**
            CartManager.clearCart() // Clear cart using Singleton CartManager
            cartItems.clear() // Clear the local copy in CartActivity
            CartManager.clearCart()
            cartAdapter.notifyDataSetChanged() // Update the ListView
            updateOrderSummary() // Recalculate order summary
            Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show() // Optional confirmation Toast
        }
        // Bottom Navigation setup (same as in MenuActivity, but handle Cart action here)
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)
        // Set "Cart" item as selected initially in BottomNavigationView (if you want Cart to be highlighted when CartActivity starts)
       // bottomNavigationView.selectedItemId = R.id.action_cart
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
        val totalPrice =CartManager.getCartTotal()
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
        saveDeliveryDetails()

        val deliveryType = if (deliveryRadioButton.isChecked) "Delivery" else "Pickup"
        val finalAmount = orderFinalPriceTextView.text.toString()

        // Simulate generating an order ID
        val orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase()

        // In a real app, integrate with a payment gateway here
        // For simulation, just show a confirmation message
        val confirmationMessage = "Payment of $finalAmount processed for $deliveryType order. Order ID: $orderId. " +
                if (deliveryType == "Delivery") "Delivery to: ${deliveryAddressEditText.text}" else "Preparing, Ready for pickup."
        Toast.makeText(this, confirmationMessage, Toast.LENGTH_LONG).show()

        // **Uncomment and modify to pass orderId to TrackOrderActivity:**
        val intent = Intent(this, TrackOrderActivity::class.java)
        intent.putExtra("orderId", orderId) // Put orderId as extra with key "orderId"
        startActivity(intent) // Launch TrackOrderActivity immediately after payment simulation

        // Optionally clear cart after successful "payment" in simulation
        CartManager.clearCart()
        cartAdapter.notifyDataSetChanged()
        clearCartButton.performClick()
        updateOrderSummary()
    }
}