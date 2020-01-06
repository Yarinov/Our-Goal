package com.yarinov.ourgoal.user.auth

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.yarinov.ourgoal.R


class AuthenticationActivity : AppCompatActivity() {

    private var switchAuthMood: TextView? = null

    private var authenticationViewPager: ViewPager? = null
    private var authenticationPagerViewAdapter : AuthenticationPagerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        switchAuthMood = findViewById(R.id.switchAuthMood)
        authenticationViewPager = findViewById(R.id.authenticationViewPager)

        authenticationPagerViewAdapter =
            AuthenticationPagerViewAdapter(
                supportFragmentManager
            )
        authenticationPagerViewAdapter!!.addFragment(LoginFragment())
        authenticationPagerViewAdapter!!.addFragment(RegistrationFragment())

        authenticationViewPager!!.adapter = authenticationPagerViewAdapter


        switchAuthMood!!.setOnClickListener {
            authenticationPagerViewAdapter
        }

    }


}
