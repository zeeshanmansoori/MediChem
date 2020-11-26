package com.example.anew.model

import java.io.Serializable

const val USER_NAME = "name"
const val USER_EMAIL = "email"
const val USER_PHONE_NO = "phoneNo"
const val USER_IMAGE = "image"

//
const val CITY_NAME = "City"
const val LOCALITY = "Locality"
const val BUILDING_NAME = "Bldg_name"
const val PIN_CODE = "Pin_code"
const val STATE = "State"
const val LAND_MARK = "Land_mark"
const val ALTERNATE_PHONE_NO = "Alternate_phone_number"
const val USER_ADDRESSES = "Addresses"
const val ADDRESS1 = "Address1"


data class User(
    val email: String = "", val name: String = "",
    val phoneNo: String = "", var image: String = "",
    val admin: Boolean = false
)


data class Address(
    val city: String = "",
    val locality: String = "",
    val buildingName: String = "",
    val pinCode: String = "",
    val state: String = "",
    val landMark: String = "",
    val userName: String = "",
    val phoneNo: String = "",
    val alternatePhoneNo: String = ""
) : Serializable {
    override fun toString(): String =
        "$buildingName $locality\n$landMark \n$city $pinCode \n$state \n$phoneNo\n$alternatePhoneNo "
}


data class AdminUser(
    val upiId:String = "",
    val email: String = "", val name: String = "",
    val phoneNo: String = "", var image: String = "",
    val admin: Boolean = false,
    val address: Address = Address()
)


