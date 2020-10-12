package com.example.anew.utils

import android.content.Context
import android.util.Patterns
import android.view.inputmethod.InputMethodManager

import androidx.fragment.app.FragmentActivity

object MyUtil {

    fun validateUser(name: String, password: String, phoneNo: String, email: String) =
        isValidEmail(email) &&
                password.isNotEmpty() && password.length == 8 &&
                isPhoneNo(phoneNo) &&
                !containsDigit(name)


    fun containsDigit(name: String) = name.contains(Regex("\\d")) && name.isNotEmpty()
    fun isPhoneNo(name: String) =
        Patterns.PHONE.matcher(name).matches() && name.isNotEmpty() && name.length == 10

    fun isValidEmail(name: String) =
        Patterns.EMAIL_ADDRESS.matcher(name).matches() && name.isNotEmpty()

    fun isPassword(name: String) = name.length >= 8 && name.isNotEmpty()
    fun hideKeyBoard(activity: FragmentActivity) {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        }
    }
}