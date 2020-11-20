package com.example.anew.ui.intialSetup

import android.content.Intent
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
import com.example.anew.model.USER_EMAIL
import com.example.anew.model.User
import com.example.anew.utils.MyUtil
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import io.paperdb.Paper

const val CHECK_BOX = "checkbox"
const val ADMIN_REF = "Admin"
const val IS_USER = "isUser"
const val RC_SIGN_IN = 123

class loginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentLoginBinding

    //auth
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore

    private var snackbar: Snackbar? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //Paper.init(activity)
        super.onActivityCreated(savedInstanceState)
        if (Paper.book().read(CHECK_BOX, false)
            && Paper.book().read(IS_USER, false)
            && mAuth.currentUser != null
        ) {
            navigateToHome()
        } else if (Paper.book().read(CHECK_BOX, false)
            && !Paper.book().read(IS_USER, false)
            && mAuth.currentUser != null
        ) {
            navigateToAdminHome()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        with(activity as AppCompatActivity) {
            googleSignInClient = GoogleSignIn.getClient(this, gso)
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
        firestore = FirebaseFirestore.getInstance()
        binding.rememberMeCheckbox.isChecked = true
        return binding.root
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.bottomContainer.id -> navigateToSignUp()
            binding.adminLoginBtn.id -> loginUser(binding.adminLoginBtn.id)
            binding.loginBtn.id -> loginUser(binding.loginBtn.id)
            binding.googleLoginBtn.id -> loginUser(binding.googleLoginBtn.id)
            else -> {
                snackbar = Snackbar.make(binding.root, "Nothing happened", Snackbar.LENGTH_SHORT)
                snackbar?.show()
            }

        }
    }

    private fun loginUser(btnId: Int) {

        Paper.book().write(CHECK_BOX, binding.rememberMeCheckbox.isChecked)

        //hide keyboard
        activity?.let { MyUtil.hideKeyBoard(it) }

        // custom dialog
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        if (btnId == binding.googleLoginBtn.id) {
            Paper.book().write(IS_USER, true)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            return
        }

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


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {

                if (it.isSuccessful) {

                    FirebaseFirestore.getInstance().collection(USER_REF)
                        .document(mAuth.currentUser?.uid!!)
                        .get()
                        .addOnSuccessListener {

                            val currentUser = it.toObject(User::class.java)!!

                            if (currentUser.admin) {
                                dialog.dismissDialog()
                                Paper.book().write(IS_USER, false)
                                if (btnId == binding.adminLoginBtn.id) {
                                    navigateToAdminHome()
                                    FirebaseMessaging.getInstance().subscribeToTopic("orders")
                                } else {
                                    dialog.dismissDialog()
                                    with(activity as MainActivity) {
                                        MaterialAlertDialogBuilder(this).apply {
                                            setTitle("Alert")
                                            setMessage("It is an Admin account Do you really want to continue as User")
                                            setPositiveButton("OK") { _,_ ->
                                                navigateToHome()
                                            }
                                            setNegativeButton("open as Admin") { _, _ ->
                                                navigateToAdminHome()
                                                FirebaseMessaging.getInstance().subscribeToTopic("orders")
                                            }
                                            setNeutralButton("CANCEL") { dialog, _ ->
                                                mAuth.signOut()
                                                dialog.dismiss()
                                            }
                                        }.also {
                                            it.show()
                                        }
                                    }

                                }
                            } else {
                                dialog.dismissDialog()
                                Paper.book().write(IS_USER, true)
                                navigateToHome()
                            }

                        }


                } else {
                    snackbar = Snackbar.make(
                        root,
                        "Email / Password incorrect ",
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar?.show()
                    dialog.dismissDialog()
                }

            }
            return


        }


    }

    private fun settingListeners() {
        with(binding) {
            googleLoginBtn.setOnClickListener(this@loginFragment)
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
        snackbar?.dismiss()
        super.onPause()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("LoginFragment", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("LoginFragment", "Google sign in failed", e)

                }
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)

            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("LoginFragment", "signInWithCredential:success")
                    val user = mAuth.currentUser
                    user?.let {
                        addToDb(it, dialog)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("LoginFragment", "signInWithCredential:failure", task.exception)
                    // ...
                    Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT)
                        .show()
                    dialog.dismissDialog()

                }
            }
    }

    private fun addToDb(fUser: FirebaseUser, dialog: CustomLoadingDialog) {
        firestore.collection(USER_REF).whereEqualTo(USER_EMAIL, fUser.email)
            .get()
            .addOnSuccessListener {

                if (it.isEmpty) {

                    val user = User(
                        fUser.email.toString(),
                        fUser.displayName.toString(),
                        phoneNo = fUser.phoneNumber.toString()
                    )
                    firestore.collection(USER_REF).document(fUser.uid)
                        .set(user)
                        .addOnSuccessListener {
                            dialog.dismissDialog()
                            Log.d("LoginFragment", "success added")
                            snackbar =
                                Snackbar.make(
                                    binding.root,
                                    "successfully logged in",
                                    Snackbar.LENGTH_SHORT
                                )
                            snackbar?.show()
                            dialog.dismissDialog()
                            Paper.book().write(CHECK_BOX, binding.rememberMeCheckbox.isChecked)
                            Paper.book().write(IS_USER, true)
                            navigateToHome()
                        }
                        .addOnFailureListener {
                            snackbar =
                                Snackbar.make(binding.root, "$it", Snackbar.LENGTH_SHORT)
                            snackbar?.show()
                            dialog.dismissDialog()
                        }

                } else {
                    dialog.dismissDialog()
                    navigateToHome()
                }

            }
            .addOnFailureListener {
                dialog.dismissDialog()
                snackbar = Snackbar.make(
                    binding.root,
                    "check your internet connection",
                    Snackbar.LENGTH_SHORT
                )
                snackbar?.show()

            }

    }

}
