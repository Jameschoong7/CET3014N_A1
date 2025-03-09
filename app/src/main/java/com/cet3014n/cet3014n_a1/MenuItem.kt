package com.cet3014n.cet3014n_a1

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MenuItem(
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val image: String,
    val dietary: List<String>
): Parcelable