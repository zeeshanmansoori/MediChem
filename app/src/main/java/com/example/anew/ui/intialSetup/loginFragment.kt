package com.example.anew.ui.intialSetup

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.example.anew.MainActivity
import com.example.anew.R
import com.example.anew.databinding.FragmentLoginBinding
import com.example.anew.utils.MyUtil
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.paperdb.Paper

const val CHECK_BOX = "checkbox"
const val ADMIN_REF = "admin"
const val IS_USER = "isUser"

class loginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding

    //auth
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private var snackbar: Snackbar? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Paper.init(activity)
        Log.d("mytag","checkbox ${Paper.book().read(CHECK_BOX, false)}")
        Log.d("mytag","user ${Paper.book().read(IS_USER,false)}")
        Log.d("mytag","auth ${mAuth.currentUser != null}")
        if (Paper.book().read(CHECK_BOX, false)
            && Paper.book().read(IS_USER,false)
            && mAuth.currentUser != null
            ) {
            navigateToHome()
        }else if (Paper.book().read(CHECK_BOX, false)
            && !Paper.book().read(IS_USER,false)
            && mAuth.currentUser != null){
            navigateToAdminHome()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        settingListeners()
        return binding.root
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.bottomContainer.id -> navigateToSignUp()
            binding.adminLoginBtn.id -> loginUser(binding.adminLoginBtn.id)
            binding.loginBtn.id -> loginUser(binding.loginBtn.id)
            else -> {
                snackbar = Snackbar.make(binding.root, "Nothing happened", Snackbar.LENGTH_SHORT)
                snackbar?.show()
            }

        }
    }

    private fun loginUser(id: Int) {

        //hide keyboard
        activity?.let { MyUtil.hideKeyBoard(it) }

        // custom dialog
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)

        with(binding) {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty()) {
                emailEditTextContainer.error = "email is empty"
                emailEditTextContainer.requestFocus()
                return

            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditTextContainer.error = "provide valid email"
                emailEditTextContainer.requestFocus()
                return
            }

            if (password.isEmpty()) {
                passwordEditTextContainer.error = "password is empty"
                passwordEditTextContainer.requestFocus()
                return
            } else if (password.length < 6) {
                passwordEditTextContainer.error = "password must be of minimum 8 characters"
                passwordEditTextContainer.requestFocus()
                return
            }

            dialog.startDialog()
            if (id == binding.loginBtn.id) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = mAuth.currentUser


                        user?.let { user ->
                            snackbar = if (user.isEmailVerified) {
                                Snackbar.make(root, "logged in successfully", Snackbar.LENGTH_SHORT)
                            } else {
                                Snackbar.make(root, "check your mail to verify your account", Snackbar.LENGTH_SHORT)
                            }
                            snackbar?.show()
                        }
                        dialog.dismissDialog()
                        Paper.book().write(CHECK_BOX, binding.rememberMeCheckbox.isChecked)
                        Paper.book().write(IS_USER,true)
                        navigateToHome()

                    } else {
                        snackbar = Snackbar.make(root, "email and password did not matched", Snackbar.LENGTH_SHORT)
                        snackbar?.show()
                        dialog.dismissDialog()
                    }

                }
                return
            }

            if (id == binding.adminLoginBtn.id) {
                FirebaseFirestore.getInstance().collection(ADMIN_REF).document(email).get()
                    .addOnSuccessListener {
                        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                            snackbar = Snackbar.make(root, "welcome back admin", Snackbar.LENGTH_SHORT)
                            snackbar?.show()
                            dialog.dismissDialog()
                            Paper.book().write(CHECK_BOX, binding.rememberMeCheckbox.isChecked)
                            Paper.book().write(IS_USER,false)
                            navigateToAdminHome()
                        }
                    }
                    .addOnFailureListener {
                        dialog.dismissDialog()
                        snackbar = Snackbar.make(root, "there is some error", Snackbar.LENGTH_SHORT)
                        snackbar?.show()
                    }


            }

        }


    }

    private fun settingListeners() {
        with(binding) {
            adminLoginBtn.setOnClickListener(this@loginFragment)
            loginBtn.setOnClickListener(this@loginFragment)
            bottomContainer.setOnClickListener(this@loginFragment)

            emailEditText.addTextChangedListener {
                it?.let {
                    if (it.isNotEmpty() && emailEditTextContainer.error != null)
                        emailEditTextContainer.error = null
                }
            }

            passwordEditText.addTextChangedListener {
                it?.let {
                    if (it.isNotEmpty() && passwordEditTextContainer.error != null)
                        passwordEditTextContainer.error = null
                }
            }
        }
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_nav_login_to_nav_home)
    }

    private fun navigateToSignUp() {
        findNavController().navigate(R.id.action_nav_login_to_nav_signUp)
    }

    private fun navigateToAdminHome() {
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

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
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