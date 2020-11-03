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


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_order, container, false)
        with(root){
            val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)

                // layoutManager = LinearLayoutManager(this.context)

            }
        }

        return root
    }


}