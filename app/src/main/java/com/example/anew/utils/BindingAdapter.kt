package com.example.anew.utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.anew.model.Product
import com.example.anew.ui.admin.detail.PagerAdapter
import com.example.anew.ui.order.NestedOrderAdapter
import com.squareup.picasso.Picasso
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator


@BindingAdapter("glideSrc","glideErrorSrc",requireAll = false)
fun ImageView.setImageUri(
    Uri:String?,
    drawable:Drawable?=null
){

    Uri?.let {
        uri ->
        if (uri.isNotEmpty()){
            Picasso.get().load(uri).into(this)
        }

        if (uri.isEmpty()){
            drawable?.let {
                setImageDrawable(drawable)
            }
            Log.d("mytag","drawable resource id ${drawable.toString()}")
        }

    }
    Log.d("mytag","found null")

}


@BindingAdapter("setData")
fun RecyclerView.setData(
    products: MutableList<Product>
) {
    val nestedOrderAdapter = NestedOrderAdapter()
    nestedOrderAdapter.submitList(products)
    setHasFixedSize(true)
    setItemViewCacheSize(20)
    adapter = nestedOrderAdapter
}

@BindingAdapter("setUris","setIndicator",requireAll = true)
fun ViewPager2.setUris(product: Product,indicator: DotsIndicator){
    val imgList = mutableListOf(product.image1,product.image2,product.image3,product.image4).filter {
        it.isNotEmpty()
    }
    this.adapter = PagerAdapter(imgList)
    clipChildren = false
    clipToPadding = false
    offscreenPageLimit = 3
    indicator.setViewPager2(this)
//    getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
//    val compositePageTransformer = CompositePageTransformer()
//    compositePageTransformer.addTransformer(MarginPageTransformer(5))

//    compositePageTransformer.addTransformer { page, position ->
//        val r = 1 - kotlin.math.abs(position)
//        page.scaleY = .85f + r * .15f
//        page.scaleX = .85f + r * .15f
//    }
//    setPageTransformer(compositePageTransformer)

}
