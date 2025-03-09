package com.cet3014n.cet3014n_a1


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CartAdapter(context: Context, private val cartItems: List<CartItem>) :
    ArrayAdapter<CartItem>(context, R.layout.cart_item, cartItems) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.cart_item, parent, false)
        val cartItem = getItem(position)!!

        val itemNameTextView: TextView = view.findViewById(R.id.cartItemName)
        val itemDetailsTextView: TextView = view.findViewById(R.id.cartItemDetails)
        val itemPriceTextView: TextView = view.findViewById(R.id.cartItemPrice)

        itemNameTextView.text = cartItem.menuItem.name
        val details = "Milk: ${cartItem.milkOption}, Sugar: ${cartItem.sugarLevel}" // Corrected details string
        itemDetailsTextView.text = details // Corrected assignment
        itemPriceTextView.text = "$${String.format("%.2f", cartItem.menuItem.price)}" // Corrected assignment and price formatting

        return view
    }
}