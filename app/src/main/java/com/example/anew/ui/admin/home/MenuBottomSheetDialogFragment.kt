package com.example.anew.ui.admin.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MenuRes
import com.example.anew.R
import com.example.anew.model.Product
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView

/**
 * A bottom sheet dialog for displaying a simple list of action items.
 */
class MenuBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var navigationView: NavigationView
    private var menuBottomSheetItemClickListener: MenuBottomSheetItemClickListener? = null
    private lateinit var product: Product

    @MenuRes
    private var menuResId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuResId = arguments?.getInt(KEY_MENU_RES_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.menu_bottom_sheet_layout,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navigationView = view.findViewById(R.id.navigation_view)
        navigationView.inflateMenu(menuResId)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            dismiss()
            menuBottomSheetItemClickListener?.apply {
                onMenuBottomSheetItemClicked(menuItem,product)
            }
            true
        }
    }

    fun setMenuItemClickListener(listener: MenuBottomSheetItemClickListener) {
        menuBottomSheetItemClickListener = listener
    }

    fun setProduct(product: Product){
        this.product = product
    }
    companion object {

        private const val KEY_MENU_RES_ID = "MenuBottomSheetDialogFragment_menuResId"

        fun newInstance(@MenuRes menuResId: Int): MenuBottomSheetDialogFragment {
            val fragment = MenuBottomSheetDialogFragment()
            val bundle = Bundle().apply {
                putInt(KEY_MENU_RES_ID, menuResId)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    interface MenuBottomSheetItemClickListener {

        fun onMenuBottomSheetItemClicked(item: MenuItem, product: Product)

    }
}

