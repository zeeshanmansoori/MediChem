package com.example.anew.ui.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.anew.MainActivity
import com.example.anew.R
import com.example.anew.databinding.FragmentCartBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.CART_REF
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.ui.medDetails.MedDetailsFragmentDirections
import com.example.anew.utils.CustomLoadingDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.StringBuilder

class CartFragment : Fragment(), CartAdapter.CartItemClickListener, View.OnClickListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String
    private var snackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth.currentUser?.uid!!

        with(binding) {

            val query: Query =
                firestore.collection(USER_REF).document(userId).collection(CART_REF)
            val options = FirestoreRecyclerOptions.Builder<CartProduct>()
                .setQuery(query, CartProduct::class.java).build()
            cartAdapter = CartAdapter(this@CartFragment, options)


            //setting up recycler view
            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)

                // setting adapter
                adapter = cartAdapter

            }


            proceedToBuy.setOnClickListener(this@CartFragment)
        }
        return binding.root
    }

    private fun removeProductFromCart(cartProduct: CartProduct) {

        val deleteTask = FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
            .collection(CART_REF)
            .document(cartProduct.product.id)
            .delete()
        deleteTask.addOnSuccessListener {

            snackbar = Snackbar.make((activity as MainActivity).drawerLayout, "item removed from bag", Snackbar.LENGTH_SHORT)
            snackbar?.show()
            cartAdapter.totalItemCount -= cartProduct.product.quantity
            cartAdapter.totalPrize -= (cartProduct.product.quantity) * (cartProduct.product.prize)
            onCartItemChange(cartAdapter.totalItemCount, cartAdapter.totalPrize)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.proceedToBuy.id -> buyNow()

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

    private fun navigateToBottomSheet(address: Address) {
        CartFragmentDirections.actionNavCartToProceedWithDefaultAddBottomSheet(
            getAllProductsOfTheCart(),
            address
        )
            .also {
                findNavController().navigate(it)
            }

    }

    private fun navigateToNewAddress() {

        val action = CartFragmentDirections.actionNavCartToNewAddressFragment(
            getAllProductsOfTheCart()
        )
        findNavController().navigate(action)
    }

    private fun getAllProductsOfTheCart(): Array<Product> {
        val ls = mutableListOf<Product>()
        for (i in 0 until cartAdapter.itemCount)
            with(cartAdapter.getItem(i)) {
                ls.add(
                    product
                )
            }
        return ls.toTypedArray()
    }

    override fun onNumberPickerValueChanged(position: Int, oldValue: Int, newValue: Int) {

        if (newValue > oldValue) {
            cartAdapter.totalItemCount += newValue - oldValue
            cartAdapter.totalPrize += (newValue - oldValue) * (cartAdapter.getItem(position).product.prize)
            cartAdapter.getItem(position).product.quantity = newValue
        } else if (oldValue > newValue) {
            cartAdapter.totalItemCount -= oldValue - newValue
            cartAdapter.totalPrize -= (oldValue - newValue) * (cartAdapter.getItem(position).product.prize)
            cartAdapter.getItem(position).product.quantity = newValue
        }
        onCartItemChange(cartAdapter.totalItemCount, cartAdapter.totalPrize)


    }

    override fun onCartItemClicked(view: View, cartProduct: CartProduct) {
        when (view.id) {
            R.id.cart_delete_btn -> removeProductFromCart(cartProduct)
        }
    }

    override fun onCartItemChange(totalItemCount: Int, totalPrize: Double) {
        binding.cartSubTotal.text =
            "subtotal ( ${cartAdapter.totalItemCount} items ) : Rs ${cartAdapter.totalPrize}"
    }


    override fun onStart() {

        super.onStart()
        cartAdapter.startListening()
    }

    override fun onPause() {
        snackbar?.dismiss()

        if (cartAdapter.itemCount==0)
        {
            super.onPause()
            return
        }
        if (cartAdapter.itemCount > 0) {
            for (position in 0 until cartAdapter.itemCount) {
                firestore.collection(USER_REF).document(userId)
                    .collection(CART_REF)
                    .document(cartAdapter.getItem(position).product.id)
                    .update("product.$QUANTITY", cartAdapter.getItem(position).product.quantity)
                    .addOnSuccessListener { Log.d("CartFragment", "updated quantity ") }

            }
            super.onPause()
        }


    }

    override fun onStop() {
        super.onStop()
        cartAdapter.stopListening()

    }


}