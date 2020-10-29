package com.example.anew.ui.admin.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anew.model.User

class NavHeaderViewModel : ViewModel() {

    private val _image = MutableLiveData<String>()
    private val _name = MutableLiveData<String>()
    private val _email = MutableLiveData<String>()


    val image: LiveData<String>
        get() = _image

    val name: LiveData<String>
        get() = _name

    val email: LiveData<String>
        get() = _name

    fun setUser(user: User){
        _image.value = user.image
        _name.value = user.name
        _email.value = user.email
    }


}