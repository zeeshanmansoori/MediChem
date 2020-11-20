package com.example.anew.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.anew.IS_DARK_MODE_ENABLED
import com.example.anew.R
import com.example.anew.databinding.FragmentSettingsBinding
import io.paperdb.Paper

class SettingsFragment : Fragment() {

    private lateinit var binding:FragmentSettingsBinding
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false)
            binding.nightModeSwitch.isChecked = getNightMode()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Paper.book().write(IS_DARK_MODE_ENABLED,isChecked)
        }

    }


    private fun getNightMode() = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
}