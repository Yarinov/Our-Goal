package com.yarinov.ourgoal.user.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AuthenticationPagerViewAdapter(fm: FragmentManager?) :
    FragmentPagerAdapter(fm!!) {

    private val fragmentList: MutableList<Fragment> =
        ArrayList()


    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }


    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }
}