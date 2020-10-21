package com.example.anew.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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


object MyUtil {
    const val IMAGE_REF ="images"
    const val PRODUCT_REF = "Product"
    fun validateUser(name: String, password: String, phoneNo: String, email: String) =
        isValidEmail(email) &&
                password.isNotEmpty() && password.length >= 8 &&
                isPhoneNo(phoneNo) &&
                !containsDigit(name)


    fun containsDigit(name: String) = name.contains(Regex("\\d")) && name.isNotEmpty()
    fun isPhoneNo(name: String) =
        Patterns.PHONE.matcher(name).matches() && name.isNotEmpty() && name.length == 10

    fun isValidEmail(name: String) =
        Patterns.EMAIL_ADDRESS.matcher(name).matches() && name.isNotEmpty()

    fun isPassword(name: String) = name.length >= 8 && name.isNotEmpty()
    fun hideKeyBoard(activity: FragmentActivity) {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }

    fun uploadImage(phoneNo: String, uri: Uri?){
        val storageRef: StorageReference = Firebase.storage.getReference(IMAGE_REF)
        var metadata = storageMetadata {
            contentType = "image/jpg"
        }
        val profileImageRef = storageRef.child("$phoneNo.jpg")
        uri?.let {
            val uploadTask = profileImageRef.putFile(uri,metadata)
            uploadTask.addOnSuccessListener {
                Log.d("mytag","image uploaded successfully")
            }.addOnFailureListener{
                Log.d("mytag","got exception $it")
            }
        }

    }

    fun getDownloadUri(uploadTask:UploadTask,profileImageRef:StorageReference): Uri? {
        var uri:Uri? = null
        val urlTask = uploadTask.continueWithTask{
                    task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let { throw it }
                    }
                    profileImageRef.downloadUrl
                }
                urlTask.addOnSuccessListener {
                    Log.d("mytag","uri downloaded $it")

                uri = it
                }
        return uri
    }

}