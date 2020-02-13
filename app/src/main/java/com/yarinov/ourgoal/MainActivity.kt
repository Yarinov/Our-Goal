package com.yarinov.ourgoal

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.yarinov.ourgoal.connection.ConnectionFragment
import com.yarinov.ourgoal.feed.HomeFragment
import com.yarinov.ourgoal.goal.mygoals.MyGoalsFragment
import com.yarinov.ourgoal.search.SearchFragment
import com.yarinov.ourgoal.user.auth.AuthenticationActivity


class MainActivity : AppCompatActivity() {

    private var mainViewPager: ViewPager? = null
    private var mainTabsPagerViewAdapter: MainTabsPagerViewAdapter? = null
    private var mainTabLayout: TabLayout? = null


    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainTabLayout = findViewById(R.id.mainTabLayout)
        mainViewPager = findViewById(R.id.mainViewPager)

        mainTabsPagerViewAdapter =
            MainTabsPagerViewAdapter(supportFragmentManager)

        mainTabsPagerViewAdapter!!.addFragment(SearchFragment(), "Search")
        mainTabsPagerViewAdapter!!.addFragment(HomeFragment(), "Our Goal")
        mainTabsPagerViewAdapter!!.addFragment(MyGoalsFragment(), "My Goals")
        mainTabsPagerViewAdapter!!.addFragment(ConnectionFragment(), "Connections")


        mainViewPager!!.adapter = mainTabsPagerViewAdapter
        mainTabLayout!!.setupWithViewPager(mainViewPager)
        setupTabIcons()
    }

    override fun onStart() {
        super.onStart()


        currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            val moveToAuthenticationActivityIntent =
                Intent(this, AuthenticationActivity::class.java)
            startActivity(moveToAuthenticationActivityIntent)
            finish()
        }

    }

    private fun setupTabIcons() {
        mainTabLayout!!.getTabAt(0)!!.setIcon(R.drawable.search_black_ic)
        mainTabLayout!!.getTabAt(1)!!.setIcon(R.drawable.home_black_ic).select()
        mainTabLayout!!.getTabAt(2)!!.setIcon(R.drawable.my_goals_black_ic)
        mainTabLayout!!.getTabAt(3)!!.setIcon(R.drawable.friends_black_ic)
    }

    override fun onBackPressed() {
        minimizeApp()
    }

    private fun minimizeApp() {

        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }


}
