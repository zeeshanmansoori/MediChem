package com.example.anew.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.example.anew.R
import com.example.anew.databinding.CartSingleItemLayoutBinding
import com.example.anew.model.CartProduct
import com.example.anew.model.Product
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CartAdapter(
    val cartItemClickListener: CartItemClickListener,
    options: FirestoreRecyclerOptions<CartProduct>
) :
    FirestoreRecyclerAdapter<CartProduct, CartAdapter.CartHolder>(
        options
    ) {

    var totalItemCount = 0
    var totalPrize = 0.0
 
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {

        val binding: CartSingleItemLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.cart_single_item_layout,
                parent,
                false
            )
        return CartHolder(binding)
    }



    override fun onBindViewHolder(holder: CartHolder, position: Int, model: CartProduct) {
        holder.bind(model)

    }

    //
    inner class CartHolder(
        val binding: CartSingleItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.listner = cartItemClickListener
            binding.numberPicker.setOnValueChangeListener { _, oldValue, newValue ->
                cartItemClickListener.onNumberPickerValueChanged(getItem(bindingAdapterPosition),oldValue, newValue)

            }

        }

        fun bind(model: CartProduct) {
            binding.cartProduct = model
            binding.numberPicker.number = model.product.quantity.toString()
            totalPrize += (model.product.prize) * (model.product.quantity)
            totalItemCount += model.product.quantity
            cartItemClickListener.onCartItemChange(totalItemCount, totalPrize)

        }

        fun getCurrentItem():Product{
            val position = bindingAdapterPosition
            if (position!=RecyclerView.NO_POSITION)
                return getItem(position).product
            return Product()

        }

    }

    interface CartItemClickListener {
        fun onCartItemClicked(view: View, cartProduct: CartProduct)

        fun onCartItemChange(totalItemCount: Int, totalPrize: Double)

        fun onNumberPickerValueChanged(product: CartProduct,oldValue: Int, newValue: Int)
    }

}


