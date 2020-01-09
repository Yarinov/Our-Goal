package com.yarinov.ourgoal.goal.mygoals

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shuhart.stepview.StepView
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.goal.MilestoneTitle
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MyGoalsAdapter(
    private val context: Context,
    private var myGoalsList: ArrayList<Goal>,
    private val currentUserUid: String
) : RecyclerView.Adapter<MyGoalsAdapter.ViewHolder>() {

    var currentGoalMilestonesTitle: ArrayList<MilestoneTitle>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_goal_layout, parent, false)

        currentGoalMilestonesTitle = ArrayList()

        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentGoal = myGoalsList[position]

        holder.goalTitleLabel!!.text = currentGoal.goalTitle
        holder.goalDescriptionLabel!!.text = currentGoal.goalDescription


        //Get Comments Count
        val currentUserGoalCommentsDB =
            FirebaseDatabase.getInstance()
                .reference.child("comments/${currentUserUid}/goals/${myGoalsList[position].goalId}/${myGoalsList[position].commentSectionId}")

        var commentsCount: String

        val getCommentsCountListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                commentsCount = if (p0.exists()) {
                    "${p0.children.count()} Comments"
                } else
                    "0 Comments"

                holder.commentsCounterLabel!!.text = commentsCount
            }

        }

        currentUserGoalCommentsDB.addListenerForSingleValueEvent(getCommentsCountListener)

        //Get Supporters Count
        val currentUserGoalSupportDB =
            FirebaseDatabase.getInstance()
                .reference.child("supports/${currentUserUid}/goals/${myGoalsList[position].goalId}")

        var supportCount: String

        val getSupportCountListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                supportCount = if (p0.exists()) {
                    "${p0.children.count()} Supporters"
                } else
                    "0 Supporters"

                holder.supportersCounterLabel!!.text = supportCount
            }

        }

        currentUserGoalSupportDB.addListenerForSingleValueEvent(getSupportCountListener)

        //Setup Current Goal Progressbar
        val currentGoalMilestonesDB =
            FirebaseDatabase.getInstance()
                .reference.child("goals/milestones/${currentUserUid}/${myGoalsList[position].goalId}")

        val getGoalMilestonesListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                currentGoalMilestonesTitle!!.clear()
                currentGoalMilestonesTitle!!.add(MilestoneTitle("Start", 0))

                if (p0.exists()) {//Goal have milestone/s
                    for (milestone in p0.children) {

                        val currentMileTitle = milestone.child("goalMilestoneTitle").value.toString()
                        val currentMileOrder = milestone.child("goalMilestoneOrder").value.toString()

                        currentGoalMilestonesTitle!!.add(MilestoneTitle(currentMileTitle, currentMileOrder.toInt()))

                    }
                }

                currentGoalMilestonesTitle!!.add(MilestoneTitle(currentGoal.goalTitle, currentGoalMilestonesTitle!!.size))

                currentGoalMilestonesTitle!!.sortBy { it.milestoneOrder }

                holder.myGoalProgressBar!!.setStepsNumber(currentGoalMilestonesTitle!!.size)

                //Set the current goal progress
                if (currentGoal.goalProgress == 100.toLong()) {//If goal accomplished
                    holder.myGoalProgressBar!!.go(currentGoalMilestonesTitle!!.size - 1, true)
                    holder.myGoalProgressBar!!.done(true)

                    holder.goalDoneIcon!!.visibility = View.VISIBLE
                } else if (currentGoal.goalSteps != 0.toLong() && currentGoal.goalProgress != 0.toLong()) {

                    val stepWeight = 100 / (currentGoal.goalSteps + 1)
                    val milestonesAccomplished = currentGoal.goalProgress/stepWeight

                    holder.myGoalProgressBar!!.go(milestonesAccomplished.toInt() + 1, true)

                } else {
                    //Skip the first 'Start' Step
                    holder.myGoalProgressBar!!.go(1, true)
                }
            }

        }
        currentGoalMilestonesDB.addListenerForSingleValueEvent(getGoalMilestonesListener)

    }


    override fun getItemCount(): Int {
        return myGoalsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): Goal {
        return myGoalsList[position]
    }


    fun sortByAsc() {
        val comparator: Comparator<Goal> =
            Comparator { object1: Goal, object2: Goal ->
                object2.datePosted.compareTo(object1.datePosted, true)
            }
        Collections.sort(myGoalsList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var goalTitleLabel: TextView? = null
        var goalDescriptionLabel: TextView? = null
        var supportersCounterLabel: TextView? = null
        var commentsCounterLabel: TextView? = null
        var goalDoneIcon: ImageView? = null
        var myGoalProgressBar: StepView? = null

        init {

            goalTitleLabel = mView.findViewById(R.id.goalTitleLabel) as TextView
            goalDescriptionLabel = mView.findViewById(R.id.goalDescriptionLabel) as TextView
            supportersCounterLabel =
                mView.findViewById(R.id.supportersCounterLabel) as TextView
            commentsCounterLabel =
                mView.findViewById(R.id.commentsCounterLabel) as TextView
            goalDoneIcon = mView.findViewById(R.id.goalDoneIcon) as ImageView
            myGoalProgressBar =
                mView.findViewById(R.id.step_view) as StepView

        }

    }

}