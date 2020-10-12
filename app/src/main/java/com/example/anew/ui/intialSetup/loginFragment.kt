package com.example.anew.ui.intialSetup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.anew.MainActivity
import com.example.anew.R
import com.example.anew.model.Users
import com.example.anew.databinding.FragmentLoginBinding
import com.example.anew.utils.MyUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class loginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        with(activity as AppCompatActivity) {
            supportActionBar?.hide()
            //window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            with(activity as MainActivity) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        settingListeners()
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        with(activity as AppCompatActivity) {
            supportActionBar?.show()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.move_to_signUp -> v.findNavController()
                .navigate(R.id.action_nav_login_to_nav_signUp)
            R.id.move_to_admin -> v.findNavController()
                .navigate(R.id.action_nav_login_to_adminActivity)
            R.id.login_btn -> loginUser()
            else -> Toast.makeText(context, "Nothing happened", Toast.LENGTH_SHORT)

        }
    }

    private fun loginUser() {
        //hide keyboard
        activity?.let { MyUtil.hideKeyBoard(it) }
        databaseReference = FirebaseDatabase.getInstance().reference

        val phoneNo = binding.phoneNoEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        if (MyUtil.isPhoneNo(phoneNo) && MyUtil.isPassword(password)) {
            binding.progressBar.visibility = View.VISIBLE
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child("Users").child(phoneNo).exists()) {
                        val user: Users? =
                            snapshot.child("Users").child(phoneNo).getValue(Users::class.java)
                        if (user?.password == password && user?.phoneNo == phoneNo) {
                            binding.progressBar.visibility = View.GONE

                            Snackbar.make(binding.root, "login done", Snackbar.LENGTH_SHORT)
                                .show()

                            //move to home
                            moveToHome()
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Snackbar.make(binding.root, "some issue with password", Snackbar.LENGTH_SHORT)
                                .show()

                        }
                    } else {
                        Snackbar.make(binding.root, "Account does not exist", Snackbar.LENGTH_SHORT)
                            .show()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        } else {
            if (MyUtil.isPhoneNo(phoneNo) && !MyUtil.isPassword(password))
                Snackbar.make(
                    binding.root,
                    "password should be of at least 8 character long",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            else {
                Snackbar.make(binding.root, "fill the correct details", Snackbar.LENGTH_SHORT)
                    .show()

            }
        }
    }

    private fun settingListeners() {
        with(binding) {
            moveToAdmin.setOnClickListener(this@loginFragment)
            loginBtn.setOnClickListener(this@loginFragment)
            moveToSignUp.setOnClickListener(this@loginFragment)

        }
    }

    private fun moveToHome(){
        findNavController().navigate(R.id.action_nav_login_to_nav_home)
    }
}