package com.example.ndvi.adapter

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ndvi.TrueColorFragment
import com.example.ndvi.VegIndexFragment

class TabAdapter(fragmentActivity: Fragment) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2



    override fun createFragment(position: Int): Fragment {
        return when(position){
            0-> VegIndexFragment()
            1-> TrueColorFragment()
            else -> VegIndexFragment()
        }
    }
}