package com.example.anew.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val DESCRIPTION = "description"
const val PRODUCT_NAME = "name"
const val QUANTITY = "quantity"
const val EXP_DATE = "expDate"
const val PRIZE = "prize"
const val MAN_NAME = "manName"
const val PRODUCT_ID = "id"
const val IMAGE1 = "image1"
const val IMAGE2 = "image2"
const val IMAGE3 = "image3"
const val IMAGE4 = "image4"
const val DATE_ADDED = "date_added"


@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val expDate: String = "",
    var quantity: Int = 0,
    val prize: Float = 0.0f,
    val manName: String = "",
    val image1: String = "",
    val image2: String = "",
    val image3: String = "",
    val image4: String = ""
) : Parcelable



data class Order(
    val address: Address = Address(),
    val product: MutableList<Product> = mutableListOf(),
    val dateAdded: String = "",
    val paymentStatus: String = "paid online"
)