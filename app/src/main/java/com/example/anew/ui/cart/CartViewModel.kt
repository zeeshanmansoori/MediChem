package com.example.anew.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CartViewModel: ViewModel() {

    private val _totalItem = MutableLiveData<Int>()
    private val _totalPrize = MutableLiveData<Float>()

    val totalItem:LiveData<Int>
    get() = _totalItem

    val totalPrize:LiveData<Float>
        get() = _totalPrize


}