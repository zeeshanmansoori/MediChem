package com.example.anew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.anew.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var drawerLayout:DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_admin)
        drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView
        val navController = findNavController(R.id.nav_host_fragment_admin)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home,
            R.id.nav_orders,R.id.nav_add),drawerLayout)
        setupActionBarWithNavController(navController,appBarConfiguration)
        navigationView.setupWithNavController(navController)
        navigationView.itemIconTintList = null

    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d("mytag","onSupportNavigateUp is called")
        val navController = findNavController(R.id.nav_host_fragment_admin)
        Log.d("mytag","onSupportNavigateUp +${navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()}")
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}