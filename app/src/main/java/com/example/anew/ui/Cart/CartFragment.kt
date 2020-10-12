package com.example.anew.ui.Cart
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        with(root){
            val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
            val cartAdapter: CartAdapter = CartAdapter(this.context,getFakeData())
            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = cartAdapter
                // layoutManager = LinearLayoutManager(this.context)

            }
        }


        return root
    }

    private fun getFakeData(): MutableList<CartData> {
        val ls = mutableListOf<CartData>()
        for (i in 1..20){
            ls.add(CartData("crocine","50Rs",1,50))
        }
        return ls
    }
}