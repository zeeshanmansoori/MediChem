package com.example.anew.ui.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentCartBinding
import com.example.anew.model.*
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.intialSetup.USER_REF
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CartFragment : Fragment(), CartAdapter.CartItemClickListener, View.OnClickListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        firestore = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        val userId = mAuth.currentUser?.uid

        with(binding) {
            userId?.let {
                val query: Query =
                    firestore.collection(USER_REF).document(userId).collection(PRODUCT_REF)
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
        cartProduct: CartProduct,
        oldValue: Int,
        newValue: Int
    ) {
        if (newValue > oldValue) {
            cartAdapter.totalItemCount += newValue - oldValue
            cartAdapter.totalPrize += (newValue - oldValue) * (cartProduct.product.prize)
        } else if (oldValue > newValue) {
            cartAdapter.totalItemCount -= oldValue - newValue
            cartAdapter.totalPrize -= (oldValue - newValue) * (cartProduct.product.prize)
        }
        onCartItemChange(cartAdapter.totalItemCount,cartAdapter.totalPrize)

        (FirebaseAuth.getInstance().currentUser?.uid)?.let { userId ->
            val updateTask = FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
                .collection(PRODUCT_REF)
                .document(cartProduct.product.id)
                .update(QUANTITY,newValue)

            updateTask.addOnSuccessListener {
                if (newValue > oldValue) {
                    cartAdapter.totalItemCount += newValue - oldValue
                    cartAdapter.totalPrize += (newValue - oldValue) * (cartProduct.product.prize)
                } else if (oldValue > newValue) {
                    cartAdapter.totalItemCount -= oldValue - newValue
                    cartAdapter.totalPrize -= (oldValue - newValue) * (cartProduct.product.prize)
                }
                onCartItemChange(cartAdapter.totalItemCount, cartAdapter.totalPrize)
            }
        }

    }


    private fun removeProductFromCart(cartProduct: CartProduct) {
        (FirebaseAuth.getInstance().currentUser?.uid)?.let { userId ->
            val deleteTask = FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
                .collection(PRODUCT_REF)
                .document(cartProduct.product.id)
                .delete()
            deleteTask.addOnSuccessListener {
                Log.d("mytag", "product removed from cart")
                cartAdapter.totalItemCount -= cartProduct.product.quantity
                cartAdapter.totalPrize -= (cartProduct.product.quantity) * (cartProduct.product.prize)
                onCartItemChange(cartAdapter.totalItemCount, cartAdapter.totalPrize)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.proceedToBuy.id ->{
                firestore.collection(USER_REF).document(mAuth.currentUser?.uid!!).collection(
                    USER_ADDRESSES
                ).document(ADDRESS1).get().addOnSuccessListener {
                    if (it.exists()) {
                        navigateToPayment()
                    } else {
                        navigateToNewAddress()
                    }
                }.addOnFailureListener {
                    Toast.makeText(context, it.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun navigateToPayment(){
        findNavController().navigate(R.id.action_nav_cart_to_paymentDetailsFragment)
    }

    private fun navigateToNewAddress(){
        val ls = mutableListOf<Product>()
        for (i in 0 until cartAdapter.itemCount)
            ls.add(cartAdapter.getItem(i).product)
        val action = CartFragmentDirections.actionNavCartToNewAddressFragment(true,
            ls.toTypedArray())
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        cartAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        cartAdapter.stopListening()
    }
}