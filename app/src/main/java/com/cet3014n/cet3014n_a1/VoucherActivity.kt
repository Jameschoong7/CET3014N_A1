package com.cet3014n.cet3014n_a1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class VouchersActivity : AppCompatActivity() {

    private lateinit var vouchersTitleTextView: TextView
    private lateinit var vouchersRecyclerView: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var voucherAdapter: VoucherAdapter
    private lateinit var voucherList: List<Voucher>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vouchers_activity)

        vouchersTitleTextView = findViewById(R.id.vouchersTitleTextView)
        vouchersRecyclerView = findViewById(R.id.vouchersRecyclerView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set up Bottom Navigation
        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)

        // Load Voucher Data from JSON
        voucherList = loadVouchersFromJson()

        // Set up RecyclerView and Adapter
        voucherAdapter = VoucherAdapter(voucherList)
        vouchersRecyclerView.adapter = voucherAdapter
    }

    private fun loadVouchersFromJson(): List<Voucher> {
        return try {
            val inputStream = assets.open("voucher.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val gson = Gson()
            val voucherType = object : TypeToken<List<Voucher>>() {}.type
            gson.fromJson(jsonString, voucherType) ?: emptyList() // Handle null case, return emptyList if parsing fails
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList() // Return empty list in case of IO error
        }
    }
}