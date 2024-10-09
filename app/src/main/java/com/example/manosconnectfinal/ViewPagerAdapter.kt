package com.example.manosconnectfinal

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MisServiciosFragment()
            1 -> ServiciosReservadosFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}
