package com.example.anew.model

import java.io.Serializable

const val USER_NAME = "name"
const val USER_EMAIL = "email"
const val USER_PHONE_NO = "phoneNo"
const val USER_IMAGE = "image"

//
const val CITY_NAME = "city"
const val LOCALITY = "locality"
const val BUILDING_NAME = "bldg_name"
const val PIN_CODE = "pin_code"
const val STATE = "state"
const val LAND_MARK = "land_mark"
const val ALTERNATE_PHONE_NO = "alternate_phone_number"
const val USER_ADDRESSES = "addresses"
const val ADDRESS1 = "address1"


data class User(val email:String="", val name:String="",
                val phoneNo:String="", var image:String = "")


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
): Serializable

