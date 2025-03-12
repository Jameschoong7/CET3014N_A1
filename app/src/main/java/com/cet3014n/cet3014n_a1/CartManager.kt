package com.cet3014n.cet3014n_a1

import android.content.Context
object CartManager {
    private val _cartItems = mutableListOf<CartItem>() // Private backing list
    val cartItems: List<CartItem> = _cartItems // Public read-only view

    fun addToCart(cartItem: CartItem) {
        _cartItems.add(cartItem)
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getCartTotal(): Double {
        var total = 0.0
        for (item in _cartItems) {
            total += item.menuItem.price
        }
        return total
    }
}