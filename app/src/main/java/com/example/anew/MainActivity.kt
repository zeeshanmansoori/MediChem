package com.example.anew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewParent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.customview.widget.Openable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.anew.databinding.NavHeaderMainBinding
import com.example.anew.model.User
import com.example.anew.ui.intialSetup.CHECK_BOX
import com.example.anew.ui.intialSetup.CURRENT_USER
import com.example.anew.ui.intialSetup.IS_USER
import com.example.anew.ui.intialSetup.USER_REF
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

const val IS_DARK_MODE_ENABLED = "darkMode"
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var navHeaderMainBinding:NavHeaderMainBinding
    private lateinit var googleSignInClient: GoogleSignInClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        navView = findViewById(R.id.nav_view)

        // setting up header layout to show user details
        val headerView = navView.getHeaderView(0)
        navHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        if (Firebase.auth.currentUser!=null){
            Firebase.auth.currentUser?.uid?.let {
                    userId ->
                FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
                    .addSnapshotListener {value,error ->

                        if (error==null && value!=null){
                            if (value.exists())
                                navHeaderMainBinding.user = value.toObject(User::class.java)
                        }

                    }
            }
        }


        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_orders, R.id.nav_settings, R.id.nav_profile,R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
        navView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.logout)
                logMeOut()
            else {
                //NavigationUI.onNavDestinationSelected(it, navController)
                val handled = NavigationUI.onNavDestinationSelected(it, navController)
                if (handled) {
                    val parent: ViewParent = navView.parent
                    if (parent is Openable) {
                        (parent as Openable).close()
                    }
                }
                Log.d("MainActivity", "called")
                handled
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    }

    private fun logMeOut(): Boolean {
        MaterialAlertDialogBuilder(this).apply {

            setTitle("Alert")
            setMessage("Do you really want to sign out \nthis will not delete your account")
            setNeutralButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Log Me Out"){
                _,_ ->

                Firebase.auth.signOut()
                googleSignInClient.signOut()
                Paper.book().write(CHECK_BOX, false)
                Paper.book().write(IS_USER,false)

                startActivity(Intent(this@MainActivity,MainActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })

            }
        }.apply { show() }

        return true

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

        //this is not called because we handled this in onnavgationitemselected

    }




}