package com.example.anew

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
import com.example.anew.ui.intialSetup.USER_REF
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout:DrawerLayout
    private val viewModel:NavHeaderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout= findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)

        // setting up header layout to show user details
        val headerView = navView.getHeaderView(0)
        val navHeaderMainBinding = NavHeaderMainBinding.bind(headerView)
        FirebaseAuth.getInstance().currentUser?.uid?.let {
                userId ->
            FirebaseFirestore.getInstance().collection(USER_REF)
                .document(userId)
                .get()
                .addOnSuccessListener {

                    val user = it.toObject(User::class.java)
                    user?.let {
                        viewModel.setValue(user)
                        navHeaderMainBinding.viewModel = viewModel
                    }

                }
        }

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_orders, R.id.nav_settings,R.id.nav_profile
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.itemIconTintList = null
        navView.setNavigationItemSelectedListener {
            if (it.itemId==R.id.logout)
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
                Log.d("mynavigation","called")
                handled

            }
        }

    }

    private fun logMeOut(): Boolean {
        finish()
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

    }




}