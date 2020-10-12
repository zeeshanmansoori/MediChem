package com.example.anew.ui.order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R

class OrderAdapter(private val context: Context, private val orderElementList:MutableList<OrderData>) : RecyclerView.Adapter<OrderHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.ordered_single_item_layout, parent, false)
        return OrderHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHolder, position: Int) {
        val element = orderElementList[position]
        holder.apply {
            medicine_name.text = element.name

        }
    }

    override fun getItemCount() = orderElementList.size


}

class OrderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var medicine_image: ImageView = itemView.findViewById(R.id.medicine_image)
    var medicine_name: TextView = itemView.findViewById(R.id.medicine_name)
    var total_price: TextView = itemView.findViewById(R.id.ordered_total)
    var qty: TextView = itemView.findViewById(R.id.ordered_qty)
    var date: TextView = itemView.findViewById(R.id.ordered_date)
    var payment_status: TextView = itemView.findViewById(R.id.payment_status)


}