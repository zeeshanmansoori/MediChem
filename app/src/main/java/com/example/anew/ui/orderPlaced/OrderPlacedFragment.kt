package com.example.anew.ui.orderPlaced

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.example.anew.MainActivity
import com.example.anew.R
import com.example.anew.databinding.FragmentOrderPlacedBinding
import com.example.anew.model.Order
import com.google.firebase.firestore.FirebaseFirestore

const val ORDER_PLACED = "order_placed"
class OrderPlacedFragment : Fragment(), View.OnClickListener {

    private lateinit var binding:FragmentOrderPlacedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_order_placed, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.checkOutBtn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        with(activity as MainActivity){
            supportActionBar?.hide()
        }
    }

    override fun onStop() {
        super.onStop()
        with(activity as MainActivity){
            supportActionBar?.show()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.checkOutBtn.id -> navigateToOrders()
        }
    }

    private fun navigateToOrders():Boolean{
        with(activity as MainActivity) {
            val menuId = navView.menu.findItem(R.id.nav_orders)
            return NavigationUI.onNavDestinationSelected(menuId, findNavController())
        }
    }

}