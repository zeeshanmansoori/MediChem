package com.example.anew.model

const val USER_NAME = "name"
const val USER_EMAIL = "email"
const val USER_PHONE_NO = "phoneNo"
const val USER_IMAGE = "image"

data class User(val email:String="", val name:String="",
                val phoneNo:String="", var image:String = "")
