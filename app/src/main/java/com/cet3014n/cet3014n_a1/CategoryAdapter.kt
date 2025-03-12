package com.cet3014n.cet3014n_a1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: List<Category>, private val itemClickListener: OnCategoryClickListener) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView as TextView // Assuming category_item.xml is just a TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false) // Inflate category_item.xml
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryNameTextView.text = category.name

        holder.itemView.setOnClickListener { // Set click listener for each category item
            itemClickListener.onCategoryClick(category) // Notify listener when clicked
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}