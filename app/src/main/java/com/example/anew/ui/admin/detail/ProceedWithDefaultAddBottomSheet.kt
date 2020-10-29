package com.example.anew.ui.admin.detail

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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class ProceedWithDefaultAddBottomSheet() :
    BottomSheetDialogFragment(), RadioGroup.OnCheckedChangeListener {

    private val navArgs:ProceedWithDefaultAddBottomSheetArgs by navArgs()

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
        binding.product = navArgs.product
        binding.address = navArgs.address
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.buyNow.setOnClickListener {
            navigateToNewAddress()
        }
    }

    private fun navigateToNewAddress() {
        val action = ProceedWithDefaultAddBottomSheetDirections
            .actionProceedWithDefaultAddBottomSheetToNewAddressFragment2(true)
        findNavController().navigate(action)
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when(checkedId){
            binding.newAddressRadioButton.id -> findNavController()
                .navigate(R.id.action_proceedWithDefaultAddBottomSheet_to_newAddressFragment2)


        }
    }


}