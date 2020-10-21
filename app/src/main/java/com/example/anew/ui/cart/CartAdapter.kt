package com.example.anew.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R

class CartAdapter(private val context: Context, private val cartElementList:MutableList<CartData>) : RecyclerView.Adapter<CartHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.cart_single_item_layout, parent, false)
        return CartHolder(view)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        val element = cartElementList[position]
        holder.apply {
            medicine_name.text = element.name
            medicine_prize.text = element.prize

        }
    }

    override fun getItemCount() = cartElementList.size


}

class CartHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var medicine_image: ImageView = itemView.findViewById(R.id.medicine_image)
    var medicine_name: TextView = itemView.findViewById(R.id.medicine_name)
    var medicine_prize: TextView = itemView.findViewById(R.id.medicine_prize)



}