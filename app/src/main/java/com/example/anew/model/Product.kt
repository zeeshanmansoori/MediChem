package com.example.anew.model

import java.io.Serializable

data class Product(
    val id:String = "",
    val quantity: Int = 0,
    val manName: String = "",
    val name: String = "",
    val description: String = "",
    val prize: String = "",
    val expDate: String = "",
    val image:String = ""
):Serializable