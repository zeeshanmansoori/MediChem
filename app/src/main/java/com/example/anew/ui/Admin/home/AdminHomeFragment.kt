package com.example.anew.ui.Admin.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.ui.home.HomeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminHomeFragment : Fragment() {

    private lateinit var adminHomeViewModel: AdminHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //homeViewModel =ViewModelProviders.of(this).get(HomeViewModel::class.java)
        //val root = inflater.inflate(R.layout.fragment_home, container, false)
        //homeViewModel.text.observe(viewLifecycleOwner, Observer)
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.admin_fragment_home,container,false)
        root.apply {
            val recyclerView:RecyclerView = findViewById(R.id.recycler_view)
                val AdminHomeAdapter: AdminHomeAdapter = AdminHomeAdapter(this.context,getFakeData())
            recyclerView.apply {
                setHasFixedSize(true)
                setItemViewCacheSize(20)
                adapter = AdminHomeAdapter
               // layoutManager = LinearLayoutManager(this.context)

            }
        }
        return root
    }

    private fun getFakeData(): MutableList<AdminhomeData> {
            val ls = mutableListOf<AdminhomeData>()
            for (i in 1..20){
                ls.add(AdminhomeData("crocine","50Rs",1))
            }
        return ls
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu,menu)

    }
}