package com.example.anew.ui.intialSetup

import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentSignUpBinding
import com.example.anew.utils.MyUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class SignUpFragment : Fragment(), View.OnClickListener {

    private lateinit var bindng: FragmentSignUpBinding
    private lateinit var rootRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val binding = DataBindingUtil.inflate<>()
        bindng = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        settingListeners()
        return bindng.root
    }

    private fun settingListeners() {
        with(bindng) {
            signUpBtn.setOnClickListener(this@SignUpFragment)
            helpBtn.setOnClickListener(this@SignUpFragment)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.help_btn -> v.findNavController().navigate(R.id.action_nav_signUp_to_nav_home)
            R.id.sign_up_btn -> createAccount()
        }
    }

    private fun createAccount() {

        rootRef = FirebaseDatabase.getInstance().reference

        with(bindng) {
            val name = nameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val phoneNo = phoneNoEditText.text.toString()
            val email = emailEditText.text.toString()
            val isValid = MyUtil.validateUser(
                name,
                password,
                phoneNo,
                email
            )
            if (isValid){
                bindng.progressBar.visibility = View.VISIBLE
                rootRef.addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.child("Users").child(phoneNo).exists())
                        {
                            val map = HashMap<String,Any>()
                            map["name"] = name
                            map["password"] = password
                            map["phoneNo"] = phoneNo
                            map["email"] = email

                            rootRef.child("Users").child(phoneNo).updateChildren(map).addOnCompleteListener {
                                if (it.isSuccessful)
                                {
                                    bindng.progressBar.visibility = View.GONE
                                    Snackbar.make(bindng.root, "successfully created ", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                                else{
                                    bindng.progressBar.visibility = View.GONE
                                    Snackbar.make(bindng.root, "cant create an account", Snackbar.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }else{
                            bindng.progressBar.visibility = View.GONE
                            Snackbar.make(bindng.root, "account exits", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
            }else{
                Snackbar.make(bindng.root, "one of the fields are empty", Snackbar.LENGTH_SHORT)
                    .show()
            }


        }

    }

    private fun hideKeyBoard() {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(view?.windowToken,0)
        }
    }
}