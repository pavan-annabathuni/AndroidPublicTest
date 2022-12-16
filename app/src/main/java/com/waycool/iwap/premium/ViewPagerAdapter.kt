package com.waycool.iwap.premium

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (val item:ArrayList<Fragment>,activity:Fragment):FragmentStateAdapter(activity){
    override fun getItemCount(): Int {
        return item.size
    }

    override fun createFragment(position: Int): Fragment {
       return item[position]
    }

}