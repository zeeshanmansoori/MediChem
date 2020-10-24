package com.example.anew.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anew.R
import com.example.anew.databinding.FragmentHomeBinding
import com.example.anew.model.Product
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.admin.home.AdminHomeAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment(),AdminHomeAdapter.ProductItemClickListener {

   // private lateinit var adminHomeViewModel: AdminHomeViewModel

    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding:FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //homeViewModel =ViewModelProviders.of(this).get(HomeViewModel::class.java)
        //val root = inflater.inflate(R.layout.fragment_home, container, false)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer)
        setHasOptionsMenu(true)

        firestore = FirebaseFirestore.getInstance()

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)

        //query
        val query:Query = firestore.collection(PRODUCT_REF)

        //fire store recycler option
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query,Product::class.java)
            .build()

        homeAdapter = HomeAdapter(firestoreRecyclerOptions,this)
        with(binding){
            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = homeAdapter
            }
            fab.setOnClickListener {
                findNavController().navigate(R.id.action_nav_home_to_nav_cart)
            }

        }

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu,menu)

    }

    override fun onStart() {
        super.onStart()

        homeAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        homeAdapter.stopListening()
    }

    override fun onProductItemClicked(product: Product) {
        moveToDetailFragment(product)
    }

    override fun onProductItemLongClicked(product: Product) {
        TODO("Not yet implemented")
    }

    fun moveToDetailFragment(product: Product){
            val action =  HomeFragmentDirections.actionNavHomeToMedDetailsFragment(product)
            findNavController().navigate(action)
    }

}