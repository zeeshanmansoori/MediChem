package com.example.anew.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R

class OrderFragment : Fragment() {

    private lateinit var orderviewModel: OrderViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        orderviewModel =
                ViewModelProviders.of(this).get(OrderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_order, container, false)
        with(root){
            val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
            val orderAdapter: OrderAdapter = OrderAdapter(this.context,getFakeData())
            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = orderAdapter
                // layoutManager = LinearLayoutManager(this.context)

            }
        }

        return root
    }
    private fun getFakeData(): MutableList<OrderData> {
        val ls = mutableListOf<OrderData>()
        for (i in 1..5){
            ls.add(
                OrderData("15454","crocine","500Rs",1,5,"08-Sep-2020","Successful")
            )
        }
        return ls
    }

}