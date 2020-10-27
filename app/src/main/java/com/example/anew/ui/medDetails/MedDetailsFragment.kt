package com.example.anew.ui.medDetails

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.anew.R
import com.example.anew.databinding.FragmentMedDetailsBinding
import com.example.anew.model.CartProduct
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

    val args: MedDetailsFragmentArgs by navArgs()

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

        // enabling optn menu
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onImageClicked(view: View) {
        when (view.id) {
            binding.addToBag.id -> addToCart()
            binding.buyNow.id -> byNow()
        }
    }

    private fun byNow() {
        TODO("Not yet implemented")
    }

    private fun addToCart() {
        val userId = mAuth.currentUser?.uid

        val cartProduct = CartProduct(args.product, MyUtil.getDate())
        val dialog = CustomLoadingDialog(activity as AppCompatActivity)
        userId?.let { userid ->
            dialog.startDialog()
            val insertTask = firestore.collection(USER_REF).document(userid)
                .collection(PRODUCT_REF).document(cartProduct.product.id).set(
                    cartProduct
                )

            insertTask.addOnSuccessListener {
                Snackbar.make(binding.root, "successfully added to bag", Snackbar.LENGTH_SHORT)
                    .show()
                dialog.dismissDialog()
            }
            insertTask.addOnFailureListener {
                Snackbar.make(binding.root, "failed to add", Snackbar.LENGTH_SHORT).show()
                dialog.dismissDialog()
                Log.d("fail", "${it}")
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.med_details_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.med_details_bag -> moveToCart()
            R.id.buy_now -> buyNow()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun buyNow(): Boolean {
        return true
    }

    private fun moveToCart():Boolean {
        findNavController().navigate(R.id.action_medDetailsFragment_to_nav_cart)
        return true
    }

}