package com.example.anew.model

class PaymentProduct(val products: Array<Product>) {

    private var totalPrize:Float = 0.0.toFloat()

    val noOfItems:Int
    get() = products.sumOf {
        it.quantity
    }

    val totalMrp: Double
    get() = products.sumByDouble {
        it.prize.toDouble() * it.quantity
    }

    val shippingCharges:Float = 0.toFloat()

    val discount = 0

    val amountToBePaid:Double
    get() = totalMrp + shippingCharges - discount

    val totalSavings:Double
    get() = totalMrp - amountToBePaid

}