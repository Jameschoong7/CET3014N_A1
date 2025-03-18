package com.cet3014n.cet3014n_a1

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MenuRecyclerAdapter(
    private val context: Context,
    private val menuItems: List<MenuItem>
) : RecyclerView.Adapter<MenuRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.itemImage)
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDescription: TextView = itemView.findViewById(R.id.itemDescription)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false) // Inflate 'menu_item.xml'
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val menuItem = menuItems[position]

        holder.itemName.text = menuItem.name
        holder.itemDescription.text = menuItem.description
        holder.itemPrice.text = "RM ${String.format("%.2f", menuItem.price)}"

        val imageName = menuItem.image // Get image name from menu item
        Log.d("ImageLoadingAdapter", "Binding image for item: ${menuItem.name}, imageName: $imageName") // Log item name and imageName
        // **ADD THIS LOG LINE:**
        Log.d("ImageLoadingAdapter", "Value of R.drawable.placeholder: ${R.drawable.placeholder}")
        // Load image from drawable
        //val imageResId = context.resources.getIdentifier(
          //  menuItem.image.removeSuffix(".jpg"), "drawable", context.packageName
        //)

        val imageResId = (context as? MenuActivity)?.getImageResourceId(imageName) ?: R.drawable.placeholder
        Log.d("ImageLoadingAdapter", "Resource ID to set for ${menuItem.name}: $imageResId")

        Log.d("ImageLoadingAdapter", "Resource ID to set for ${menuItem.name}: $imageResId") // Log resId before setting

        holder.itemImage.setImageResource(imageResId)
        Log.d("ImageLoadingAdapter", "setImageResource called for ${menuItem.name}") // Log after setImageResource

        holder.addToCartButton.setOnClickListener {
            // Start CustomizeItemActivity when "Add to Cart" is clicked
            val intent = Intent(context, CustomizeItemActivity::class.java)
            intent.putExtra("menuItem", menuItem) // Pass the MenuItem to CustomizeItemActivity
            (context as? MenuActivity)?.let { activityContext -> // Safe cast to MenuActivity
                activityContext.startActivityForResult(intent, MenuActivity.REQUEST_CUSTOMIZE_ITEM) // Use startActivityForResult if context is MenuActivity
            } ?: run { // If context is not MenuActivity (e.g., for testing or other scenarios)
                context.startActivity(intent) // Fallback to regular startActivity if needed, or handle differently
            }
            // Toast for feedback
            Toast.makeText(context, "Customize ${menuItem.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount() = menuItems.size
}