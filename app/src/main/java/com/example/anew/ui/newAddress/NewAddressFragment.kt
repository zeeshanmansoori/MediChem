package com.example.anew.ui.newAddress

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentNewAddressBinding
import com.example.anew.model.ADDRESS1
import com.example.anew.model.Address
import com.example.anew.model.USER_ADDRESSES
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NewAddressFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentNewAddressBinding

    private val firestore = FirebaseFirestore.getInstance()

    private val userId = Firebase.auth.currentUser?.uid!!

    private val navArgs: NewAddressFragmentArgs by navArgs()

    private lateinit var state: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_address, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.stateSpinner.adapter =
            ArrayAdapter.createFromResource(
                activity?.baseContext!!, R.array.india_states, android.R.layout.simple_spinner_item
            ).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }
        binding.stateSpinner.onItemSelectedListener = this
        binding.saveAddress.setOnClickListener {
            addNewAddress()
        }


    }


    private fun addNewAddress() {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        with(binding) {
            val city = cityName.text?.trim().toString()
            val locality = locality.text?.trim().toString()
            val buildingName = buildingName.text?.trim().toString()
            val pinCode = pinCode.text?.trim().toString()
            val landMark = landMark.text?.trim().toString()
            val userName = userName.text?.trim().toString()
            val phoneNo = phoneNo.text?.trim().toString()
            val alternatePhoneNo = alternatePhoneNo.text?.trim().toString()

            if (city.isEmpty()) {
                cityName.error = "city can not be empty !"
                cityName.requestFocus()
                return
            }
            if (locality.isEmpty()) {
                binding.locality.error = "locality can not be empty !"
                binding.locality.requestFocus()
                return
            }
            if (buildingName.isEmpty()) {
                binding.buildingName.error = "buildingName can not be empty !"
                binding.buildingName.requestFocus()
                return
            }
            if (pinCode.isEmpty()) {
                binding.pinCode.error = "pincode can not be empty !"
                binding.pinCode.requestFocus()
                return
            }
            if (state.isEmpty()) {
                Snackbar.make(binding.root, "plz select state", Snackbar.LENGTH_SHORT)
                    .show()

                stateSpinner.requestFocus()
                return
            }
            if (userName.isEmpty()) {
                binding.userName.error = "userName can not be empty !"
                binding.userName.requestFocus()
                return
            }

            if (phoneNo.isEmpty()) {
                binding.phoneNo.error = "phoneNo can not be empty !"
                binding.phoneNo.requestFocus()
                return
            }

            if (phoneNo.length<10) {
                binding.phoneNo.error = "phone no should have 10 characters"
                binding.phoneNo.requestFocus()
                return
            }


            if (alternatePhoneNo.isEmpty()) {
                binding.phoneNo.error = "phoneNo can not be empty !"
                binding.phoneNo.requestFocus()
                return
            }

            if (alternatePhoneNo.length<10) {
                binding.phoneNo.error = "phone no should have 10 characters"
                binding.phoneNo.requestFocus()
                return
            }
            val address = Address(
                city,
                locality,
                buildingName,
                pinCode,
                state,
                landMark,
                userName,
                phoneNo,
                alternatePhoneNo
            )

            if (navArgs.fromBottomSheet){
                navigateToPayment(address)
            }
            else{
                dialog.startDialog()
                firestore.collection(USER_REF).document(userId)
                    .collection(USER_ADDRESSES)
                    .document(ADDRESS1)
                    .set(address)
                    .addOnSuccessListener {
                        Snackbar.make(
                            binding.root,
                            "address added successfully !",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        dialog.dismissDialog()
                        navigateToPayment(address)
                    }
            }

        }
    }


    private fun navigateToPayment(address: Address) {
        NewAddressFragmentDirections.actionNewAddressFragmentToPaymentDetailsFragment(address,navArgs.products,false).also {
            findNavController().navigate(it)
        }

    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        state = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        state = ""
    }


}