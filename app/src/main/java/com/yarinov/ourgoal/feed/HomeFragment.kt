package com.yarinov.ourgoal.feed


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.user.profile.ProfileActivity
import de.hdodenhof.circleimageview.CircleImageView


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    var profileImage: CircleImageView? = null
    var userLogoutIcon: ImageView? = null

    var feedRecyclerView: RecyclerView? = null
    var feedAdapter: FeedAdapter? = null

    var currentUser: FirebaseUser? = null

    var currentFeedUsersIdList: ArrayList<String>? = null
    var currentFeedGoalsList: ArrayList<Goal>? = null

    var swipeContainer: SwipeRefreshLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        currentUser = FirebaseAuth.getInstance().currentUser

        // Inflate the layout for this fragment
        val homeView = inflater.inflate(R.layout.fragment_home, container, false)

        profileImage = homeView.findViewById(R.id.profileImage)
        feedRecyclerView = homeView.findViewById(R.id.feedRecyclerView)
        swipeContainer = homeView.findViewById(R.id.swipeContainer)
        userLogoutIcon = homeView.findViewById(R.id.userLogoutIcon)

        currentFeedUsersIdList = ArrayList()
        currentFeedGoalsList = ArrayList()

        feedAdapter = context?.let { FeedAdapter(it, currentFeedGoalsList!!, currentUser!!.uid) }

        feedRecyclerView!!.adapter = feedAdapter
        feedRecyclerView!!.layoutManager = LinearLayoutManager(context)
        feedRecyclerView!!.itemAnimator!!.changeDuration = 0
        feedRecyclerView!!.setItemViewCacheSize(20)

        profileImage!!.setOnClickListener {
            var moveToMyProfile = Intent(context, ProfileActivity::class.java)
            moveToMyProfile.putExtra("userId", FirebaseAuth.getInstance().currentUser!!.uid)
            startActivity(moveToMyProfile)
        }

        //Load current user profile pic
        loadUserProfilePic()

        getFeed(currentFeedUsersIdList, currentFeedGoalsList!!)

        //Refresh Feed
        swipeContainer!!.setOnRefreshListener {
            fetchFeedAsync()
        }


        //Set swipe-to-load colors
        swipeContainer!!.setProgressBackgroundColor(R.color.colorPrimary)
        swipeContainer!!.setColorSchemeColors(Color.WHITE)

        //user logout
        userLogoutIcon!!.setOnClickListener { userLogout()}
        return homeView
    }

    private fun userLogout() {

        val logoutAlert = AlertDialog.Builder(context as Activity)
        logoutAlert.setMessage("Are you sure?")
            .setPositiveButton("Logout") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                (context as Activity).finish()

            }.setNegativeButton("Cancel", null)

        logoutAlert.create().show()

    }

    private fun fetchFeedAsync() {

        LoadFeedAsyncTask(
            currentUser!!.uid,
            currentFeedUsersIdList!!,
            currentFeedGoalsList!!,
            feedAdapter,
            swipeContainer!!
        ).execute()

    }


    private fun loadUserProfilePic() {
        val storage = FirebaseStorage.getInstance()

        val gsReference =
            storage.getReferenceFromUrl("gs://ourgoal-ebee9.appspot.com/users/profile_pic/${currentUser!!.uid}")

        gsReference.downloadUrl
            .addOnSuccessListener {

                Picasso.get().load(it).placeholder(R.drawable.default_user_ic).noFade()
                    .into(profileImage)

            }
            .addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    //Not Found
                }
            }
    }

    private fun getFeed(usersIdList: ArrayList<String>?, feedGoalsList: ArrayList<Goal>) {

        usersIdList!!.clear()

        val currentUserConnectionDB =
            FirebaseDatabase.getInstance()
                .reference.child("connections/${currentUser!!.uid}/follow")


        val getAllFriendsIdListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                //Add all the users which current user follow
                for (userId in p0.children) {
                    usersIdList.add(userId.key.toString())
                }

                usersIdList.add(currentUser!!.uid)

                getAllPosts(usersIdList, feedGoalsList)
            }

        }

        currentUserConnectionDB.addListenerForSingleValueEvent(getAllFriendsIdListener)
    }

    private fun getAllPosts(
        usersIdList: ArrayList<String>,
        feedGoalsList: ArrayList<Goal>
    ) {

        feedGoalsList.clear()

        //Get each user's goals
        for (userId in usersIdList) {

            val userGoalsDB =
                FirebaseDatabase.getInstance()
                    .reference.child("goals")

            val getAllUserGoalsListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    for (goal in p0.child(userId).children) {

                        val userGoalMap = goal.value as HashMap<*, *>
                        val userGoal = Goal(
                            userGoalMap["goalId"].toString(),
                            userGoalMap["goalTitle"].toString(),
                            userGoalMap["goalDescription"].toString(),
                            userGoalMap["goalProgress"] as Long,
                            userGoalMap["goalSteps"] as Long,
                            userGoalMap["commentSectionId"].toString(),
                            userGoalMap["goalStatus"].toString(),
                            userGoalMap["datePosted"].toString(),
                            userGoalMap["userId"].toString()
                        )

                        //Check if goal isn't hidden
                        val isGoalHiddenFlag =
                            p0.child("hidden_goals/${currentUser!!.uid}/${userGoal.goalId}")
                                .exists()
                        if (!isGoalHiddenFlag)
                            feedGoalsList.add(userGoal)
                    }

                    feedAdapter!!.sortByAsc()
                    feedAdapter!!.notifyDataSetChanged()

                }

            }


            userGoalsDB.addListenerForSingleValueEvent(getAllUserGoalsListener)
        }
    }


}
