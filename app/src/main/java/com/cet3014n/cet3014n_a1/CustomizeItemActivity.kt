package com.cet3014n.cet3014n_a1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class CustomizeItemActivity : AppCompatActivity() {

    private lateinit var item: MenuItem // To hold the MenuItem being customized
    private lateinit var itemImage: ImageView
    private lateinit var itemNameText: TextView
    private lateinit var milkSpinner: Spinner
    private lateinit var sugarSpinner: Spinner
    private lateinit var addToCartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customize_item_activity)

        item = intent.getParcelableExtra<MenuItem>("menuItem") ?: MenuItem("", "", "", 0.0, "", emptyList()) // Get MenuItem from intent, provide default if null

        itemImage = findViewById(R.id.itemImageCustomize)
        itemNameText = findViewById(R.id.itemNameCustomize)
        milkSpinner = findViewById(R.id.milkSpinner)
        sugarSpinner = findViewById(R.id.sugarSpinner)
        addToCartButton = findViewById(R.id.customizeAddToCartButton)

        // Load item details
        itemNameText.text = item.name
        // Load image (using your image loading method - placeholder for now)
         itemImage.setImageResource(getImageResourceId(item.image)) // You'll need to implement getImageResourceId

        // Populate customization Spinners (example options)
        val milkOptions = arrayOf("Whole Milk", "Skim Milk", "Almond Milk", "Soy Milk")
        val sugarOptions = arrayOf("No Sugar", "Less Sugar", "Regular Sugar", "Extra Sugar")

        milkSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, milkOptions)
        sugarSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sugarOptions)

        addToCartButton.setOnClickListener {
            val selectedMilk = milkSpinner.selectedItem.toString()
            val selectedSugar = sugarSpinner.selectedItem.toString()

            // Create a CartItem (you'll need to define CartItem data class)
            val cartItem = CartItem(item, selectedMilk, selectedSugar)

            // Add to Cart (you'll need to manage the cart - e.g., in MenuActivity or a singleton)
            // Example - assuming cart is in MenuActivity:
            if (callingActivity != null) { // Check if started from another activity (MenuActivity in this case)
                val resultIntent = Intent()
                resultIntent.putExtra("cartItem", cartItem) // Pass CartItem back to MenuActivity
                setResult(RESULT_OK, resultIntent)
                finish() // Go back to MenuActivity
            } else {
                // Handle case if CustomizeItemActivity is launched directly (not from MenuActivity) - optional
                Toast.makeText(this, "Item added to cart (but going nowhere in this standalone launch)", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getImageResourceId(imageName: String?): Int {
        Log.d("ImageLoading", "Attempting to load image: $imageName")
        val imageNameWithoutExtension = imageName?.substringBeforeLast(".")
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
}


