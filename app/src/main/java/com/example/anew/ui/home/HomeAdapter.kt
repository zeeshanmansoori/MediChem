package com.example.anew.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.HomeSingleItemLayoutBinding
import com.example.anew.model.Product
import com.example.anew.ui.admin.home.AdminHomeAdapter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HomeAdapter(options: FirestoreRecyclerOptions<Product>,val listener:AdminHomeAdapter.ProductItemClickListener) :
    FirestoreRecyclerAdapter<Product, ProductHolder>(
        options
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding: HomeSingleItemLayoutBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.home_single_item_layout, parent, false
            )
        return ProductHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int, model: Product) {
        val currentItem = getItem(position)
        Log.d("mytag", "current item ${currentItem.toString()}")
        currentItem?.let {
            holder.bind(it,listener)
        }

    }


}

class ProductHolder(private val binding: HomeSingleItemLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(product: Product,listener:AdminHomeAdapter.ProductItemClickListener) {
        with(binding) {
            //medicineName.text = product.name
            //medicinePrize.text = product.prize
//            GlideApp.with(binding.root).load(product.image.toUri()).into(
//                medicineImage
//            )
            //Picasso.get().load(product.image).into(medicineImage)
            //Glide.with(binding.root).load(product.image).into(medicineImage)

            this.product = product
            this.listener  = listener
        }
    }
}