package com.example.anew.ui.admin.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.SingleImageLayoutBinding

class PagerAdapter(val uriList: MutableList<String>): RecyclerView.Adapter<PagerAdapter.PagerHolder>(){


    inner class PagerHolder(val binding: SingleImageLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int){
            binding.uri = uriList[position]
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        val binding:SingleImageLayoutBinding = DataBindingUtil
            .inflate(LayoutInflater.from(parent.context), R.layout.single_image_layout,parent,false)

        return PagerHolder(binding)
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        if (position!=RecyclerView.NO_POSITION)
            holder.bind(position)

    }

    override fun getItemCount(): Int {
        return uriList.size
    }


}