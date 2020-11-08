package com.example.anew.ui.admin.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.AdminOrderSingleItemLayoutBinding
import com.example.anew.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdminOrderAdapter(options:FirestoreRecyclerOptions<Order>): FirestoreRecyclerAdapter<Order, AdminOrderHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderHolder {
        val binding:AdminOrderSingleItemLayoutBinding
        = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.admin_order_single_item_layout,parent,false)
        return AdminOrderHolder(binding)
    }

    override fun onBindViewHolder(holderAdmin: AdminOrderHolder, position: Int, model: Order) {
        holderAdmin.bind(model)
    }


}

class AdminOrderHolder(val binding: AdminOrderSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Order){
        binding.order = model
    }

}