package com.example.anew.ui.admin.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.OrderSingleItemLayoutBinding
import com.example.anew.model.Order
import com.example.anew.ui.order.OrderAdapter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdminOrderAdapter(options:FirestoreRecyclerOptions<Order>,val listener: OrderAdapter.OrderItemClickListener): FirestoreRecyclerAdapter<Order, AdminOrderAdapter.AdminOrderHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderHolder {
        val binding:OrderSingleItemLayoutBinding
        = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.order_single_item_layout,parent,false)
        return AdminOrderHolder(binding)
    }

    override fun onBindViewHolder(holderAdmin: AdminOrderHolder, position: Int, model: Order) {
        holderAdmin.bind(model)
    }

    inner class AdminOrderHolder(val binding: OrderSingleItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.expansionBtn.setOnClickListener {
                listener.onExpansionButtonClicked(it,binding.expandableView,binding.cardView)

            }
        }
        fun bind(model: Order){
            binding.order = model
        }

    }
}

