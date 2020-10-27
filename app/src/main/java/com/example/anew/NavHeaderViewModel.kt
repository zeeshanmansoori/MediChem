package com.example.anew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.anew.model.User
import com.example.anew.ui.intialSetup.USER_REF
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NavHeaderViewModel : ViewModel() {

    private var _user = MutableLiveData<User>()

    val user: LiveData<User?>
        get() = _user


    fun setValue(user: User) {
        _user.value = user
    }


}