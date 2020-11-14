package com.example.anew.utils

import android.content.Intent
import android.net.Uri
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
import com.example.anew.model.AdminDetails
import com.example.anew.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ProceedWithDefaultAddBottomSheet :
    BottomSheetDialogFragment(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private val navArgs: ProceedWithDefaultAddBottomSheetArgs by navArgs()

    private lateinit var binding: ProceedWithDefaultAddBottomSheetLayoutBinding

    private var adminAddress = Address()



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

        binding.adminDetails = AdminDetails(
            Address(
                "mumbai",
                "andheri east",
                pinCode = "400058",
                state = "Maharashtra",
                buildingName = "aklnfd",
                landMark = "opposite metro station"

            ),
            User(phoneNo = "8975676576")
        )
        adminAddress = navArgs.address
        binding.address = adminAddress

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener(this)

        binding.buyNow.setOnClickListener {
            navigateToPayment()
        }

        binding.getDirection.setOnClickListener(this)

        binding.phoneNo.setOnClickListener(this)
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
                navArgs.products, true
            )
        findNavController().navigate(action)
    }

    private fun navigateToPayment() {

        var address = when (binding.radioGroup.checkedRadioButtonId) {

            binding.pickFromStoreRadioButton.id -> {
                adminAddress
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


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.phoneNo.id -> makeCall()
            binding.getDirection.id -> openMap()
        }
    }

    private fun openMap() {
        Intent(
            Intent.ACTION_VIEW, Uri.parse("geo:19.1198242,72.8443423?z=17")
        ).apply {
            this.`package` = "com.google.android.apps.maps"
        }
            .also {

                startActivity(it)
            }


    }

    private fun makeCall() {

        val u: Uri = Uri.parse("tel:" + binding.phoneNo.text.toString())
        Intent(Intent.ACTION_DIAL, u).also {
            startActivity(it)
        }
    }
}