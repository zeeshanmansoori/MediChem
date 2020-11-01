package com.example.anew.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentPaymentDetailsBinding
import com.example.anew.model.PaymentProduct

class PaymentDetailsFragment : Fragment() {

    private lateinit var binding:FragmentPaymentDetailsBinding
    private val navArgs:PaymentDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_payment_details, container, false)

        binding.address = navArgs.address
        binding.paymentProduct = PaymentProduct(navArgs.products)
        return binding.root
    }

}