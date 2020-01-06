package com.yarinov.ourgoal


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.yarinov.ourgoal.user.profile.ProfileActivity
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var profileImage: CircleImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)

        profileImage = homeView.findViewById(R.id.profile_image)

        profileImage!!.setOnClickListener {
            var moveToMyProfile = Intent(context, ProfileActivity::class.java)
            moveToMyProfile.putExtra("userId", FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(moveToMyProfile)
        }

        return homeView
    }




}
