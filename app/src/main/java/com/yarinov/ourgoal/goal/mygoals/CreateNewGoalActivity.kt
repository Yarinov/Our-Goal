package com.yarinov.ourgoal.goal.mygoals

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.GOAL_STATUS
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.goal.GoalMilestone
import com.yarinov.ourgoal.goal.GoalMilestoneAdapter

class CreateNewGoalActivity : AppCompatActivity() {

    var currentGoalMilestones: ArrayList<GoalMilestone>? = null
    var goalMilestoneAdapter: GoalMilestoneAdapter? = null
    var milestonesRecyclerView: RecyclerView? = null

    var newMilestoneSection: CardView? = null
    var deleteMilestoneSection: CardView? = null

    var myGoalInputEditText: EditText? = null
    var myGoalDescriptionInputEditText: EditText? = null
    var createGoalButton: Button? = null

    var currentUser: FirebaseUser? = null
    var currentMilestoneNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_goal)

        //Setup current user
        currentUser = FirebaseAuth.getInstance().currentUser

        //View Relate
        newMilestoneSection = findViewById(R.id.newMilestoneSection)
        deleteMilestoneSection = findViewById(R.id.deleteMilestoneSection)
        milestonesRecyclerView = findViewById(R.id.milestonesRecyclerView)
        myGoalInputEditText = findViewById(R.id.myGoalInputEditText)
        myGoalDescriptionInputEditText = findViewById(R.id.myGoalDescriptionInputEditText)
        createGoalButton = findViewById(R.id.createGoalButton)

        //Init this goal milestones arraylist and set recycleradapter with it
        currentGoalMilestones = ArrayList()
        goalMilestoneAdapter = GoalMilestoneAdapter(this, currentGoalMilestones!!)

        //Setup recyclerview
        milestonesRecyclerView!!.layoutManager = LinearLayoutManager(this)
        milestonesRecyclerView!!.adapter = goalMilestoneAdapter

        //Add Milestone Section
        newMilestoneSection?.setOnClickListener {
            addNewMilestone()
        }

        //Delete Milestone Section
        deleteMilestoneSection?.setOnClickListener {
            deleteMilestone()
        }

        //Finish process and create new goal
        createGoalButton?.setOnClickListener {
            createGoal()
        }
    }

    private fun deleteMilestone() {

        //REmove the last item
        currentGoalMilestones?.removeAt(currentMilestoneNumber - 1)

        //Update the adapter with the latest change
        goalMilestoneAdapter!!.notifyItemRemoved(currentMilestoneNumber - 1)
        goalMilestoneAdapter!!.notifyItemRangeChanged(
            currentMilestoneNumber - 1,
            currentGoalMilestones!!.size
        )

        //Delete the view which hold the object
        milestonesRecyclerView!!.removeViewAt(currentMilestoneNumber - 1)

        //Update milestone counting
        currentMilestoneNumber--

        //If there isn't any milestones hide the 'Delete Milestone' Section
        if (currentMilestoneNumber == 0)
            deleteMilestoneSection!!.visibility = View.GONE

    }

    private fun addNewMilestone() {

        //Add new milestone with correct order
        currentGoalMilestones?.add(GoalMilestone("", currentMilestoneNumber + 1))

        //Nofify the adapter about the latest change and update milestone counting
        goalMilestoneAdapter!!.notifyItemInserted(currentGoalMilestones!!.size - 1)
        currentMilestoneNumber++


        deleteMilestoneSection!!.visibility = View.VISIBLE


    }

    private fun createGoal() {

        if (checkEmptyField()) return


        //Create new Goal
        val newGoal = Goal(
            myGoalInputEditText!!.text.toString(),
            myGoalDescriptionInputEditText!!.text.toString(),
            currentGoalMilestones!!.size.toLong(),
            GOAL_STATUS.ON_PROGRESS.toString()
        )

        //Write To Goals Database
        val currentUserGoalsDB = FirebaseDatabase.getInstance()
            .reference.child("goals/${currentUser!!.uid}/${newGoal.goalId}")

        currentUserGoalsDB.setValue(newGoal).addOnCompleteListener {

            //If there is goal's milestone -> Convert the arraylist to map and update the new goal object
            var goalMilestonesMap: Map<String, GoalMilestone>

            if (currentGoalMilestones!!.isNotEmpty()) {
                goalMilestonesMap =
                    currentGoalMilestones!!.map { it.goalMilestoneId to it }.toMap()


                //Write to milestones DB
                FirebaseDatabase.getInstance()
                    .reference.child("goals/milestones/${currentUser!!.uid}/${newGoal.goalId}")
                    .setValue(goalMilestonesMap).addOnCompleteListener {
                        finish()
                    }
            } else {
                finish()
            }
        }

    }

    private fun checkEmptyField(): Boolean {

        //Check if there is any empty fields
        if (myGoalInputEditText!!.text.isNullOrEmpty() || myGoalDescriptionInputEditText!!.text.isNullOrEmpty()) {

            Toast.makeText(this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show()

            return true
        }

        goalMilestoneAdapter?.insertMilestoneData()

        if (currentGoalMilestones!!.isNotEmpty()) {
            for (goalMilestone in currentGoalMilestones!!) {

                if (goalMilestone.goalMilestoneTitle.isEmpty()) {
                    Toast.makeText(this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show()

                    return true
                }
            }
        }

        return false
    }
}
