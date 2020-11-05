package com.example.anew.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.ProceedWithDefaultAddBottomSheetLayoutBinding
import com.example.anew.model.Address
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProceedWithDefaultAddBottomSheet() :
    BottomSheetDialogFragment(), RadioGroup.OnCheckedChangeListener {

    private val navArgs: ProceedWithDefaultAddBottomSheetArgs by navArgs()

    private lateinit var binding: ProceedWithDefaultAddBottomSheetLayoutBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.proceed_with_default_add_bottom_sheet_layout,
            container,
            false
        )
        binding.address = navArgs.address
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener(this)

        binding.buyNow.setOnClickListener {
            navigateToPayment()
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            binding.newAddressRadioButton.id -> navigateToNewAddress()

            binding.pickFromStoreRadioButton.id -> {
                binding.apply {
                    medicalAddress.visibility = View.VISIBLE
                    medicalFullAddress.visibility = View.VISIBLE
                    defaultAddress.visibility = View.GONE
                    phoneNo.visibility = View.VISIBLE
                    getDirection.visibility = View.VISIBLE
                }
            }

            binding.defaultAddressRadioButton.id -> {
                binding.apply {
                    medicalAddress.visibility = View.GONE
                    medicalFullAddress.visibility = View.GONE
                    phoneNo.visibility = View.GONE
                    getDirection.visibility = View.GONE
                    defaultAddress.visibility = View.VISIBLE
                }
            }
        }
    }


    private fun navigateToNewAddress() {
        val action =
            ProceedWithDefaultAddBottomSheetDirections.actionProceedWithDefaultAddBottomSheetToNewAddressFragment2(
                navArgs.products
            )
        findNavController().navigate(action)
    }

    private fun navigateToPayment() {

        var address = when (binding.radioGroup.checkedRadioButtonId) {

            binding.pickFromStoreRadioButton.id -> {
                Address(
                    "mumbai", "andheri (west)",
                    "shanti apart", "400103", "MAHARASHTRA",
                    "opposite to cinema", "", "126574357", ""
                )
            }

            binding.defaultAddressRadioButton.id -> navArgs.address

            else -> Address()

        }

        val action =
            ProceedWithDefaultAddBottomSheetDirections.actionProceedWithDefaultAddBottomSheetToPaymentDetailsFragment(
                address, navArgs.products
            )

        findNavController().navigate(action)
    }

}