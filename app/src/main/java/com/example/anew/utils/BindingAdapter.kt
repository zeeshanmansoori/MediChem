package com.example.anew.utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso


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
