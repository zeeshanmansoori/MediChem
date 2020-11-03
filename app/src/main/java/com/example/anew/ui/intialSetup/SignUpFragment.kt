package com.example.anew.ui.intialSetup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentSignUpBinding
import com.example.anew.model.User
import com.example.anew.utils.CustomLoadingDialog
import com.example.anew.utils.MyUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.paperdb.Paper


const val REQUEST_GET_PROFILE_IMAGE = 23
const val USER_REF = "USERS"

class SignUpFragment : Fragment(), View.OnClickListener {

    private lateinit var bindng: FragmentSignUpBinding
    //private lateinit var userRef: DatabaseReference

    private lateinit var firestore: FirebaseFirestore

    //auth
    private lateinit var mAuth: FirebaseAuth

    private var imageUri: Uri? = null

    private var snackbar:Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindng = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        mAuth = FirebaseAuth.getInstance()
        settingListeners()
        firestore = FirebaseFirestore.getInstance()
        return bindng.root
    }

    private fun settingListeners() {
        with(bindng) {
            signUpBtn.setOnClickListener(this@SignUpFragment)
            bottomContainer.setOnClickListener(this@SignUpFragment)

            nameEditText.addTextChangedListener {
                it?.let {
                    if (it.isNotEmpty() && nameEditTextContainer.error != null)
                        nameEditTextContainer.error = null
                }
            }

            phoneNoEditText.addTextChangedListener {
                it?.let {
                    if (it.isNotEmpty() && phoneNoEditTextContainer.error != null)
                        phoneNoEditTextContainer.error = null
                }
            }

            passwordEditText.addTextChangedListener {
                it?.let {
                    if (it.isNotEmpty() && passwordEditTextContainer.error != null)
                        passwordEditTextContainer.error = null
                }
            }

            emailEditText.addTextChangedListener {
                it?.let {
                    if (it.isNotEmpty() && emailEditTextContainer.error != null)
                        emailEditTextContainer.error = null
                }
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.bottom_container -> {
                snackbar = Snackbar.make(bindng.root,"plz mail us ",Snackbar.LENGTH_SHORT)
                snackbar?.show()

            }
            R.id.sign_up_btn -> createAccount()
        }
    }


    private fun createAccount() {
        activity?.let { MyUtil.hideKeyBoard(it) }
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)

        with(bindng) {
            val name = nameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val phoneNo = phoneNoEditText.text.toString()
            val email = emailEditText.text.toString().trim()

            if (name.isEmpty()) {
                nameEditTextContainer.error = "name is empty"
                nameEditTextContainer.requestFocus()
                return
            }
            if (email.isEmpty()) {
                emailEditTextContainer.error = "email is empty"
                emailEditTextContainer.requestFocus()
                return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditTextContainer.error = "please provide valid email"
                emailEditTextContainer.requestFocus()
                return
            }
            if (phoneNo.isEmpty()) {
                phoneNoEditTextContainer.error = "phone number is empty"
                phoneNoEditTextContainer.requestFocus()
                return
            } else if (phoneNo.length < 10) {
                phoneNoEditTextContainer.error = "phone number is of 10 characters"
                phoneNoEditTextContainer.requestFocus()
                return
            }
            if (password.isEmpty()) {
                passwordEditTextContainer.error = "password is empty"
                passwordEditTextContainer.requestFocus()

                return
            }


            if (password.length < 8) {
                passwordEditTextContainer.error = "password must be of minimum 8 characters"
                passwordEditTextContainer.requestFocus()
                return
            }
            dialog.startDialog()
            val task = mAuth.createUserWithEmailAndPassword(email, password)
            task.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(email, name,phoneNo)
                    val userId = mAuth.currentUser?.uid!!

                    firestore.collection(USER_REF).document(userId).set(
                        user
                    ).addOnSuccessListener {
                        dialog.dismissDialog()
                        Snackbar.make(
                            bindng.root,
                            "user has been registered successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        Paper.book().write(CHECK_BOX, true)
                        Paper.book().write(IS_USER, true)

                            moveToHome()
                    }
                        .addOnFailureListener {
                            dialog.dismissDialog()
                            Snackbar.make(bindng.root, "failed to register ", Snackbar.LENGTH_SHORT)
                                .show()

                        }


                } else {
                    dialog.dismissDialog()
                    Snackbar.make(
                        bindng.root,
                        "can not create an account ",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun moveToHome(){
        findNavController().navigate(R.id.action_nav_signUp_to_nav_home)
    }

//    private fun getProfileImage() {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image/*"
//        startActivityForResult(intent, REQUEST_GET_PROFILE_IMAGE)
//    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_GET_PROFILE_IMAGE && resultCode == RESULT_OK) {
//            imageUri = data?.data
//            imageUri?.let {
//                bindng.profileImageImageview.setImageURI(it)
//
//            }
//        }
    }


}


//            if (isValid) {
//                bindng.progressBar.visibility = View.VISIBLE
//                rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (!snapshot.child("Users").child(phoneNo).exists()) {
//                            val map = HashMap<String, Any>()
//                            map["name"] = name
//                            map["password"] = password
//                            map["phoneNo"] = phoneNo
//                            map["email"] = email
//
//
//                            rootRef.child("Users").child(phoneNo).updateChildren(map)
//                                .addOnCompleteListener {
//                                    if (it.isSuccessful) {
//                                        bindng.progressBar.visibility = View.GONE
//                                        Snackbar.make(
//                                            bindng.root,
//                                            "successfully created ",
//                                            Snackbar.LENGTH_SHORT
//                                        )
//                                            .show()
//                                    } else {
//                                        bindng.progressBar.visibility = View.GONE
//                                        Snackbar.make(
//                                            bindng.root,
//                                            "cant create an account",
//                                            Snackbar.LENGTH_SHORT
//                                        )
//                                            .show()
//                                    }
//                                }
//                        } else {
//                            bindng.progressBar.visibility = View.GONE
//                            Snackbar.make(bindng.root, "account exits", Snackbar.LENGTH_SHORT)
//                                .show()
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        TODO("Not yet implemented")
//                    }
//                })
//                MyUtil.uploadImage(phoneNo, imageUri)
//            } else{
//                Snackbar.make(bindng.root, "one of the fields are empty", Snackbar.LENGTH_SHORT)
//                    .show()
//            }
//
//
//        }
