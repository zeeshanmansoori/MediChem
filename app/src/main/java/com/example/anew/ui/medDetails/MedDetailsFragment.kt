package com.example.anew.ui.medDetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentMedDetailsBinding
import com.example.anew.model.Product

class MedDetailsFragment : Fragment() {

    private lateinit var binding: FragmentMedDetailsBinding
    //private lateinit var product: Product

    val args: MedDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_med_details, container, false)
        //product = args.product
        binding.product = args.product
        return binding.root
    }

}