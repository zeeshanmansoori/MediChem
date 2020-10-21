package com.example.anew.ui.intialSetup

import android.os.Bundle
import android.util.Patterns
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
import com.example.anew.databinding.FragmentLoginBinding
import com.example.anew.utils.MyUtil
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class loginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var databaseReference: DatabaseReference

    //auth
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        mAuth = FirebaseAuth.getInstance()

        settingListeners()
        return binding.root
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bottom_container -> moveToSignUp()
            R.id.move_to_admin -> moveToAdmin()
            R.id.login_btn -> loginUser()
            else -> Snackbar.make(binding.root,"Nothing happened",Snackbar.LENGTH_SHORT).show()

        }
    }

    private fun loginUser() {

        //hide keyboard
        activity?.let { MyUtil.hideKeyBoard(it) }

        // custom dialog
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)

        //databaseReference = FirebaseDatabase.getInstance().reference
        with(binding) {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "email is empty"
                emailEditText.requestFocus()
                return

            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "provide valid email"
                emailEditText.requestFocus()
                return
            }

            if (password.isEmpty()){
                passwordEditText.error = "password is empty"
                passwordEditText.requestFocus()
                return
            }else if (password.length<6){
                passwordEditText.error = "password length must be greater than 5 characters"
                passwordEditText.requestFocus()
                return
            }

            dialog.startDialog()
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                if (it.isSuccessful){
                    val user = mAuth.currentUser


                    user?.let {
                        user ->
                        if (user.isEmailVerified){
                            Snackbar.make(root,"logged in successfully",Snackbar.LENGTH_SHORT).show()

                        }else{
                            Snackbar.make(root,"check your mail to verify your account",Snackbar.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismissDialog()
                    moveToHome()

                }else{
                    Snackbar.make(root,"login failed",Snackbar.LENGTH_SHORT).show()
                    dialog.dismissDialog()
                }
            }
        }



    }

    private fun settingListeners() {
        with(binding) {
            moveToAdmin.setOnClickListener(this@loginFragment)
            loginBtn.setOnClickListener(this@loginFragment)
            bottomContainer.setOnClickListener(this@loginFragment)

        }
    }

    private fun moveToHome(){
        findNavController().navigate(R.id.action_nav_login_to_nav_home)
    }

    private fun moveToSignUp(){
        findNavController().navigate(R.id.action_nav_login_to_nav_signUp)
    }
    private fun moveToAdmin(){
        findNavController().navigate(R.id.action_nav_login_to_adminActivity)
    }

    override fun onResume() {
        super.onResume()
        //binding.loginBtn.isEnabled = true
        with(activity as AppCompatActivity) {
            supportActionBar?.hide()

        }
        with(activity as MainActivity) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        with(activity as AppCompatActivity) {
            supportActionBar?.show()
        }

        with(activity as MainActivity) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }


}



//        if (MyUtil.isPhoneNo(email) && MyUtil.isPassword(password)) {
//            dialog.startDialog()
//            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    if (snapshot.child("Users").child(email).exists()) {
//                        val user: User? =
//                            snapshot.child("Users").child(email).getValue(User::class.java)
//                        if (user?.password == password && user?.email == email) {
//                            dialog.dismissDialog()
//                            Snackbar.make(binding.root, "login done", Snackbar.LENGTH_SHORT)
//                                .show()
//
//                            //move to home
//                            moveToHome()
//                        } else {
//                            dialog.dismissDialog()
//                            Snackbar.make(binding.root, "some issue with password", Snackbar.LENGTH_SHORT)
//                                .show()
//
//                        }
//
//
//                    } else {
//                        dialog.dismissDialog()
//                        Snackbar.make(binding.root, "Account does not exist", Snackbar.LENGTH_SHORT)
//                            .show()
//
//                    }
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })
//        } else {
//            if (MyUtil.isPhoneNo(email) && !MyUtil.isPassword(password))
//                Snackbar.make(
//                    binding.root,
//                    "password should be of at least 8 character long",
//                    Snackbar.LENGTH_SHORT
//                )
//                    .show()
//            else {
//                Snackbar.make(binding.root, "fill the correct details", Snackbar.LENGTH_SHORT)
//                    .show()
//
//            }
//        }