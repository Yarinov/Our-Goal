package com.yarinov.ourgoal

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class MainTabsPagerViewAdapter(fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!) {

    private val fragmentList: MutableList<Fragment> =
        ArrayList()
    private val fragmentListTitle: MutableList<String> =
        ArrayList()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }


    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        fragmentListTitle.add(title)
    }
}