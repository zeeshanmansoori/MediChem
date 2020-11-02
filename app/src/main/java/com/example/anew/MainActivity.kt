package com.example.anew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewParent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.customview.widget.Openable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.anew.databinding.NavHeaderMainBinding
import com.example.anew.model.User
import com.example.anew.ui.admin.home.NavHeaderViewModel
import com.example.anew.ui.intialSetup.CHECK_BOX
import com.example.anew.ui.intialSetup.IS_USER
import com.example.anew.ui.intialSetup.USER_REF
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.paperdb.Paper

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)

        // setting up header layout to show user details
        val headerView = navView.getHeaderView(0)
        val navHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
            FirebaseFirestore.getInstance().collection(USER_REF)
                .document(userId)
                .get()
                .addOnSuccessListener {
                    navHeaderMainBinding.user = it.toObject(User::class.java)!!
                }.addOnFailureListener {
                    Snackbar.make(drawerLayout,"plz login again having some issue",Snackbar.LENGTH_SHORT).show()
                }
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_orders, R.id.nav_settings, R.id.nav_profile
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
                Log.d("mynavigation", "called")
                handled

            }
        }

    }

    private fun logMeOut(): Boolean {
        val dialogBuilder = MaterialAlertDialogBuilder(this)
        with(dialogBuilder) {
            setTitle("Alert")
            setMessage("Do you really want to sign out \nthis will not delete your account")
            setNeutralButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Log Me Out"){
                _,_ ->
                FirebaseAuth.getInstance().signOut()
                Paper.book().write(CHECK_BOX, false)
                Paper.book().write(IS_USER,false)
                startActivity(Intent(this@MainActivity,MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK))

            }
        }.apply { show() }

        return true

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()

        //this is not called because we handled this in onnavgationitemselected

    }


}