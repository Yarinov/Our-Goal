package com.yarinov.ourgoal.feed

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class FeedAdapter(
    private val context: Context,
    private var goalsList: ArrayList<Goal>,
    private var currentUserId: String
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    var unfollowUserText: TextView? = null

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
        getGoalUserName(currentGoal, holder.userNameLabel, 0)

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


        //Get user pic (if exits)
        AdapterUtils().loadUserProfilePic(holder.goalUserProfilePic, currentGoal.userId)

        AdapterUtils().setFadeAnimation(holder.itemView, 1000)

        holder.timeStampLabel!!.text =
            AdapterUtils().getTimeSincePosted(position, currentGoal.datePosted)

        //Setup option menu
        goalOptionsMenuSetup(holder, position)

    }

    private fun goalOptionsMenuSetup(
        holder: ViewHolder,
        position: Int
    ) {

        var currentGoal = goalsList[position]

        val popupView = (context as Activity).layoutInflater.inflate(
            R.layout.goal_options_menu_layout,
            null
        )

        //Set the goal's user full name on the setting menu
        unfollowUserText = popupView.findViewById<TextView>(R.id.unfollowUserText)

        getGoalUserName(currentGoal, unfollowUserText, 1)

        var shareSectionOnGoalOptionMenu =
            popupView.findViewById<LinearLayout>(R.id.shareSectionOnGoalOptionMenu)
        var hideSectionOnGoalOptionMenu =
            popupView.findViewById<LinearLayout>(R.id.hideSectionOnGoalOptionMenu)
        var unfollowSectionOnGoalOptionMenu =
            popupView.findViewById<LinearLayout>(R.id.unfollowSectionOnGoalOptionMenu)
        var reportSectionOnGoalOptionMenu =
            popupView.findViewById<LinearLayout>(R.id.reportSectionOnGoalOptionMenu)
        var deleteSectionOnGoalOptionMenu =
            popupView.findViewById<LinearLayout>(R.id.deleteSectionOnGoalOptionMenu)


        var upperDivider =
            popupView.findViewById<View>(R.id.upperDivider)
        var lowerDivider =
            popupView.findViewById<View>(R.id.lowerDivider)

        //Check if current goal is current user's goal. If so make the delete section in the option menu visible and the unfollow user hide
        if (currentGoal.userId == currentUserId) {
            deleteSectionOnGoalOptionMenu.visibility = View.VISIBLE
            unfollowSectionOnGoalOptionMenu.visibility = View.GONE
            upperDivider.visibility = View.GONE
        }else{
            lowerDivider.visibility = View.GONE
        }

        var goalOptionsMenuPopupWindow = PopupWindow(context)
        goalOptionsMenuPopupWindow.contentView = popupView
        goalOptionsMenuPopupWindow.width = LinearLayout.LayoutParams.MATCH_PARENT
        goalOptionsMenuPopupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        goalOptionsMenuPopupWindow.isFocusable = true
        goalOptionsMenuPopupWindow.setBackgroundDrawable(ColorDrawable())
        goalOptionsMenuPopupWindow.animationStyle = R.style.popup_window_animation_bottom

        //On 'More' icon press -> open the option menu and dim the background. When dismiss clear dim.
        holder.goalOptions!!.setOnClickListener {

            val root = context.window.decorView.rootView as ViewGroup
            AdapterUtils().applyDim(root, 0.5f)
            goalOptionsMenuPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0)

            goalOptionsMenuPopupWindow.setOnDismissListener {
                AdapterUtils().clearDim(root)
            }
        }

        //Delete the goal
        deleteSectionOnGoalOptionMenu.setOnClickListener {
            deleteCurrentUserGoal(currentGoal, goalOptionsMenuPopupWindow)
        }

        //unfollow user
        unfollowSectionOnGoalOptionMenu.setOnClickListener {
            unfollowUser(currentGoal, goalOptionsMenuPopupWindow)
        }
    }

    private fun unfollowUser(currentGoal: Goal, goalOptionsMenuPopupWindow: PopupWindow) {

        FirebaseDatabase.getInstance()
            .reference.child("connections/$currentUserId/hidden_friends/${currentGoal.userId}")
            .setValue(true).addOnCompleteListener {
                goalOptionsMenuPopupWindow.dismiss()
            }
    }

    private fun deleteCurrentUserGoal(
        currentGoal: Goal,
        goalOptionsMenuPopupWindow: PopupWindow
    ) {
        val deleteGoalAlert = AlertDialog.Builder(context)
        deleteGoalAlert.setMessage("This Action Will Delete The Goal Itself, Are You Sure?")
            .setPositiveButton(
                "Delete Goal",
                DialogInterface.OnClickListener { dialog, which ->

                    //Update the milestone DB
                    FirebaseDatabase.getInstance()
                        .reference.child("goals/milestones/${currentGoal.userId}/${currentGoal.goalId}")
                        .removeValue()

                    //Update number of steps in the goal DB
                    FirebaseDatabase.getInstance()
                        .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}")
                        .removeValue()

                    notifyDataSetChanged()
                    goalOptionsMenuPopupWindow.dismiss()

                }).setNegativeButton("Cancel", null)

        deleteGoalAlert.create().show()
    }


    private fun setupCurrentGoalProgress(currentGoal: Goal, holder: FeedAdapter.ViewHolder) {
        val currentGoalMilestonesDB =
            FirebaseDatabase.getInstance()
                .reference.child("goals/milestones/${currentGoal.userId}/${currentGoal.goalId}")

        val getGoalMilestonesListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                currentGoalMilestonesTitle!!.clear()

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
                } else {//If goal have no milestone add 'Starting Point'
                    currentGoalMilestonesTitle!!.add(
                        MilestoneTitle(
                            "Start",
                            0
                        )
                    )
                }

                currentGoalMilestonesTitle!!.add(
                    MilestoneTitle(
                        currentGoal.goalTitle,
                        currentGoalMilestonesTitle!!.size + 1
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

                    holder.myGoalProgressBar!!.go(milestonesAccomplished.toInt(), true)

                } else if (currentGoal.goalSteps != 0.toLong() && currentGoal.goalProgress == 0.toLong()) {
                    holder.myGoalProgressBar!!.go(0, true)
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

    /**
     * This function will set the user's full name in the textViews.
     * View position: 0 - User name Label in the post itself
     *                  1 - User name label in the goal option menu
     */
    private fun getGoalUserName(currentGoal: Goal, textView: TextView?, viewPosition: Int) {

        val currentUserGoalDB =
            FirebaseDatabase.getInstance()
                .reference.child("users/${currentGoal.userId}")

        val getUserNameListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userFullName =
                    "${p0.child("firstName").value.toString()} ${p0.child("lastName").value.toString()}"


                if (viewPosition == 0)
                    textView!!.text = userFullName
                else if (viewPosition == 1)
                    textView!!.text = "Unfollow $userFullName"

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
        var timeStampLabel: TextView? = null


        var goalOptions: ImageView? = null
        var supportIcon: ImageView? = null

        var myGoalProgressBar: StepView? = null

        var goalSupportLayout: LinearLayout? = null
        var goalCommentLayout: LinearLayout? = null
        var goalUserProfilePic: CircleImageView? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            goalTitleLabel = mView.findViewById(R.id.goalTitleLabel) as TextView
            goalDescriptionLabel = mView.findViewById(R.id.goalDescriptionLabel) as TextView
            supportersCounterLabel =
                mView.findViewById(R.id.supportersCounterLabel) as TextView
            commentsCounterLabel =
                mView.findViewById(R.id.commentsCounterLabel) as TextView
            timeStampLabel = mView.findViewById(R.id.timeStampLabel) as TextView


            goalUserProfilePic =
                mView.findViewById(R.id.goalUserProfilePic) as CircleImageView

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
                    } else {//Current user support the goal
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