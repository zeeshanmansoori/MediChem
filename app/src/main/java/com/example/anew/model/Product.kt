package com.example.anew.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val DESCRIPTION = "description"
const val PRODUCT_NAME = "name"
const val QUANTITY = "quantity"
const val PRIZE = "prize"
const val PRODUCT_ID = "id"
const val IMAGE1 = "image1"
const val IMAGE2 = "image2"
const val IMAGE3 = "image3"
const val IMAGE4 = "image4"
const val DATE_ADDED = "date_added"
const val MEDICINE_USAGE = "medicineUsage"


@Parcelize
data class Product(
    val id: String = "",
    var name: String = "",
    var description: String = "",
    var quantity: Int = 0,
    var prize: Float = 0.0f,
    var medicineUsage: String = "",
    val image1: String = "",
    val image2: String = "",
    val image3: String = "",
    val image4: String = ""
) : Parcelable


data class CartProduct(
    val product: Product=Product(),
    val dateAdded:String=""
)

@Parcelize
data class Order(
    val address: Address = Address(),
    val dateAdded: String = "",
    var orderId:String = "",
    val paymentStatus: Boolean =true,
    val product: MutableList<Product> = mutableListOf(),
    val totalPrize: Double = product.sumByDouble {it.prize.toDouble() }
):Parcelable

