package com.example.anew.model


data class CartProduct(

    val id: String = "",
    val name: String = "",
    val description: String = "",
    val expDate: String = "",
    var quantity: Int = 0,
    val prize: Float = 0.0f,
    val manName: String = "",
    val image1:String = "",
    val image2:String = "",
    val image3:String = "",
    val image4:String = "",
    val dateAdded:String=""
)