package com.example.anew.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.CartSingleItemLayoutBinding
import com.example.anew.model.CartProduct
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CartAdapter(val cartItemClickListener: CartItemClickListener,
                  options: FirestoreRecyclerOptions<CartProduct>) :
    FirestoreRecyclerAdapter<CartProduct, CartHolder>(
        options
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {

        val binding: CartSingleItemLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.cart_single_item_layout,
                parent,
                false
            )
        return CartHolder(binding,cartItemClickListener)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int, model: CartProduct) {
        holder.bind(model)
    }

    interface CartItemClickListener{
        fun onCartItemClicked(view: View,cartProduct: CartProduct)
        }
    }


class CartHolder(val binding: CartSingleItemLayoutBinding,val cartItemClickListener: CartAdapter.CartItemClickListener)
    : RecyclerView.ViewHolder(binding.root)
{
    init {
        binding.listner = cartItemClickListener
    }

    fun bind(model: CartProduct){
        binding.cartProduct = model
        binding.numberPicker.number = model.product.quantity.toString()
    }

}
