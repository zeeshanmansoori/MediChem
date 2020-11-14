package com.example.anew.ui.admin.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.example.anew.R
import com.example.anew.model.Order
import com.example.anew.ui.order.OrderAdapter
import com.example.anew.ui.orderPlaced.ORDER_PLACED
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class adminOrderFragment : Fragment() ,OrderAdapter.OrderItemClickListener{

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var adminOrderAdapter: AdminOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        firebaseFirestore = FirebaseFirestore.getInstance()
        val root = inflater.inflate(R.layout.admin_fragment_order, container, false)
        val query: Query = firebaseFirestore.collection(ORDER_PLACED)
        val options = FirestoreRecyclerOptions
            .Builder<Order>().setQuery(query, Order::class.java).build()
        adminOrderAdapter = AdminOrderAdapter(options,this)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            adapter = adminOrderAdapter
        }
    }


    override fun onStart() {
        super.onStart()
        adminOrderAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adminOrderAdapter.stopListening()
    }

    override fun onExpansionButtonClicked(
        view: View,
        expandableView: LinearLayoutCompat,
        cardView: CardView
    ) {
        if (expandableView.visibility==View.GONE){
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            expandableView.visibility = View.VISIBLE
            (view as AppCompatImageButton).setImageResource(R.drawable.ic_up_arrow)
        }else{
            TransitionManager.beginDelayedTransition(cardView, AutoTransition())
            expandableView.visibility = View.GONE
            (view as AppCompatImageButton).setImageResource(R.drawable.ic_down_arrow)
        }
    }
}