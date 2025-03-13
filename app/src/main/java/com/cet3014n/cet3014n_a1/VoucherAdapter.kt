package com.cet3014n.cet3014n_a1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VoucherAdapter(private val vouchers: List<Voucher>) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    class VoucherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val voucherCodeTextView: TextView = itemView.findViewById(R.id.voucherCodeTextView)
        val voucherDescriptionTextView: TextView = itemView.findViewById(R.id.voucherDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.voucher_item_layout, parent, false)
        return VoucherViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        val currentVoucher = vouchers[position]
        holder.voucherCodeTextView.text = currentVoucher.code
        holder.voucherDescriptionTextView.text = currentVoucher.description
    }

    override fun getItemCount() = vouchers.size
}