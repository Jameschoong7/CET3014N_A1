package com.cet3014n.cet3014n_a1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class MenuActivity : AppCompatActivity(), CategoryAdapter.OnCategoryClickListener {

    private lateinit var menuItems: List<MenuItem>
    private lateinit var filteredMenuItems: List<MenuItem> // Use filteredMenuItems for clarity
    private lateinit var categories: List<Category>
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter // Category Adapter
    private lateinit var menuAdapter: MenuRecyclerAdapter // Menu Item Adapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private val cartItemsList = mutableListOf<CartItem>()

    companion object {
        const val REQUEST_CUSTOMIZE_ITEM = 1
        private const val MENU_JSON_FILE = "menu.json"
        private const val TAG = "MenuActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView) // Initialize category RecyclerView
        menuRecyclerView = findViewById(R.id.menuRecyclerView) // Initialize menu RecyclerView
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        menuItems = loadMenuItems()
        categories = extractCategories(menuItems) // Extract categories from menu items
        filteredMenuItems = menuItems // Initially show all menu items

        setupCategoryRecyclerView() // Setup category RecyclerView
        setupMenuRecyclerView() // Setup menu RecyclerView (using filteredMenuItems)

        BottomNavigationUtils.setupBottomNavigation(this, bottomNavigationView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //onActivityResult - CartItem process is the same as before
        if (requestCode == REQUEST_CUSTOMIZE_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                val cartItem = data?.getParcelableExtra<CartItem>("cartItem")
                if (cartItem != null) {
                    cartItemsList.add(cartItem)
                    Toast.makeText(
                        this,
                        "${cartItem.menuItem.name} added to cart!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        TAG,
                        "Added to cart: ${cartItem.menuItem.name}, Cart size: ${cartItemsList.size}"
                    )
                } else {
                    Log.e(TAG, "Error: CartItem data is null in onActivityResult")
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Customize item cancelled")
            }
        }
    }

    private fun setupCategoryRecyclerView() {
        categoryAdapter = CategoryAdapter(categories, this) // Initialize CategoryAdapter with categories and listener
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) // Vertical LinearLayoutManager for categories
        categoryRecyclerView.adapter = categoryAdapter // Set CategoryAdapter to category RecyclerView
    }

    private fun setupMenuRecyclerView() {
        menuAdapter = MenuRecyclerAdapter(this, filteredMenuItems) // Initialize MenuRecyclerAdapter with filtered items
        menuRecyclerView.layoutManager =
            GridLayoutManager(this, 2) // GridLayoutManager with 2 columns for menu items
        menuRecyclerView.adapter = menuAdapter // Set MenuRecyclerAdapter to menu RecyclerView
    }

    private fun loadMenuItems(): List<MenuItem> {
        return try {
            val inputStream = assets.open(MENU_JSON_FILE)
            val reader = InputStreamReader(inputStream)
            val menuType = object : TypeToken<List<MenuItem>>() {}.type
            Gson().fromJson(reader, menuType) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading menu items: ${e.message}", e)
            emptyList()
        }
    }

    private fun extractCategories(menuItems: List<MenuItem>): List<Category> {
        val categorySet = mutableSetOf<String>() // Use a Set to store unique categories
        menuItems.forEach { menuItem ->
            menuItem.category?.let { categorySet.add(it) } // Add category if not null
        }
        val categoryList = categorySet.toList()
        return listOf(Category("All")) + categoryList.map { Category(it) } // Convert Set to List of Category objects, Add "All Categories"
    }


    fun getImageResourceId(imageName: String): Int {
        Log.d("ImageLoading", "Attempting to load image: $imageName")
        val imageNameWithoutExtension = imageName.substringBeforeLast(".")
        Log.d("ImageLoading", "Image name without extension: $imageNameWithoutExtension")
        val resId = resources.getIdentifier(imageNameWithoutExtension, "drawable", packageName)
        Log.d("ImageLoading", "Resource ID for $imageNameWithoutExtension: $resId")
        return if (resId == 0) {
            Log.d("ImageLoading", "Resource NOT FOUND for $imageNameWithoutExtension. Returning placeholder: R.drawable.placeholder")
            R.drawable.placeholder
        } else {
            Log.d("ImageLoading", "Resource FOUND for $imageNameWithoutExtension. Returning resId: $resId")
            resId
        }
    }

    override fun onCategoryClick(category: Category) {
        filterMenuItemsByCategory(category.name) // Filter menu items when category is clicked
    }

    private fun filterMenuItemsByCategory(selectedCategoryName: String) {
        filteredMenuItems = if (selectedCategoryName == "All Categories") {
            menuItems // Show all items if "All Categories" is selected
        } else {
            menuItems.filter { it.category == selectedCategoryName } // Filter by selected category
        }
        setupMenuRecyclerView() // Re-setup menu RecyclerView to update the list
    }
}