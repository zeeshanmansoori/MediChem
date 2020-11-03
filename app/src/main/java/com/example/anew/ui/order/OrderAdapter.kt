package com.example.anew.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.OrderedSingleItemLayoutBinding
import com.example.anew.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OrderAdapter(options:FirestoreRecyclerOptions<Order>): FirestoreRecyclerAdapter<Order, OrderHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val binding:OrderedSingleItemLayoutBinding
        = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.ordered_single_item_layout,parent,false)
        return OrderHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int, model: Order) {
        holder.bind(model)
    }


}

class OrderHolder(val binding: OrderedSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Order){

    }

}