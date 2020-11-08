package com.example.anew.ui.order

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.OrderSingleItemLayoutBinding
import com.example.anew.model.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OrderAdapter(options: FirestoreRecyclerOptions<Order>,val listener: OrderItemClickListener) :
    FirestoreRecyclerAdapter<Order, OrderHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val binding: OrderSingleItemLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.order_single_item_layout, parent, false
            )
        Log.d("orderAdapter","oncreateviewholder")
        return OrderHolder(binding,listener)

    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int, model: Order) {
            holder.bind(model)
    }
    interface OrderItemClickListener{

        fun onExpansionButtonClicked(view: View, expandableView: LinearLayoutCompat, cardView: CardView)
    }


}

class OrderHolder(val binding: OrderSingleItemLayoutBinding,listener: OrderAdapter.OrderItemClickListener) :
    RecyclerView.ViewHolder(binding.root){
    init {
        binding.expansionBtn.setOnClickListener {
            listener.onExpansionButtonClicked(it,binding.expandableView,binding.cardView)

        }
    }
    fun bind(model: Order){
        binding.order = model

    }


}