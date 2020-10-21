package com.example.anew.ui.admin.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.AdminHomeSingleItemLayoutBinding
import com.example.anew.model.Product
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class AdminHomeAdapter(val listener:ProductItemClickListener, options: FirestoreRecyclerOptions<Product>) : FirestoreRecyclerAdapter<Product, ProductHolder>(
    options
)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding:AdminHomeSingleItemLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.admin_home_single_item_layout,parent,false
            )
        return ProductHolder(binding,listener)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int, model: Product) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(it)
        }

    }


    interface ProductItemClickListener{

        fun onProductItemClicked(product: Product)
        fun onProductItemLongClicked(product: Product)
    }


}

class ProductHolder(private val binding: AdminHomeSingleItemLayoutBinding,
                    private val productItemClickListener: AdminHomeAdapter.ProductItemClickListener)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product){
        binding.product = product
        binding.listener = productItemClickListener
        binding.cardView.setOnLongClickListener {
            productItemClickListener.onProductItemLongClicked(product)
            true
        }
    }
//    fun bind(product: Product){
//        with(binding){
//            medicineName.text = product.name
//            medId.text = product.id
//            price.text = product.prize
//            expDate.text = product.expDate
//            quantity.text = product.quantity.toString()
//            medicineDescription.text = product.description
////            GlideApp.with(binding.root).load(product.image.toUri()).into(
////                medicineImage
////            )
//            Glide.with(binding.root).load(product.image).into(medImgImageView)
//
//        }
//    }

}