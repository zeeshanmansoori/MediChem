package com.example.anew.ui.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.FragmentCartBinding
import com.example.anew.model.CartProduct
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.intialSetup.USER_REF
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class CartFragment : Fragment(), CartAdapter.CartItemClickListener {

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
                cartAdapter = CartAdapter(this@CartFragment,options)


                //setting up recycler view
                recyclerView.apply {
                    setHasFixedSize(true)
                    setItemViewCacheSize(20)

                    // setting adapter
                    adapter = cartAdapter

                }
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        cartAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        cartAdapter.stopListening()
    }

    override fun onCartItemClicked(view: View, cartProduct: CartProduct) {
        when(view.id){
            R.id.cart_delete_btn -> removeProductFromCart(cartProduct)

        }
    }

    private fun removeProductFromCart(cartProduct: CartProduct){
        (FirebaseAuth.getInstance().currentUser?.uid)?.let {
            userId ->
            val deleteTask =FirebaseFirestore.getInstance().collection(USER_REF).document(userId)
                .collection(PRODUCT_REF)
                .document(cartProduct.product.id)
                .delete()
            deleteTask.addOnSuccessListener {
                Snackbar.make(binding.root,"product has been removed from bag",Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}