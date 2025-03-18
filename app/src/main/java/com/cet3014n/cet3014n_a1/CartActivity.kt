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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    private lateinit var paymentMethodRadioGroup: RadioGroup // Added
    private lateinit var creditDebitRadioButton: RadioButton // Added
    private lateinit var mobileWalletRadioButton: RadioButton // Added
    private lateinit var otherPaymentRadioButton: RadioButton // Added
    private val DELIVERY_DETAILS_PREFS_NAME = "DeliveryDetailsPrefs"
    private val ORDER_PREFS_NAME = "OrderPrefs"
    private lateinit var emptyCartTextView: TextView // Added

    private fun setupPaymentButtonListener() {
        paymentButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Are you sure you want to proceed with the payment?")
                .setPositiveButton("Yes") { dialog, which ->
                    processPayment()
                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
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
        paymentMethodRadioGroup = findViewById(R.id.paymentMethodRadioGroup)
        creditDebitRadioButton = findViewById(R.id.creditDebitRadioButton)
        mobileWalletRadioButton = findViewById(R.id.mobileWalletRadioButton)
        otherPaymentRadioButton = findViewById(R.id.otherPaymentRadioButton)
        emptyCartTextView = findViewById(R.id.emptyCartTextView) // Initialize

        sharedPreferences = getSharedPreferences(DELIVERY_DETAILS_PREFS_NAME, MODE_PRIVATE)

        cartItems = CartManager.cartItems as ArrayList<CartItem>
        Log.d("CartActivity", "Cart Items received in CartActivity, size: ${cartItems.size}")
        for (item in cartItems) {
            Log.d("CartActivity", "  Item: ${item.menuItem.name}, Milk: ${item.milkOption}, Sugar: ${item.sugarLevel}")
        }

        cartAdapter = CartAdapter(this, cartItems)
        cartListView.adapter = cartAdapter

        // Set initial visibility of ListView based on cart items
        updateCartVisibility()

        pickupRadioButton.setOnCheckedChangeListener { _, isChecked ->
            deliveryDetailsLayout.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
        deliveryRadioButton.setOnCheckedChangeListener { _, isChecked ->
            deliveryDetailsLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        loadDeliveryDetails()
        updateOrderSummary()
        voucherList = loadVouchersFromJson()
        applyVoucherButton.setOnClickListener {
            applyVoucher()
        }
        setupPaymentButtonListener()

        clearCartButton.setOnClickListener {
            CartManager.clearCart()
            cartItems.clear()
            cartAdapter.notifyDataSetChanged()
            updateOrderSummary()
            updateCartVisibility() // Update visibility after clearing cart
            Toast.makeText(this, "Cart cleared!", Toast.LENGTH_SHORT).show()
        }
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)
    }
    private fun updateCartVisibility() {
        if (cartItems.isEmpty()) {
            cartListView.visibility = View.GONE
            emptyCartTextView.visibility = View.VISIBLE
        } else {
            cartListView.visibility = View.VISIBLE
            emptyCartTextView.visibility = View.GONE
        }
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
            deliveryRadioButton.isChecked = true
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
        val tax = totalPrice * 0.06
        val finalPrice = totalPrice + tax

        orderTotalPriceTextView.text = "Total Price: RM ${String.format("%.2f", totalPrice)}"
        orderTaxTextView.text = "Tax (6%): RM ${String.format("%.2f", tax)}"
        orderFinalPriceTextView.text = "Final Price: RM ${String.format("%.2f", finalPrice)}"

        if (CartManager.getCartTotal() == 0.0) {
            paymentButton.isEnabled = false
            paymentButton.alpha = 0.5f
        } else {
            paymentButton.isEnabled = true
            paymentButton.alpha = 1.0f
        }
        // Also update visibility here in case items are removed individually (if you implement that)
        updateCartVisibility()
    }

    private fun applyVoucher() {
        val voucherCode = voucherEditText.text.toString()
        if (voucherCode.isEmpty()) {
            Toast.makeText(this, "Please enter a voucher code.", Toast.LENGTH_SHORT).show()
            return
        }

        val validVoucher = voucherList.find { it.code == voucherCode && it.isActive }

        if (validVoucher != null) {
            var finalPrice = orderFinalPriceTextView.text.toString().removePrefix("Final Price: RM ").toDoubleOrNull() ?: 0.0
            finalPrice *= 0.90
            orderFinalPriceTextView.text = "Final Price: RM ${String.format("%.2f", finalPrice)}"
            Toast.makeText(this, "Voucher '${validVoucher.code}' applied: ${validVoucher.description}", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Invalid or inactive voucher code.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processPayment() {
        saveDeliveryDetails()

        val deliveryType = if (deliveryRadioButton.isChecked) "Delivery" else "Pickup"
        val finalAmount = orderFinalPriceTextView.text.toString()

        val selectedPaymentMethodId = paymentMethodRadioGroup.checkedRadioButtonId
        val paymentMethod = when (selectedPaymentMethodId) {
            R.id.creditDebitRadioButton -> "Credit/Debit Card"
            R.id.mobileWalletRadioButton -> "Mobile Wallet"
            R.id.otherPaymentRadioButton -> "Other Payment Method"
            else -> "Not Selected"
        }
        Log.d("CartActivity", "Selected Payment Method: $paymentMethod")
        if (paymentMethod == "Not Selected") {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        val initialOrderStatus = "Preparing Order"
        sharedPreferences = getSharedPreferences(ORDER_PREFS_NAME, MODE_PRIVATE)
        Log.d("CartActivity", "Saving orderId: $orderId to SharedPreferences")
        Log.d("CartActivity", "Saving orderStatus: $initialOrderStatus to SharedPreferences")

        val editor = sharedPreferences.edit()
        editor.putString("orderId", orderId)
        editor.putString("orderStatus", initialOrderStatus)
        editor.putString("paymentMethod", paymentMethod)
        editor.putString("finalAmount", finalAmount)

        val gson = Gson()
        val cartItemsJson = gson.toJson(CartManager.cartItems)
        editor.putString("orderItems", cartItemsJson)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val paymentTime = sdf.format(Date())
        editor.putString("paymentTime", paymentTime)

        editor.apply()
        Log.d("CartActivity", "Order ID and Status SAVED to SharedPreferences")

        val confirmationMessage = "Payment of $finalAmount via $paymentMethod processed for $deliveryType order. Order ID: $orderId. " +
                if (deliveryType == "Delivery") "Delivery to: ${deliveryAddressEditText.text}" else "Preparing, Ready for pickup."
        Toast.makeText(this, confirmationMessage, Toast.LENGTH_LONG).show()

        val intent = Intent(this, TrackOrderActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)

        CartManager.clearCart()
        cartAdapter.notifyDataSetChanged()
        updateCartVisibility() // Update visibility after payment (cart is cleared)
        clearCartButton.performClick()
        updateOrderSummary()
    }
}