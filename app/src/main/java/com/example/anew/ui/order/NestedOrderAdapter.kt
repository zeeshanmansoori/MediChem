package com.example.anew.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.NestedOrderSingleItemLayoutBinding
import com.example.anew.model.Product

class NestedOrderAdapter : ListAdapter<Product, NestedOrderAdapter.NestedOrderHolder>(callback) {


    companion object {
        val callback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean =
                oldItem.name == newItem.name &&
                        oldItem.description == newItem.description &&
                        oldItem.medicineUsage == newItem.medicineUsage &&
                        oldItem.prize == newItem.prize &&
                        oldItem.quantity == newItem.quantity
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NestedOrderHolder {
       val binding:NestedOrderSingleItemLayoutBinding
        = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.nested_order_single_item_layout,parent,false)
        return NestedOrderHolder(binding)
    }

    override fun onBindViewHolder(holder: NestedOrderHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION)
            holder.bind(position)
    }


    inner class NestedOrderHolder(val binding: NestedOrderSingleItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.product = getItem(position)
            Log.d("mytag","called")
        }
    }

}