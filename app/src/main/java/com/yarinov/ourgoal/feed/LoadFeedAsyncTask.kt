package com.yarinov.ourgoal.feed

import android.os.AsyncTask
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.goal.Goal

class LoadFeedAsyncTask : AsyncTask<Void, Void, String> {


    var usersIdList: ArrayList<String>
    var feedGoalsList: ArrayList<Goal>
    var currentUserId: String
    var feedAdapter: FeedAdapter?
    var swipeContainer: SwipeRefreshLayout

    constructor(
        currentUserId: String,
        usersIdList: ArrayList<String>,
        feedGoalsList: ArrayList<Goal>,
        feedAdapter: FeedAdapter?,
        swipeContainer: SwipeRefreshLayout
    ) {
        this.currentUserId = currentUserId
        this.usersIdList = usersIdList
        this.feedGoalsList = feedGoalsList
        this.feedAdapter = feedAdapter
        this.swipeContainer = swipeContainer
    }

    override fun doInBackground(vararg p0: Void?): String {
        getFeed(this.usersIdList, this.feedGoalsList)

        return "feed updated"
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        swipeContainer.isRefreshing = false

    }


    private fun getFeed(usersIdList: ArrayList<String>?, feedGoalsList: ArrayList<Goal>) {

        usersIdList!!.clear()

        val currentUserConnectionDB =
            FirebaseDatabase.getInstance()
                .reference.child("connections/${this.currentUserId}/friends")

        val getAllFriendsIdListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                //Add all current user friends
                for (userId in p0.children)
                    usersIdList.add(userId.key.toString())

                usersIdList.add(currentUserId)

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
                    .reference.child("goals/$userId")

            val getAllUserGoalsListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

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