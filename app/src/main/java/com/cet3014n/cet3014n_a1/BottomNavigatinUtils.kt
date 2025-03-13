package com.cet3014n.cet3014n_a1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavigationUtils {

    fun setupBottomNavigation(activity: Activity, bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_category -> {
                    // Navigate to MenuActivity (or refresh if already there - depending on your app flow)
                    if (activity !is MenuActivity) {
                        val intent = Intent(activity, MenuActivity::class.java)
                        activity.startActivity(intent)
                    }
                    true
                }
                R.id.action_cart -> {
                    // Navigate to CartActivity (or refresh)
                    if (activity !is CartActivity) {
                        val intent = Intent(activity, CartActivity::class.java)
                        activity.startActivity(intent)
                    }
                    true
                }
                R.id.action_settings -> {
                    val popupMenu = PopupMenu(activity, bottomNavigationView.findViewById(R.id.action_settings))
                    popupMenu.menuInflater.inflate(R.menu.settings_menu, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.action_track_order -> {
                                // Launch TrackOrderActivity when "Track Order" is clicked
                                val intent = Intent(activity, TrackOrderActivity::class.java)
                                activity.startActivity(intent)
                                true
                            }
                            R.id.action_accounts -> {
                                val intent = Intent(activity, AccountsActivity::class.java)
                                activity.startActivity(intent)
                                true
                            }
                            R.id.action_about -> {
                                Toast.makeText(activity, "About Clicked", Toast.LENGTH_SHORT).show()
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                    true
                }
                else -> false
            }
        }
    }
}