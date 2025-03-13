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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
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
    private lateinit var cartAdapter: CartAdapter
    private lateinit var clearCartButton:Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var voucherList: List<Voucher>
    private val DELIVERY_DETAILS_PREFS_NAME = "DeliveryDetailsPrefs"
    private val ORDER_PREFS_NAME = "OrderPrefs"

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
        //cartItems = intent.getParcelableArrayListExtra<CartItem>("cartItems") ?: ArrayList()

        cartItems = CartManager.cartItems as ArrayList<CartItem>
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
        voucherList = loadVouchersFromJson()
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
    private fun loadVouchersFromJson(): List<Voucher> {
        return try {
            val inputStream = assets.open("voucher.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val voucherType = object : TypeToken<List<Voucher>>() {}.type
            gson.fromJson(jsonString, voucherType) ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
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

        // **ENABLE/DISABLE PAYMENT BUTTON BASED ON CART ITEMS**
        if (CartManager.getCartTotal() == 0.0) { // Check if cart is empty using CartManager
            paymentButton.isEnabled = false // Disable the payment button
            paymentButton.alpha = 0.5f // Optionally visually grey out the button (adjust alpha as needed)
        } else {
            paymentButton.isEnabled = true // Enable the payment button
            paymentButton.alpha = 1.0f // Reset alpha to fully visible
        }
    }

    private fun applyVoucher() {
        val voucherCode = voucherEditText.text.toString()
        if (voucherCode.isEmpty()) {
            Toast.makeText(this, "Please enter a voucher code.", Toast.LENGTH_SHORT).show()
            return // Exit if voucher code is empty
        }

        // **Find the voucher from voucherList**
        val validVoucher = voucherList.find { it.code == voucherCode && it.isActive }

        if (validVoucher != null) {
            var finalPrice = orderFinalPriceTextView.text.toString().removePrefix("Final Price: $").toDoubleOrNull() ?: 0.0
            finalPrice *= 0.90 // Apply 10% discount (assuming all vouchers are 10% for now - make dynamic later if needed)
            orderFinalPriceTextView.text = "Final Price: $${String.format("%.2f", finalPrice)}"
            Toast.makeText(this, "Voucher '${validVoucher.code}' applied: ${validVoucher.description}", Toast.LENGTH_LONG).show() // Show voucher description
        } else {
            Toast.makeText(this, "Invalid or inactive voucher code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPayment() {
        saveDeliveryDetails()

        val deliveryType = if (deliveryRadioButton.isChecked) "Delivery" else "Pickup"
        val finalAmount = orderFinalPriceTextView.text.toString()

        // Simulate generating an order ID
        val orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        val initialOrderStatus = "Preparing Order" // Initial order status
        sharedPreferences = getSharedPreferences(ORDER_PREFS_NAME, MODE_PRIVATE)
        // **Logging BEFORE saving to SharedPreferences**
        Log.d("CartActivity", "Saving orderId: $orderId to SharedPreferences")
        Log.d("CartActivity", "Saving orderStatus: $initialOrderStatus to SharedPreferences")
        // **Save orderId and orderStatus to SharedPreferences:**
        val editor = sharedPreferences.edit() // Use the same sharedPreferences instance as for delivery details
        editor.putString("orderId", orderId)
        editor.putString("orderStatus", initialOrderStatus)
        editor.apply() // Apply changes

        // **Logging AFTER saving to SharedPreferences**
        Log.d("CartActivity", "Order ID and Status SAVED to SharedPreferences")

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