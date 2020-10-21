package com.example.anew.utils


import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.anew.R

class CustomLoadingDialog(val activity: AppCompatActivity) {

    private lateinit var dialog: AlertDialog


    fun startDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_loading_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()

    }

    fun dismissDialog() = dialog.dismiss()


}