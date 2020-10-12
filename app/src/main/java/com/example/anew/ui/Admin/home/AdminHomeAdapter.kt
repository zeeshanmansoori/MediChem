package com.example.anew.ui.Admin.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R

class AdminHomeAdapter(private val context: Context, private val adminhomeMedicineList:MutableList<AdminhomeData>)
    : RecyclerView.Adapter<HomeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.admin_update_single_item_layout, parent, false)
        return HomeHolder(view)
    }

    override fun onBindViewHolder(holder: HomeHolder, position: Int) {
        val element = adminhomeMedicineList[position]

    }

    override fun getItemCount() = adminhomeMedicineList.size


}

class HomeHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    var medicine_image: ImageView = itemView.findViewById(R.id.medicine_image)
//    var medicine_name: TextView = itemView.findViewById(R.id.medicine_name)
//    var medicine_prize: TextView = itemView.findViewById(R.id.medicine_prize)
}