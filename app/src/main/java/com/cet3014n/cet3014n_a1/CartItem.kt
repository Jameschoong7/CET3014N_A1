package com.cet3014n.cet3014n_a1


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val menuItem: MenuItem,
    val milkOption: String,
    val sugarLevel: String
    // Add other customizations as needed
) : Parcelable