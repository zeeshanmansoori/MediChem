package com.example.anew.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView

import androidx.fragment.app.FragmentActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*


object MyUtil {
    fun hideKeyBoard(activity: FragmentActivity) {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }

    fun getDate(): String {
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = Date()
        return format.format(date)
    }

    fun isConnectedToInternet(context: Context?):Boolean{
        if (context == null)
            return false
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true



    }


}