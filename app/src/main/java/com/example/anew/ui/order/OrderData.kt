package com.example.anew.ui.order

data class OrderData(
    val orderId: String, val name:String, val totalPrice:String,
    val image:Int, val quantity:Int, val date:String, val paymentStatus:String)