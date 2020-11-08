package com.example.anew.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.anew.MainActivity
import com.example.anew.R
import com.example.anew.databinding.FragmentHomeBinding
import com.example.anew.model.PRODUCT_NAME
import com.example.anew.model.Product
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.admin.home.AdminHomeAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HomeFragment : Fragment(), AdminHomeAdapter.ProductItemClickListener {

    // private lateinit var adminHomeViewModel: AdminHomeViewModel

    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: FragmentHomeBinding
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

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //query
        val query: Query = firestore.collection(PRODUCT_REF).orderBy("name")

        //fire store recycler option
        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Product>()
            .setQuery(query, Product::class.java)
            .build()

        homeAdapter = HomeAdapter(firestoreRecyclerOptions, this)
        with(binding) {
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
        inflater.inflate(R.menu.home_menu, menu)

        val seachItem = menu.findItem(R.id.home_search)
        val searchView = seachItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    //query
                    val query: Query = firestore.collection(PRODUCT_REF).orderBy(PRODUCT_NAME)
                        .startAt(query)
                        .endAt(query + "\uf8ff")

                    //fire store recycler option
                    FirestoreRecyclerOptions.Builder<Product>()
                        .setQuery(query, Product::class.java)
                        .build()
                        .also {
                            homeAdapter.updateOptions(it)
                        }



                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (newText.isNotEmpty()){
                        binding.recyclerView.scrollToPosition(0)
                        //query
                        val query: Query = firestore.collection(PRODUCT_REF).orderBy(PRODUCT_NAME)
                            .startAt(newText)
                            .endAt(newText + "\uf8ff")

                        //fire store recycler option
                        FirestoreRecyclerOptions.Builder<Product>()
                            .setQuery(query, Product::class.java)
                            .build()
                            .also {
                                homeAdapter.updateOptions(it)
                            }

                    }
                    else{
                        //query
                        val query: Query = firestore.collection(PRODUCT_REF)

                        //fire store recycler option
                        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Product>()
                            .setQuery(query, Product::class.java)
                            .build()
                        homeAdapter.updateOptions(firestoreRecyclerOptions)
                    }
                }
                Log.d("mytag","$newText")
                return true
            }

        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home_profile -> navigateToProfile()
            R.id.home_about -> navigateToAbout()
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun navigateToAbout(): Boolean {
        with(activity as MainActivity) {
            val menuId = navView.menu.findItem(R.id.nav_about)
            return NavigationUI.onNavDestinationSelected(menuId, findNavController())
        }
    }

    private fun navigateToProfile(): Boolean {
        with(activity as MainActivity) {
            val menuId = navView.menu.findItem(R.id.nav_profile)
            return NavigationUI.onNavDestinationSelected(menuId, findNavController())
        }
    }


    override fun onProductItemClicked(product: Product) {
        moveToDetailFragment(product)
    }

    override fun onProductItemLongClicked(product: Product) {
        TODO("Not yet implemented")
    }

    private fun moveToDetailFragment(product: Product) {
        val action = HomeFragmentDirections.actionNavHomeToMedDetailsFragment(product)
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()

        homeAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        homeAdapter.stopListening()
    }


}