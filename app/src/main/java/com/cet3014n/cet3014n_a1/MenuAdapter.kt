package com.cet3014n.cet3014n_a1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MenuAdapter(private val context: Context, private val items: List<MenuItem>) :
    ArrayAdapter<MenuItem>(context, R.layout.menu_item, items) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val activityContext = context as Activity // Cast context to Activity to use startActivityForResult

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.menu_item, parent, false)
        val item = items[position]
        val menuItem = getItem(position)!!

        val itemImage = view.findViewById<ImageView>(R.id.itemImage)
        val itemName = view.findViewById<TextView>(R.id.itemName)
        val itemDescription = view.findViewById<TextView>(R.id.itemDescription)
        val itemPrice = view.findViewById<TextView>(R.id.itemPrice)
        val addToCartButton: Button = view.findViewById(R.id.addToCartButton) // Find button

        itemName.text = item.name
        itemDescription.text = item.description
        itemPrice.text = "$${item.price}"

        // Load image from drawable
        val imageResId = context.resources.getIdentifier(
            item.image.removeSuffix(".jpg"), "drawable", context.packageName
        )
        itemImage.setImageResource(if (imageResId != 0) imageResId else R.drawable.placeholder)

        addToCartButton.setOnClickListener {
            // Start CustomizeItemActivity when "Add to Cart" is clicked
            val intent = Intent(context, CustomizeItemActivity::class.java)
            intent.putExtra("menuItem", menuItem) // Pass the MenuItem to CustomizeItemActivity
            activityContext.startActivityForResult(intent, MenuActivity.REQUEST_CUSTOMIZE_ITEM) // Use startActivityForResult
            // Toast for feedback
            Toast.makeText(context, "Customize ${menuItem.name}", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}