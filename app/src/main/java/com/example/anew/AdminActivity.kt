package com.example.anew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.customview.widget.Openable
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.example.anew.ui.intialSetup.CHECK_BOX
import com.example.anew.ui.intialSetup.IS_USER
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.paperdb.Paper

class AdminActivity : AppCompatActivity() {


    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val navController = findNavController(R.id.nav_host_fragment_admin)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_orders, R.id.nav_add
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener {
            if (it.itemId == R.id.logout)
                logMeOut()
            else {
                val handled = NavigationUI.onNavDestinationSelected(it, navController)
                if (handled) {
                    val navParent = navigationView.parent
                    if (navParent is Openable)
                        navParent.close()
                }
                handled
            }
        }

    }

    private fun logMeOut(): Boolean {
        MaterialAlertDialogBuilder(this).apply {

            setTitle("Alert")
            setMessage("Do you really want to sign out \nthis will not delete your account")
            setNeutralButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Log Me Out") { _, _ ->
                Firebase.auth.signOut()
                Paper.book().write(CHECK_BOX, false)
                Paper.book().write(IS_USER, false)
                startActivity(Intent(this@AdminActivity, MainActivity::class.java).apply {
                 //   flags = Intent.FLAG_ACTIVITY_NEW_TASK
                 //   addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                })
                finishAffinity()
            }
        }.also {
            it.show()
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("adminActivity", "onSupportNavigateUp is called")
        val navController = findNavController(R.id.nav_host_fragment_admin)
        Log.d(
            "adminActivity",
            "onSupportNavigateUp +${navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()}"
        )
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}