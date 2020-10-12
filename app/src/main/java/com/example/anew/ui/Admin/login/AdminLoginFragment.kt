package com.example.anew.ui.Admin.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentAdminLoginBinding


class AdminLoginFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentAdminLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_login, container, false)
        with(activity as AppCompatActivity){
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
        with(activity as AppCompatActivity){
            supportActionBar?.show()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.login_btn -> v.findNavController().navigate(R.id.action_adminLoginFragment_to_homeFragment)
        }
    }
}