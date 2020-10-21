package com.example.anew.utils

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.example.anew.R
import com.squareup.picasso.Picasso


@BindingAdapter("glideSrc","glideErrorSrc",requireAll = false)
fun ImageView.setImageUri(
    uri:String,
    drawable:Drawable?=null
){
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
