package com.example.anew.model

const val USER_NAME = "name"
const val USER_EMAIL = "email"
const val USER_PHONE_NO = "phone_no"
const val USER_ADDRESS = "address"
const val USER_IMAGE = "image"

data class User(val email:String="", val name:String="",
                val phoneNo:String="", val image:String = "",val address:String ="")
