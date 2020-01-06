package com.yarinov.ourgoal.user.profile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal

class ProfileActivity : AppCompatActivity() {

    var userInProfileId: String? = null
    var currentUserProfileFlag: Boolean? = null

    var profileUserNameLabel: TextView? = null
    var profileUserInfoLabel: TextView? = null
    var goalsTitleLabel: TextView? = null

    var inProfileGoalsList: RecyclerView? = null

    var followSection: CardView? = null

    var userInProfileGoalsList: ArrayList<Goal>? = null

    var currentUser: FirebaseUser? = null

    lateinit var userFirstName: String
    lateinit var userLastName: String
    lateinit var userInfo: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        currentUser = FirebaseAuth.getInstance().currentUser

        userInProfileGoalsList = ArrayList()

        profileUserNameLabel = findViewById(R.id.profileUserNameLabel)
        profileUserInfoLabel = findViewById(R.id.profileUserInfoLabel)
        goalsTitleLabel = findViewById(R.id.goalsTitleLabel)
        followSection = findViewById(R.id.followSection)
        inProfileGoalsList = findViewById(R.id.inProfileGoalsList)

        var extra = intent.extras
        userInProfileId = extra!!.getString("userId")

        //Check if the current user is in his profile
        currentUserProfileFlag = currentUser?.uid.equals(userInProfileId)

        //If current user went to his profile, disable the 'Follow Section'
        if (currentUserProfileFlag!!)
            followSection!!.visibility = View.GONE

        getUserInProfileData()
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

        val getProfileUserGoalsListener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                userInProfileGoalsList?.clear()

                if (p0.exists()){//If user in profile have goals

                    for (goal in p0.children){

                        val userGoalMap = goal.value as HashMap<*, *>
                        val userGoal = Goal(userGoalMap["goalId"].toString(),
                            userGoalMap["goalTitle"].toString(),
                            userGoalMap["goalDescription"].toString(),
                            userGoalMap["goalProgress"] as Long,
                            userGoalMap["goalSteps"] as Long,
                            userGoalMap["commentSectionId"].toString(),
                            userGoalMap["goalStatus"].toString())

                        userInProfileGoalsList?.add(userGoal)
                    }

                }else{//If user in profile dont have goals
                    //TODO Add 'No goals' label
                }
            }

        }

        profileUserGoalsDB.addListenerForSingleValueEvent(getProfileUserGoalsListener)
    }
}
