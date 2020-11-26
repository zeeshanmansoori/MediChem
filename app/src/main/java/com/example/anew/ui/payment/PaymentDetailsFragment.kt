package com.example.anew.ui.payment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentPaymentDetailsBinding
import com.example.anew.model.AdminUser
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


const val GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
const val PAYMENT_REQUEST_CODE = 863

class PaymentDetailsFragment : Fragment() {

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
        when (navArgs.isPickFromStoreSelected) {
            true -> {
                binding.googlePayRadioButton.isEnabled = false
                binding.cashOnDevRadioBtn.isChecked = true
                binding.cashOnDevRadioBtn.text = "Pay at Store"
            }
        }

        binding.paymentProduct = PaymentProduct(navArgs.products)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.payNowBtn.setOnClickListener {
            when {
                binding.googlePayRadioButton.isChecked -> payUsingGooglePay(binding.toBePaid.text.toString())
                //binding.paytmRadioButton.isChecked -> payUsingPaytm(binding.toBePaid.text.toString())
                else -> navigateToConfirmation()
            }
        }
    }


    private fun payUsingGooglePay(amount: String = "1") {
        if (MyUtil.isConnectedToInternet(context)) {
            FirebaseFirestore.getInstance().collection(USER_REF)
                .whereEqualTo("admin", true)
                .get()
                .addOnSuccessListener {
                    val adminUser = it.toObjects(AdminUser::class.java).first()
                    Log.d("UPIPAY", "user ${adminUser.toString()}")
                    val uri = Uri.parse("upi://pay").buildUpon()
                        .appendQueryParameter("pa", adminUser.upiId)
                        .appendQueryParameter("pn", adminUser.name)
                        .appendQueryParameter("tn", "paying to ${adminUser.name}")
                        .appendQueryParameter("am", amount)
                        .appendQueryParameter("cu", "INR")
                        .build()


                    Intent(Intent.ACTION_VIEW).apply {
                        data = uri
                        setPackage(GOOGLE_PAY_PACKAGE_NAME)
                    }
                        .also { paymentIntent ->
                            startActivityForResult(paymentIntent, PAYMENT_REQUEST_CODE)
                        }

                }
                .addOnFailureListener {
                    Log.d("UPIPAY", "failed to fetch")
                }

        } else {
            snackbar = Snackbar.make(binding.root, "plz connect to internet", Snackbar.LENGTH_SHORT)
            snackbar?.show()
        }
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
                                navigateToOrderPlaced(order)
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

    private fun navigateToOrderPlaced(order: Order) {
        val action =
            PaymentDetailsFragmentDirections.actionPaymentDetailsFragmentToOrderPlacedFragment(
                order
            )
        findNavController().navigate(action)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK) {
            val trxt = data?.getStringExtra("response")
            Log.d("UPIPAY", "onActivityResult: $trxt")
            val dataList = ArrayList<String>()
            dataList.add(trxt.toString())
            context?.let {
                upiPaymentDataOperation(dataList, it)
            }

        }
    }


    private fun upiPaymentDataOperation(data: ArrayList<String>, context: Context) {
        if (MyUtil.isConnectedToInternet(context)) {
            var str = data[0]
            Log.e("UPIPAY", "upiPaymentDataOperation: $str")
            var paymentCancel = ""
            if (str == "null") str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=".toRegex()).toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].equals("Status", ignoreCase = true)) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0].equals(
                            "ApprovalRefNo",
                            ignoreCase = true
                        ) || equalStr[0].equals(
                            "txnRef", ignoreCase = true
                        )
                    ) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            when {
                status == "success" -> {
                    //Code to handle successful transaction here.
                    Log.e("UPI", "payment successful: $approvalRefNo")
                }
                "Payment cancelled by user." == paymentCancel -> {
                    Log.e("UPI", "Cancelled by user: $approvalRefNo")
                }
                else -> {
                    Log.e("UPI", "failed payment: $approvalRefNo")
                }
            }
        } else {
            Log.e("UPI", "Internet issue: ")
        }
    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

}