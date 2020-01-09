package com.yarinov.ourgoal.feed


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.user.profile.ProfileActivity
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var profileImage: CircleImageView? = null

    var feedRecyclerView: RecyclerView? = null
    var feedAdapter: FeedAdapter? = null

    var currentUser: FirebaseUser? = null

    var currentFeedUsersIdList: ArrayList<String>? = null
    var currentFeedGoalsList: ArrayList<Goal>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentUser = FirebaseAuth.getInstance().currentUser

        // Inflate the layout for this fragment
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)

        profileImage = homeView.findViewById(R.id.profile_image)
        feedRecyclerView = homeView.findViewById(R.id.feedRecyclerView)

        currentFeedUsersIdList = ArrayList()
        currentFeedGoalsList = ArrayList()

        feedAdapter = context?.let { FeedAdapter(it, currentFeedGoalsList!!, currentUser!!.uid) }

        feedRecyclerView!!.adapter = feedAdapter
        feedRecyclerView!!.layoutManager = LinearLayoutManager(context)

        profileImage!!.setOnClickListener {
            var moveToMyProfile = Intent(context, ProfileActivity::class.java)
            moveToMyProfile.putExtra("userId", FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(moveToMyProfile)
        }

        getFeed()
        return homeView
    }

    private fun getFeed() {

        currentFeedUsersIdList!!.clear()

        val currentUserConnectionDB =
            FirebaseDatabase.getInstance()
                .reference.child("connections/${currentUser!!.uid}/friends")

        val getAllFriendsIdListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                //Add all current user friends
                for (userId in p0.children)
                    currentFeedUsersIdList!!.add(userId.key.toString())

                currentFeedUsersIdList!!.add(currentUser!!.uid)

                getAllPosts()
            }

        }

        currentUserConnectionDB.addListenerForSingleValueEvent(getAllFriendsIdListener)
    }

    private fun getAllPosts() {

        currentFeedGoalsList!!.clear()

        //Get each user's goals
        for (userId in currentFeedUsersIdList!!){

            val userGoalsDB =
                FirebaseDatabase.getInstance()
                    .reference.child("goals/$userId")

            val getAllUserGoalsListener = object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    for (goal in p0.children){

                        val userGoalMap = goal.value as HashMap<*, *>
                        val userGoal = Goal(userGoalMap["goalId"].toString(),
                            userGoalMap["goalTitle"].toString(),
                            userGoalMap["goalDescription"].toString(),
                            userGoalMap["goalProgress"] as Long,
                            userGoalMap["goalSteps"] as Long,
                            userGoalMap["commentSectionId"].toString(),
                            userGoalMap["goalStatus"].toString(),
                            userGoalMap["datePosted"].toString(),
                            userGoalMap["userId"].toString())

                        currentFeedGoalsList!!.add(userGoal)
                    }

                    feedAdapter!!.sortByAsc()
                    feedAdapter!!.notifyDataSetChanged()
                }

            }


            userGoalsDB.addListenerForSingleValueEvent(getAllUserGoalsListener)
        }
    }


}
