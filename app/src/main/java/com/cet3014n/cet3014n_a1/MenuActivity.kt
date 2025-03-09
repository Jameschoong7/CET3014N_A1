package com.cet3014n.cet3014n_a1

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import android.view.View
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MenuActivity : AppCompatActivity() {

    private lateinit var menuItems: List<MenuItem>
    private lateinit var filteredItems: List<MenuItem>
    private lateinit var menuListView: ListView
    private lateinit var bottomNavigationView: BottomNavigationView
    private val cartItemsList = mutableListOf<CartItem>() // **Shopping Cart List**

    companion object {
        const val REQUEST_CUSTOMIZE_ITEM = 1 // Request code for startActivityForResult
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        menuItems = loadMenuItems()
        filteredItems = menuItems

        menuListView = findViewById(R.id.menuListView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Set up BottomNavigationView listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_category -> {
                    showCategoryPopupMenu(findViewById(R.id.action_category)) // Show PopupMenu
                    true
                }

                R.id.action_cart -> {
                    // Navigate to CartActivity when "Cart" is clicked
                    val intent = Intent(this, CartActivity::class.java)
                    intent.putParcelableArrayListExtra(
                        "cartItems",
                        ArrayList(cartItemsList)
                    ) // Pass current cart items to CartActivity
                    startActivity(intent)
                    true
                }

                R.id.action_settings -> {
                    // Handle "Settings" menu item click (as before)
                    // ... Your settings action ...
                    true
                }

                else -> false
            }
        }

        // Initial display - show all menu items
        updateMenuListView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CUSTOMIZE_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                val cartItem = data?.getParcelableExtra<CartItem>("cartItem")
                if (cartItem != null) {
                    cartItemsList.add(cartItem) // **Add CartItem to the cart list**
                    Toast.makeText(
                        this,
                        "${cartItem.menuItem.name} added to cart!",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Optionally update cart badge or UI here to reflect cart changes
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User canceled customization, do nothing or handle as needed
            }
        }
    }

    private fun showCategoryPopupMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.menuInflater.inflate(
            R.menu.popup_menu_category_filter,
            popupMenu.menu
        ) // Inflate popup menu

        popupMenu.setOnMenuItemClickListener { menuItem ->
            android.util.Log.d("MenuActivity", "PopupMenu Item Clicked: ${menuItem.itemId}")
            when (menuItem.itemId) {
                R.id.category_all -> {
                    filterMenuItems("All")
                    true
                }

                R.id.category_coffee -> {
                    filterMenuItems("Coffee")
                    true
                }

                R.id.category_tea -> {
                    filterMenuItems("Tea")
                    true
                }

                R.id.category_pastries -> {
                    filterMenuItems("Pastry")
                    true
                }

                else -> false
            }
        }
        popupMenu.show() // Show the popup menu
    }


    private fun filterMenuItems(selectedCategory: String) {
        android.util.Log.d(
            "MenuActivity",
            "Filtering for category: $selectedCategory"
        ) // Log selected category
        filteredItems = if (selectedCategory == "All") {
            menuItems
        } else {
            menuItems.filter { item ->
                item.category == selectedCategory.lowercase()
            }
        }
        android.util.Log.d(
            "MenuActivity",
            "Filtered items count: ${filteredItems.size}"
        ) // Log filtered items count
        updateMenuListView()
    }


    private fun updateMenuListView() {
        val adapter = MenuAdapter(this, filteredItems)
        menuListView.adapter = adapter
    }

    private fun loadMenuItems(): List<MenuItem> {
        try {
            val inputStream = assets.open("menu.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            android.util.Log.d("MenuActivity", "JSON String loaded: $jsonString")
            val gson = Gson()
            val menuType = object : TypeToken<List<MenuItem>>() {}.type
            val loadedMenuItems: List<com.cet3014n.cet3014n_a1.MenuItem> =
                gson.fromJson(jsonString, menuType)
            android.util.Log.d("MenuActivity", "Menu items loaded: ${loadedMenuItems.size} items")
            return gson.fromJson(jsonString, menuType)
        } catch (e: Exception) {
            android.util.Log.e("MenuActivity", "Error loading menu items:", e)
            e.printStackTrace()
            return emptyList()
        }
    }
}


