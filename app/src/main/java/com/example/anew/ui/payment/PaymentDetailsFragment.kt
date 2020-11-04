package com.example.anew.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentPaymentDetailsBinding
import com.example.anew.model.Order
import com.example.anew.model.PaymentProduct
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.ui.orderPlaced.ORDER_PLACED
import com.example.anew.utils.CustomLoadingDialog
import com.example.anew.utils.MyUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class PaymentDetailsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentPaymentDetailsBinding
    private val navArgs: PaymentDetailsFragmentArgs by navArgs()

    private val userId = Firebase.auth.currentUser?.uid!!

    private var snackbar: Snackbar? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_payment_details, container, false)

        binding.address = navArgs.address

        binding.paymentProduct = PaymentProduct(navArgs.products)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.payNowBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        navigateToConfirmation()
    }

    private fun navigateToConfirmation() {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val order = Order(
            address = navArgs.address,
            product = navArgs.products.toMutableList(),
            dateAdded = MyUtil.getDate()
        )
        FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
            .collection(ORDER_PLACED)
            .document()
            .apply {

                set(order.also {
                    it.orderId = this.id
                })
                    .addOnSuccessListener {

                        FirebaseFirestore.getInstance().collection(ORDER_PLACED)
                            .document(order.orderId)
                            .set(order)
                            .addOnSuccessListener {
                                dialog.dismissDialog()
                                findNavController().navigate(R.id.action_paymentDetailsFragment_to_orderPlacedFragment)
                                Log.d("paymentDetailsFragment", "placed")
                            }
                            .addOnFailureListener {
                                dialog.dismissDialog()
                                Log.d("paymentDetailsFragment", "placed")
                            }
                    }
                    .addOnFailureListener {
                        dialog.dismissDialog()
                        snackbar = Snackbar.make(
                            binding.root,
                            "some error while placing order",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("RETRY") {
                                navigateToConfirmation()
                            }
                        snackbar?.show()
                    }
            }


    }


    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

}