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
import com.example.anew.R
import com.example.anew.databinding.FragmentCartBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.CART_REF
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.utils.CustomLoadingDialog
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CartFragment : Fragment(), CartAdapter.CartItemClickListener, View.OnClickListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String
    private var snackbar:Snackbar? = null

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


    override fun onCartItemClicked(view: View, cartProduct: CartProduct) {
        when (view.id) {
            R.id.cart_delete_btn -> removeProductFromCart(cartProduct)
        }
    }

    override fun onCartItemChange(totalItemCount: Int, totalPrize: Double) {
        binding.cartSubTotal.text =
            "subtotal ( ${cartAdapter.totalItemCount} items ) : Rs ${cartAdapter.totalPrize}"
    }

    override fun onNumberPickerValueChanged(
        position:Int,
        oldValue: Int,
        newValue: Int
    ) {

        if (newValue > oldValue) {
            cartAdapter.totalItemCount += newValue - oldValue
            cartAdapter.totalPrize += (newValue - oldValue) * (cartAdapter.getItem(position).prize)
            cartAdapter.getItem(position).quantity = newValue
        } else if (oldValue > newValue) {
            cartAdapter.totalItemCount -= oldValue - newValue
            cartAdapter.totalPrize -= (oldValue - newValue) * (cartAdapter.getItem(position).prize)
            cartAdapter.getItem(position).quantity = newValue
        }
        onCartItemChange(cartAdapter.totalItemCount, cartAdapter.totalPrize)


    }


    private fun removeProductFromCart(cartProduct: CartProduct) {

        val deleteTask = FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
            .collection(CART_REF)
            .document(cartProduct.id)
            .delete()
        deleteTask.addOnSuccessListener {

            snackbar = Snackbar.make(binding.root,"item removed from bag",Snackbar.LENGTH_SHORT)
            snackbar?.show()
            cartAdapter.totalItemCount -= cartProduct.quantity
            cartAdapter.totalPrize -= (cartProduct.quantity) * (cartProduct.prize)
            onCartItemChange(cartAdapter.totalItemCount, cartAdapter.totalPrize)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.proceedToBuy.id -> {
                val dialog = CustomLoadingDialog(activity as AppCompatActivity)
                dialog.startDialog()
                firestore.collection(USER_REF).document(userId).collection(
                    USER_ADDRESSES
                ).document(ADDRESS1).get().addOnSuccessListener {
                    dialog.dismissDialog()
                    if (it.exists()) {
                        navigateToPayment(it.toObject(Address::class.java)!!)
                    } else {
                        navigateToNewAddress()
                    }
                }.addOnFailureListener {
                    dialog.dismissDialog()
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun navigateToPayment(address: Address) {


        val action = CartFragmentDirections.actionNavCartToPaymentDetailsFragment(
            address,
            getAllProductsOfTheCart()
        )
        findNavController().navigate(action)

    }

    private fun navigateToNewAddress() {

        val action = CartFragmentDirections.actionNavCartToNewAddressFragment(
            true,
            getAllProductsOfTheCart()
        )
        findNavController().navigate(action)
    }

    private fun getAllProductsOfTheCart(): Array<Product> {
        val ls = mutableListOf<Product>()
        for (i in 0 until cartAdapter.itemCount)
            with(cartAdapter.getItem(i)){
                ls.add(Product(id,name,description,expDate, quantity, prize, manName, image1, image2, image3, image4))
            }
        return ls.toTypedArray()
    }

    override fun onStart() {
        super.onStart()
        cartAdapter.startListening()
    }

    override fun onPause() {

        if (cartAdapter.itemCount==0) {
            super.onPause()
            snackbar?.dismiss()
            return
        }
        else {

            for (position in 0 until cartAdapter.itemCount) {
//                Log.d("mycart","id ${cartAdapter.getItem(0).id}")
//                Log.d("mycart","quantity ${cartAdapter.getItem(0).quantity}")

                firestore.collection(USER_REF).document(userId)
                    .collection(CART_REF)
                    .document(cartAdapter.getItem(position).id)
                    .update(QUANTITY,cartAdapter.getItem(position).quantity)
                    .addOnSuccessListener { Log.d("mycart","updated quantity ${5}") }

            }

        }
        snackbar?.dismiss()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        cartAdapter.stopListening()

    }


}