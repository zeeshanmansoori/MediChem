package com.example.anew.ui.admin.login

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentAdminLoginBinding
import com.example.anew.model.User
import com.example.anew.utils.MyUtil
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue


const val ADMIN_REF = "ADMIN"

class AdminLoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentAdminLoginBinding
    private lateinit var adminRef: DatabaseReference
    private lateinit var userAdmin: User

    //auth
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_login, container, false)
        mAuth = FirebaseAuth.getInstance()
        with(activity as AppCompatActivity) {
            supportActionBar?.hide()

//            with(activity as AdminActivity){
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//            }
        }

        binding.loginBtn.setOnClickListener(this)

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
            R.id.login_btn -> moveToAdminHome()
        }
    }

    private fun loginAdmin() {

        //hide keyboard
        activity?.let { MyUtil.hideKeyBoard(it) }

        // custom dialog
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)

        // ref to db
        adminRef = FirebaseDatabase.getInstance().getReference(ADMIN_REF)
        with(binding) {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditText.error = "email is empty"
                emailEditText.requestFocus()
                return

            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "provide valid email"
                emailEditText.requestFocus()
                return
            }

            if (password.isEmpty()) {
                passwordEditText.error = "password is empty"
                passwordEditText.requestFocus()
                return
            } else if (password.length < 6) {
                passwordEditText.error = "password length must be greater than 5 characters"
                passwordEditText.requestFocus()
                return
            }
            dialog.startDialog()
//            if (!isAdminMail(email)) {
//                emailEditText.error = "it is not admins email address"
//                emailEditText.requestFocus()
//                dialog.dismissDialog()
//                return
//            }
//            else
//            {

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = mAuth.currentUser


                        user?.let { user ->
                            if (user.isEmailVerified) {
                                Snackbar.make(root, "logged in successfully", Snackbar.LENGTH_SHORT)
                                    .show()

                            } else {
                                Snackbar.make(
                                    root,
                                    "check your mail to verify your account",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                        dialog.dismissDialog()
                        moveToAdminHome()

                    } else {
                        Snackbar.make(root, "login failed", Snackbar.LENGTH_SHORT).show()
                        dialog.dismissDialog()
                    }
                }
            }


    }

    private fun moveToAdminHome() {
        findNavController()
            .navigate(R.id.action_adminLoginFragment_to_homeFragment)
    }

    private fun isAdminMail(editextmail: String): Boolean {
        val admin = adminRef.child("zeeshan")
        var email = ""
        admin.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>()
                user?.let {
                    email = it.email
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("mytag", "adminlogin error got in onCancelled ")
            }
        })
        return email == editextmail
    }
}