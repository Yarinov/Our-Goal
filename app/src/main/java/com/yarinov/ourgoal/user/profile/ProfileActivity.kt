package com.yarinov.ourgoal.user.profile

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
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
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    var userInProfileId: String? = null
    var currentUserProfileFlag: Boolean? = null

    var profileUserNameLabel: TextView? = null
    var profileUserInfoLabel: TextView? = null
    var goalsTitleLabel: TextView? = null

    var editProfileLabel: TextView? = null
    var profileImage: CircleImageView? = null

    var friendAndFollowStatusSection: LinearLayout? = null
    var friendStatusLabel: TextView? = null
    var followStatusLabel: TextView? = null
    var friendStatusIcon: ImageView? = null
    var followStatusIcon: ImageView? = null

    var userInProfileGoalsList: ArrayList<Goal>? = null
    lateinit var goalsInProfileAdapter: GoalsInProfileAdapter
    var inProfileGoalsList: RecyclerView? = null

    var currentUser: FirebaseUser? = null

    var friendPathFlag: Boolean? = null
    var blockedPathFlag: Boolean? = null
    var friendRequestSentPathFlag: Boolean? = null
    var friendRequestReceivedPathFlag: Boolean? = null
    var friendFollowFlag: Boolean? = null
    var userFollowFlag: Boolean? = null

    var friendStatusSection: LinearLayout? = null
    var followStatusSection: LinearLayout? = null

    lateinit var userFirstName: String
    lateinit var userLastName: String
    lateinit var userInfo: String

    //FriendSectionOptionMenu
    var removeFriendInFriendSectionOptionMenu: TextView? = null
    var cancelFriendRequestInFriendSectionOptionMenu: TextView? = null
    var cancelInFriendSectionOptionMenu: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var userAchievementCardView = findViewById<CardView>(R.id.userAchievementCardView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            userAchievementCardView.outlineSpotShadowColor = Color.parseColor("#D56D6E")
        }
        currentUser = FirebaseAuth.getInstance().currentUser

        userInProfileGoalsList = ArrayList()
        goalsInProfileAdapter = GoalsInProfileAdapter(this, userInProfileGoalsList!!)

        profileUserNameLabel = findViewById(R.id.profileUserNameLabel)
        profileUserInfoLabel = findViewById(R.id.profileUserInfoLabel)
        goalsTitleLabel = findViewById(R.id.goalsTitleLabel)
        inProfileGoalsList = findViewById(R.id.inProfileGoalsList)
        editProfileLabel = findViewById(R.id.editProfileLabel)
        friendAndFollowStatusSection = findViewById(R.id.friendAndFollowStatusSection)
        friendStatusLabel = findViewById(R.id.friendStatusLabel)
        followStatusLabel = findViewById(R.id.followStatusLabel)
        friendStatusIcon = findViewById(R.id.friendStatusIcon)
        followStatusIcon = findViewById(R.id.followStatusIcon)
        profileImage = findViewById(R.id.profileImage)
        friendStatusSection = findViewById(R.id.friendStatusSection)
        followStatusSection = findViewById(R.id.followStatusSection)


        var extra = intent.extras
        userInProfileId = extra!!.getString("userId")

        //Check if the current user is in his profile
        currentUserProfileFlag = currentUser?.uid.equals(userInProfileId)

        //If current user went to his profile, disable 'Follow', 'Add friend' and show edit profile
        if (currentUserProfileFlag!!) {

            //Show edit profile
            editProfileLabel!!.visibility = View.VISIBLE

            //hide add friend and follow section
            friendAndFollowStatusSection!!.visibility = View.GONE
        }

        getUserInProfileData()

        inProfileGoalsList?.adapter = goalsInProfileAdapter
        inProfileGoalsList?.layoutManager = LinearLayoutManager(this)

        AdapterUtils().loadUserProfilePic(profileImage, userInProfileId!!)

        friendStatusSection!!.setOnClickListener {
            friendSectionPressed()
        }

        followStatusSection!!.setOnClickListener {
            followSectionPressed()
        }


    }

    private fun followSectionPressed() {

        //Setup option menu for follow section
        val optionPopupView = layoutInflater.inflate(
            R.layout.follow_status_options_menu_layout,
            null
        )

        val followUserInFollowSectionOptionMenu: TextView =
            optionPopupView.findViewById(R.id.followUserInFollowSectionOptionMenu)
        val unfollowUserInFollowSectionOptionMenu: TextView =
            optionPopupView.findViewById(R.id.unfollowUserInFollowSectionOptionMenu)
        val cancelInFollowSectionOptionMenu: TextView =
            optionPopupView.findViewById(R.id.cancelInFollowSectionOptionMenu)

        var followSectionOptionMenu = PopupWindow(this)
        followSectionOptionMenu.contentView = optionPopupView
        followSectionOptionMenu.width = LinearLayout.LayoutParams.MATCH_PARENT
        followSectionOptionMenu.height = LinearLayout.LayoutParams.WRAP_CONTENT
        followSectionOptionMenu.isFocusable = true
        followSectionOptionMenu.setBackgroundDrawable(ColorDrawable())
        followSectionOptionMenu.animationStyle = R.style.popup_window_animation_bottom

        cancelInFollowSectionOptionMenu.setOnClickListener {
            followSectionOptionMenu.dismiss()
        }

        if (userFollowFlag!!) {//If current user already following user in profile

            followUserInFollowSectionOptionMenu.visibility = View.GONE

            val root = window.decorView.rootView as ViewGroup
            AdapterUtils().applyDim(root, 0.5f)
            followSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

            followSectionOptionMenu.setOnDismissListener {
                AdapterUtils().clearDim(root)
            }

            //Remove follow
            unfollowUserInFollowSectionOptionMenu.setOnClickListener {

                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                    .removeValue()

                followSectionOptionMenu.dismiss()
            }

        } else {//If current user isn't following user in profile

            unfollowUserInFollowSectionOptionMenu.visibility = View.GONE

            val root = window.decorView.rootView as ViewGroup
            AdapterUtils().applyDim(root, 0.5f)
            followSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

            followSectionOptionMenu.setOnDismissListener {
                AdapterUtils().clearDim(root)
            }

            //Follow user in profile
            followUserInFollowSectionOptionMenu.setOnClickListener {

                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                    .setValue(true)

                followSectionOptionMenu.dismiss()
            }
        }
    }

    private fun friendSectionPressed() {


        //Setup option menu for friend section
        val optionPopupView = layoutInflater.inflate(
            R.layout.friend_status_options_menu_layout,
            null
        )

        removeFriendInFriendSectionOptionMenu =
            optionPopupView.findViewById(R.id.removeFriendInFriendSectionOptionMenu)
        cancelFriendRequestInFriendSectionOptionMenu =
            optionPopupView.findViewById(R.id.cancelFriendRequestInFriendSectionOptionMenu)
        cancelInFriendSectionOptionMenu =
            optionPopupView.findViewById(R.id.cancelInFriendSectionOptionMenu)


        var nonRespondLayout: LinearLayout = optionPopupView.findViewById(R.id.nonRespondLayout)
        var respondLayout: LinearLayout = optionPopupView.findViewById(R.id.respondLayout)

        val acceptFriendRequestLabel: TextView =
            optionPopupView.findViewById(R.id.acceptFriendRequestLabel)
        val deleteFriendRequestLabel: TextView =
            optionPopupView.findViewById(R.id.deleteFriendRequestLabel)

        var friendSectionOptionMenu = PopupWindow(this)
        friendSectionOptionMenu.contentView = optionPopupView
        friendSectionOptionMenu.width = LinearLayout.LayoutParams.MATCH_PARENT
        friendSectionOptionMenu.height = LinearLayout.LayoutParams.WRAP_CONTENT
        friendSectionOptionMenu.isFocusable = true
        friendSectionOptionMenu.setBackgroundDrawable(ColorDrawable())
        friendSectionOptionMenu.animationStyle = R.style.popup_window_animation_bottom

        cancelInFriendSectionOptionMenu!!.setOnClickListener {
            friendSectionOptionMenu.dismiss()
        }

        //If already friend -> open menu
        when {
            friendPathFlag!! -> {

                nonRespondLayout.visibility = View.VISIBLE
                cancelFriendRequestInFriendSectionOptionMenu!!.visibility = View.GONE

                val root = window.decorView.rootView as ViewGroup
                AdapterUtils().applyDim(root, 0.5f)
                friendSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

                friendSectionOptionMenu.setOnDismissListener {
                    AdapterUtils().clearDim(root)
                }

                //Remove friend
                removeFriendInFriendSectionOptionMenu!!.setOnClickListener {

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friends/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friends/${currentUser!!.uid}")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/follow/${currentUser!!.uid}")
                        .removeValue()

                    friendSectionOptionMenu.dismiss()

                }

            }
            friendRequestSentPathFlag!! -> { //if current user already sent friend request -> open menu

                nonRespondLayout.visibility = View.VISIBLE
                removeFriendInFriendSectionOptionMenu!!.visibility = View.GONE

                val root = window.decorView.rootView as ViewGroup
                AdapterUtils().applyDim(root, 0.5f)
                friendSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

                friendSectionOptionMenu.setOnDismissListener {
                    AdapterUtils().clearDim(root)
                }

                //Cancel friend request
                cancelFriendRequestInFriendSectionOptionMenu!!.setOnClickListener {

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friend_request/sent/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friend_request/received/${currentUser!!.uid}")
                        .removeValue()

                    friendSectionOptionMenu.dismiss()
                }

            }
            friendRequestReceivedPathFlag!! -> {//if current user received from user in profile a friend request

                respondLayout.visibility = View.VISIBLE

                val root = window.decorView.rootView as ViewGroup
                AdapterUtils().applyDim(root, 0.5f)
                friendSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

                friendSectionOptionMenu.setOnDismissListener {
                    AdapterUtils().clearDim(root)
                }

                //Accept friend request
                acceptFriendRequestLabel.setOnClickListener {

                    //Remove request from current user DB
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friend_request/received/$userInProfileId")
                        .removeValue()

                    //Remove request from the user who sent the friend request
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friend_request/sent/${currentUser!!.uid}")
                        .removeValue()

                    //Adding the connection to both users DB
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friends/$userInProfileId")
                        .setValue(true)

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friends/${currentUser!!.uid}")
                        .setValue(true)

                    //Add follow to both user
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                        .setValue(true)

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/follow/${currentUser!!.uid}")
                        .setValue(true)

                    friendSectionOptionMenu.dismiss()
                }

                //Delete friend request
                deleteFriendRequestLabel.setOnClickListener {

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friend_request/received/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friend_request/sent/${currentUser!!.uid}")
                        .removeValue()


                    friendSectionOptionMenu.dismiss()
                }


            }
            else -> {//No connection between current user and user in profile

                //Send friend request
                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}/friend_request/sent/$userInProfileId")
                    .setValue(true)

                FirebaseDatabase.getInstance()
                    .reference.child("connections/$userInProfileId/friend_request/received/${currentUser!!.uid}")
                    .setValue(true)

            }
        }

    }

    private fun getUserInProfileData() {

        userInProfileGoalsList?.clear()

        //Get the user first name, last name and info
        val profileUserDB =
            FirebaseDatabase.getInstance().reference.child("users/$userInProfileId")

        val getProfileUserDataListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                userFirstName = p0.child("firstName").value.toString()
                userLastName = p0.child("lastName").value.toString()
                userInfo = p0.child("userInfo").value.toString()


                profileUserNameLabel!!.text = "$userFirstName $userLastName"
                profileUserInfoLabel!!.text = userInfo

                if (currentUserProfileFlag!!) {
                    goalsTitleLabel!!.text = "My Goals"
                } else {
                    goalsTitleLabel!!.text = "$userFirstName's Goals"
                }
            }

        }

        profileUserDB.addListenerForSingleValueEvent(getProfileUserDataListener)

        //Get user's goals
        val profileUserGoalsDB =
            FirebaseDatabase.getInstance().reference.child("goals/$userInProfileId")

        val getProfileUserGoalsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                userInProfileGoalsList?.clear()

                if (p0.exists()) {//If user in profile have goals

                    for (goal in p0.children) {

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

                        userInProfileGoalsList?.add(userGoal)
                    }

                } else {//If user in profile dont have goals
                    //TODO Add 'No goals' label
                }

                goalsInProfileAdapter.notifyDataSetChanged()
            }

        }

        profileUserGoalsDB.addListenerForSingleValueEvent(getProfileUserGoalsListener)

        if (!currentUserProfileFlag!!) {//If current user isn't the user in the profile

            //Get relationship between current user to user in profile
            val currentUserDB =
                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}")

            val getRelationshipListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    friendPathFlag = p0.child("friends/$userInProfileId").exists()
                    blockedPathFlag = p0.child("blocked/$userInProfileId").exists()
                    friendRequestSentPathFlag =
                        p0.child("friend_request/sent/$userInProfileId").exists()
                    friendRequestReceivedPathFlag =
                        p0.child("friend_request/received/$userInProfileId").exists()
                    friendFollowFlag = p0.child("hidden_friends/$userInProfileId").exists()
                    userFollowFlag = p0.child("follow/$userInProfileId").exists()

                    //Check if friends
                    when {
                        friendPathFlag!! -> setFriendSection(1)
                        friendRequestSentPathFlag!! -> setFriendSection(2)
                        friendRequestReceivedPathFlag!! -> setFriendSection(3)
                        blockedPathFlag!! -> setFriendSection(-1)
                        else -> setFriendSection(0)
                    }

                    //Check follow status.
                    if (userFollowFlag!!) {
                        setFollowSection(1)
                    } else {

                        setFollowSection(0)
                    }
                }

            }

            currentUserDB.addValueEventListener(getRelationshipListener)

        }
    }

    private fun setFollowSection(i: Int) {

        //0 - Not Following | 1 - Following

        if (i == 1) {
            followStatusLabel!!.text = "Following"

            ImageViewCompat.setImageTintList(
                followStatusIcon!!,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            )
        } else if (i == 0) {
            followStatusLabel!!.text = "Follow"

            ImageViewCompat.setImageTintList(
                followStatusIcon!!,
                ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
            )
        }

    }

    private fun setFriendSection(i: Int) {

        //0 - None | 1 - Friends | 2 - Sent friend request | 3 - Friend request received | -1 - Blocked

        when (i) {
            0 -> {
                friendStatusLabel!!.text = "Add Friend"
                friendStatusIcon!!.setImageResource(R.drawable.none_friend_status_ic)
            }
            1 -> {
                friendStatusLabel!!.text = "Friends"
                friendStatusIcon!!.setImageResource(R.drawable.accepted_friend_status_ic)
            }
            2 -> {
                friendStatusLabel!!.text = "Friend Request Sent"
                friendStatusIcon!!.setImageResource(R.drawable.wating_friend_status_ic)
            }
            3 -> {
                friendStatusLabel!!.text = "Respond"
                friendStatusIcon!!.setImageResource(R.drawable.wating_friend_status_ic)
            }
        }

    }
}
