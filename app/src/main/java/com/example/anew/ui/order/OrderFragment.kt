package com.example.anew.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.anew.R
import com.example.anew.databinding.FragmentOrderBinding
import com.example.anew.model.Order
import com.example.anew.ui.intialSetup.USER_REF
import com.example.anew.ui.orderPlaced.ORDER_PLACED
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class OrderFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var orderAdapter: OrderAdapter
    private val userId = Firebase.auth.currentUser?.uid!!
    private lateinit var binding: FragmentOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)
        firestore = FirebaseFirestore.getInstance()


        //query
        val query: Query = firestore.collection(USER_REF)
            .document(userId)
            .collection(ORDER_PLACED)


        //fire store recycler option
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Order>()
            .setQuery(query, Order::class.java)
            .build()

        orderAdapter = OrderAdapter(firestoreRecyclerOptions)
        with(binding) {
            orderRecyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = orderAdapter
            }

        }

        return binding.root
    }


    override fun onStart() {
        super.onStart()
        orderAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        orderAdapter.stopListening()
    }
}


