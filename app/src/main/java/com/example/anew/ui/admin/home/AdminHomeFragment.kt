package com.example.anew.ui.admin.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.anew.AdminActivity
import com.example.anew.R
import com.example.anew.databinding.AdminFragmentHomeBinding

import com.example.anew.model.Product
import com.example.anew.ui.admin.add.PRODUCT_REF
import com.example.anew.ui.admin.detail.MenuBottomSheetDialogFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminHomeFragment : Fragment(), AdminHomeAdapter.ProductItemClickListener,
    MenuBottomSheetDialogFragment.MenuBottomSheetItemClickListener {

    //private lateinit var adminHomeViewModel: AdminHomeViewModel
    private lateinit var binding: AdminFragmentHomeBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firestoreRecyclerAdapter: AdminHomeAdapter
    private lateinit var menuBottomSheetDialogFragment: MenuBottomSheetDialogFragment


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //homeViewModel =ViewModelProviders.of(this).get(HomeViewModel::class.java)
        //val root = inflater.inflate(R.layout.fragment_home, container, false)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer)

        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.admin_fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance()

        // instance of menuBottomSheetDialogFrag
        menuBottomSheetDialogFragment =
            MenuBottomSheetDialogFragment.newInstance(R.menu.product_bottom_sheet_menu)

        //listener to menuBottomSheetDialogFrag
        menuBottomSheetDialogFragment.setMenuItemClickListener(this)


        with(binding) {

            //query
            val query: Query = firestore.collection(PRODUCT_REF)

            //firestore recycler option
            val options =
                FirestoreRecyclerOptions.Builder<Product>()
                    .setQuery(query, Product::class.java).build()

            // firestore adapter
            firestoreRecyclerAdapter = AdminHomeAdapter(this@AdminHomeFragment, options)

            //setting up recycler view
            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)

                // setting adapter
                adapter = firestoreRecyclerAdapter

            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.admin_home_menu, menu)

    }

    override fun onStart() {
        super.onStart()
        firestoreRecyclerAdapter.startListening()

    }

    override fun onStop() {
        super.onStop()
        firestoreRecyclerAdapter.stopListening()
    }


     private fun deleteProduct(product: Product) {
        val dialogBuilder = MaterialAlertDialogBuilder(activity as AdminActivity)
        with(dialogBuilder) {
            setTitle("Alert")
            setMessage("Do you really want to delete")
            setNeutralButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("DELETE") { dialog, _ ->
                val deleteTask = firestore.collection(PRODUCT_REF).document(product.id).delete()
                deleteTask.addOnSuccessListener {
                    Snackbar.make(
                        binding.root,
                        "product removed successfully",
                        Snackbar.LENGTH_SHORT
                    )
                        .show()
                    dialog.dismiss()
                }.addOnFailureListener {
                    Snackbar.make(
                        binding.root,
                        it.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                }

            }
        }.also {
            it.show()
        }
    }


    private fun moveToDetailsFrag(product: Product) {
        Log.d("mytag", "item has clicked but no effect")

        val action = AdminHomeFragmentDirections.actionNavHomeToDetailFragment(product)
        //findNavController().navigate(R.id.action_nav_home_to_detailFragment)
        findNavController().navigate(action)
    }

    // Product listener from adapter class
    override fun onProductItemClicked(product: Product) {
        moveToDetailsFrag(product)
    }

    override fun onProductItemLongClicked(product: Product) {
        menuBottomSheetDialogFragment
            .show(parentFragmentManager, null)
        menuBottomSheetDialogFragment.setProduct(product)
    }
    // Product listener from adapter class finish


    //bottom sheet menu listener
    override fun onMenuBottomSheetItemClicked(item: MenuItem, product: Product) {
        when (item.itemId) {
            R.id.menu_view -> moveToDetailsFrag(product)
            R.id.menu_delete -> deleteProduct(product)
            else -> Log.d("mytag", "menu item clicked ${item.itemId}")
        }
    }
    //bottom sheet menu listener finish

}