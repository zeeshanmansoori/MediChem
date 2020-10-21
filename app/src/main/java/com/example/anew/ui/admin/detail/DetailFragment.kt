package com.example.anew.ui.admin.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.anew.AdminActivity
import com.example.anew.R
import com.example.anew.databinding.AdminFragmentDetailBinding

class DetailFragment:Fragment(){

    private lateinit var binding:AdminFragmentDetailBinding

    //nav args
    val args:DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.admin_fragment_detail,container,false)
        binding.product = args.Product
        return binding.root
    }


    override fun onStart() {
        super.onStart()
        with(activity as AdminActivity){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onStop() {
        super.onStop()
        with(activity as AdminActivity){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

}