package com.yarinov.ourgoal.feed

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shuhart.stepview.StepView
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.goal.SingleGoalActivity
import com.yarinov.ourgoal.goal.milestone.MilestoneTitle
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class FeedAdapter(
    private val context: Context,
    private var goalsList: ArrayList<Goal>,
    private var currentUserId: String
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    var currentGoalMilestonesTitle: ArrayList<MilestoneTitle>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.goal_main_layout, parent, false)

        currentGoalMilestonesTitle = ArrayList()

        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        val currentGoal = goalsList[position]

        holder.goalTitleLabel!!.text = currentGoal.goalTitle
        holder.goalDescriptionLabel!!.text = currentGoal.goalDescription

        //Get goal's user name
        getGoalUserName(currentGoal, holder)

        //Get Comments Count
        getCommentsCount(currentGoal, holder)

        //Get Supporters Count
        getSupportersCountAndStatus(currentGoal, holder)

        //Setup Current Goal Progressbar
        setupCurrentGoalProgress(currentGoal, holder)

        holder.itemView.setOnClickListener {

            val moveToSingleGoalIntent = Intent(context, SingleGoalActivity::class.java)

            moveToSingleGoalIntent.putExtra("currentGoal", currentGoal)

            context.startActivity(moveToSingleGoalIntent)
        }

    }


    private fun setupCurrentGoalProgress(currentGoal: Goal, holder: FeedAdapter.ViewHolder) {
        val currentGoalMilestonesDB =
            FirebaseDatabase.getInstance()
                .reference.child("goals/milestones/${currentGoal.userId}/${currentGoal.goalId}")

        val getGoalMilestonesListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                currentGoalMilestonesTitle!!.clear()
                currentGoalMilestonesTitle!!.add(
                    MilestoneTitle(
                        "Start",
                        0
                    )
                )

                if (p0.exists()) {//Goal have milestone/s
                    for (milestone in p0.children) {

                        val currentMileTitle =
                            milestone.child("goalMilestoneTitle").value.toString()
                        val currentMileOrder =
                            milestone.child("goalMilestoneOrder").value.toString()

                        currentGoalMilestonesTitle!!.add(
                            MilestoneTitle(
                                currentMileTitle,
                                currentMileOrder.toInt()
                            )
                        )

                    }
                }

                currentGoalMilestonesTitle!!.add(
                    MilestoneTitle(
                        currentGoal.goalTitle,
                        currentGoalMilestonesTitle!!.size
                    )
                )

                currentGoalMilestonesTitle!!.sortBy { it.milestoneOrder }

                holder.myGoalProgressBar!!.setStepsNumber(currentGoalMilestonesTitle!!.size)

                //Set the current goal progress
                if (currentGoal.goalProgress == 100.toLong()) {//If goal accomplished
                    holder.myGoalProgressBar!!.go(currentGoalMilestonesTitle!!.size - 1, true)
                    holder.myGoalProgressBar!!.done(true)


                } else if (currentGoal.goalSteps != 0.toLong() && currentGoal.goalProgress != 0.toLong()) {

                    val stepWeight = 100 / (currentGoal.goalSteps + 1)
                    val milestonesAccomplished = currentGoal.goalProgress / stepWeight

                    holder.myGoalProgressBar!!.go(milestonesAccomplished.toInt() + 1, true)

                } else {
                    //Skip the first 'Start' Step
                    holder.myGoalProgressBar!!.go(1, true)
                }
            }

        }
        currentGoalMilestonesDB.addListenerForSingleValueEvent(getGoalMilestonesListener)
    }

    private fun getSupportersCountAndStatus(currentGoal: Goal, holder: FeedAdapter.ViewHolder) {

        val currentUserGoalSupportDB =
            FirebaseDatabase.getInstance()
                .reference.child("supports/${currentGoal.userId}/goals/${currentGoal.goalId}")

        val getSupportCountListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    holder.supportersCounterLabel!!.text = "${p0.children.count()} Supporters"

                    //If current user is support this goal, mark as supported
                    if (p0.child(currentUserId).exists())
                        holder.supportIcon!!.setBackgroundResource(R.drawable.support_full_ic)

                    holder.supportersCounterLabel!!.visibility = View.VISIBLE


                } else
                    holder.supportersCounterLabel!!.visibility = View.GONE

            }

        }

        currentUserGoalSupportDB.addListenerForSingleValueEvent(getSupportCountListener)
    }

    private fun getCommentsCount(currentGoal: Goal, holder: FeedAdapter.ViewHolder) {
        val currentUserGoalCommentsDB =
            FirebaseDatabase.getInstance()
                .reference.child("comments/${currentGoal.userId}/goals/${currentGoal.goalId}/${currentGoal.commentSectionId}")

        val getCommentsCountListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    holder.commentsCounterLabel!!.text = "${p0.children.count()} Comments"
                } else
                    holder.commentsCounterLabel!!.visibility = View.GONE

            }

        }

        currentUserGoalCommentsDB.addListenerForSingleValueEvent(getCommentsCountListener)
    }

    private fun getGoalUserName(currentGoal: Goal, holder: ViewHolder) {

        val currentUserGoalDB =
            FirebaseDatabase.getInstance()
                .reference.child("users/${currentGoal.userId}")

        val getUserNameListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userFullName =
                    "${p0.child("firstName").value.toString()} ${p0.child("lastName").value.toString()}"
                holder.userNameLabel!!.text = userFullName
            }

        }

        currentUserGoalDB.addListenerForSingleValueEvent(getUserNameListener)
    }


    override fun getItemCount(): Int {
        return goalsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): Goal {
        return goalsList[position]
    }


    fun sortByAsc() {
        val comparator: Comparator<Goal> =
            Comparator { object1: Goal, object2: Goal ->
                object2.datePosted.compareTo(object1.datePosted, true)
            }
        Collections.sort(goalsList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var userNameLabel: TextView? = null
        var goalTitleLabel: TextView? = null
        var goalDescriptionLabel: TextView? = null
        var supportersCounterLabel: TextView? = null
        var commentsCounterLabel: TextView? = null

        var goalOptions: ImageView? = null
        var supportIcon: ImageView? = null

        var myGoalProgressBar: StepView? = null

        var goalSupportLayout: LinearLayout? = null
        var goalCommentLayout: LinearLayout? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            goalTitleLabel = mView.findViewById(R.id.goalTitleLabel) as TextView
            goalDescriptionLabel = mView.findViewById(R.id.goalDescriptionLabel) as TextView
            supportersCounterLabel =
                mView.findViewById(R.id.supportersCounterLabel) as TextView
            commentsCounterLabel =
                mView.findViewById(R.id.commentsCounterLabel) as TextView

            goalOptions = mView.findViewById(R.id.goalOptions) as ImageView
            supportIcon = mView.findViewById(R.id.supportIcon) as ImageView

            myGoalProgressBar =
                mView.findViewById(R.id.step_view) as StepView

            goalSupportLayout =
                mView.findViewById(R.id.goalSupportLayout) as LinearLayout

            goalCommentLayout =
                mView.findViewById(R.id.goalCommentLayout) as LinearLayout

            goalSupportLayout!!.setOnClickListener {
                goalSupportLayoutPressed(adapterPosition)
            }

        }

        private fun goalSupportLayoutPressed(adapterPosition: Int) {

            val currentGoalSupportDB =
                FirebaseDatabase.getInstance()
                    .reference.child("supports/${goalsList[adapterPosition].userId}/goals/${goalsList[adapterPosition].goalId}/$currentUserId")

            val getSupportStatusListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.exists()) {//If user already support the goal and press again -> Remove support
                        currentGoalSupportDB.removeValue()

                        supportIcon!!.setBackgroundResource(R.drawable.support_empty_ic)
                    }
                    else{//Current user support the goal
                        currentGoalSupportDB.setValue(true)
                        supportIcon!!.setBackgroundResource(R.drawable.support_full_ic)
                    }

                    getSupportersCountAndStatus(goalsList[adapterPosition], this@ViewHolder)
                }

            }

            currentGoalSupportDB.addListenerForSingleValueEvent(getSupportStatusListener)
        }

    }


}