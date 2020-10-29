package com.example.anew.model

import java.io.Serializable

const val CITY_NAME = "city"
const val LOCALITY = "locality"
const val BUILDING_NAME = "bldg_name"
const val PIN_CODE = "pin_code"
const val STATE = "state"
const val LAND_MARK = "land_mark"
const val ALTERNATE_PHONE_NO = "alternate_phone_number"
const val USER_ADDRESSES = "addresses"
const val ADDRESS1 = "address1"



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
):Serializable

