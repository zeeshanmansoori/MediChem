package com.example.anew.ui.medDetails

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentMedDetailsBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.CART_REF
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.admin.detail.MyImageClickListener
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.example.anew.utils.MyUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MedDetailsFragment : Fragment(), MyImageClickListener {

    private lateinit var binding: FragmentMedDetailsBinding

    private lateinit var firestore: FirebaseFirestore

    private val mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser?.uid!!

    val args: MedDetailsFragmentArgs by navArgs()

    private var snackbar:Snackbar? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestore = FirebaseFirestore.getInstance()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_med_details, container, false)
        //product = args.product
        binding.product = args.product
        binding.listener = this
        binding.productQuantityDropDown.adapter =
            ArrayAdapter.createFromResource(
                activity?.baseContext!!, R.array.product_quantity, R.layout.spinner_item
            ).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }

        // enabling optn menu
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = ArrayAdapter(
            activity?.baseContext!!,
            R.layout.dropdown_menu_popup_item,
            resources.getStringArray(R.array.product_quantity)
        )
        binding.productQuantityDropDown.setAdapter(adapter)
    }

    override fun onImageClicked(view: View) {
        when (view.id) {
            binding.addToBag.id -> addToCart()
            binding.buyNow.id -> buyNow()
        }
    }


    private fun addToCart() {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        val product = args.product.apply {
            quantity = binding.productQuantityDropDown.selectedItem.toString().toInt()
        }

        val cartProduct = CartProduct(
            product,
            MyUtil.getDate()
        )
        val insertTask = firestore.collection(USER_REF).document(userId)
            .collection(CART_REF).document(cartProduct.product.id).set(
                cartProduct
            )

        insertTask.addOnSuccessListener {
            snackbar =Snackbar.make((activity as AppCompatActivity).findViewById(R.id.drawer_layout), "successfully added to bag", Snackbar.LENGTH_SHORT)
                .setAction("CHECK"){
                    navigateToCart()
                }

            snackbar?.show()

            dialog.dismissDialog()
        }
        insertTask.addOnFailureListener {
            snackbar = Snackbar.make(binding.root, "failed to add", Snackbar.LENGTH_SHORT)
            snackbar?.show()
            dialog.dismissDialog()
            Log.d("fail", "${it}")
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.med_details_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.med_details_bag -> navigateToCart()
            R.id.buy_now -> buyNow()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun buyNow(): Boolean {
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        dialog.startDialog()
        firestore.collection(USER_REF).document(mAuth.currentUser?.uid!!).collection(
            USER_ADDRESSES
        ).document(ADDRESS1).get().addOnSuccessListener {
            dialog.dismissDialog()
            if (it.exists()) {
                navigateToBottomSheet(it.toObject(Address::class.java)!!)
            } else {
                navigateToNewAddress()
            }
        }.addOnFailureListener {
            dialog.dismissDialog()
            Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
        }
        return true

    }


    private fun navigateToCart(): Boolean {
        findNavController().navigate(R.id.action_medDetailsFragment_to_nav_cart)
        return true
    }

    private fun navigateToNewAddress() {
        val action = MedDetailsFragmentDirections
            .actionMedDetailsFragmentToNewAddressFragment( products = arrayOf(args.product))
        findNavController().navigate(action)
    }


    private fun navigateToBottomSheet(address: Address) {
        val product = binding.product?.apply {
            quantity = binding.productQuantityDropDown.selectedItem.toString().toInt()
        }!!
        val action =
            MedDetailsFragmentDirections.actionMedDetailsFragmentToProceedWithDefaultAddBottomSheet(
                arrayOf(product), address
            )
        findNavController().navigate(action)

    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

}