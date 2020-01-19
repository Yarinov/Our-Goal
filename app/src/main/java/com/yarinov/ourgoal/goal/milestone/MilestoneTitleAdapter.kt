package com.yarinov.ourgoal.goal.milestone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton
import com.fangxu.allangleexpandablebutton.ButtonData
import com.fangxu.allangleexpandablebutton.ButtonEventListener
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MilestoneTitleAdapter(
    private val context: Context,
    private var goalMilestoneTitleList: ArrayList<MilestoneTitle>,
    private val currentGoal: Goal,
    private var currentGoalMilestoneNumber: Long,
    private var commentSectionLayout: LinearLayout
) : RecyclerView.Adapter<MilestoneTitleAdapter.ViewHolder>() {

    private var minimizedMilestones = true
    private var inEditMoodFlag = false

    var menuButtonIconsArrayList: ArrayList<ButtonData>? = null


    init {

        menuButtonIconsArrayList = ArrayList()

        val iconsForMenuButton =
            intArrayOf(
                R.drawable.edit_milestone_ic,
                R.drawable.popup_menu_edit_milestone_ic,
                R.drawable.popup_menu_target_milestone_ic,
                R.drawable.popup_menu_sort_milestone_ic,
                R.drawable.popup_menu_delete_milestone_ic
            )

        for (icon in iconsForMenuButton) {
            menuButtonIconsArrayList!!.add(ButtonData.buildIconButton(context, icon, 4f))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.step_layout, parent, false)

        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        holder.editMilestoneExpandableButton!!.buttonDatas = menuButtonIconsArrayList

        //Popup menu for milestone can only open in edit mood
        holder.milestoneStatusIcon!!.isEnabled = false


        holder.milestoneTitleLabel!!.text = goalMilestoneTitleList[position].milestoneTitle


        if (currentGoal.goalSteps > 0) {
            if (position == currentGoalMilestoneNumber.toInt())
                holder.milestoneStatusIcon!!.setBackgroundResource(R.drawable.current_milestone_ic)
            else if (position < currentGoalMilestoneNumber.toInt())
                holder.milestoneStatusIcon!!.setBackgroundResource(R.drawable.complete_milestone_ic)
        } else {
            if (currentGoal.goalProgress == 100.toLong())
                holder.milestoneStatusIcon!!.setBackgroundResource(R.drawable.complete_milestone_ic)
            else
                holder.milestoneStatusIcon!!.setBackgroundResource(R.drawable.current_milestone_ic)
        }


        //If milestone list is minimized hide the rest of milestones except the current one
        if (position != currentGoalMilestoneNumber.toInt() && minimizedMilestones) {
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.itemView.visibility = View.GONE
        } else if (!minimizedMilestones) {
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            holder.itemView.visibility = View.VISIBLE
        }

        //Hide last vertical divider
        if (currentGoal.goalTitle == goalMilestoneTitleList[position].milestoneTitle)
            holder.lowerDivider!!.setBackgroundColor(Color.parseColor("#ffffff"))

        //Open/Close the full milestones list
        holder.itemView.setOnClickListener {

            if (currentGoal.goalSteps != 0.toLong() && !inEditMoodFlag) {
                expendMilestoneList()
            } else if (inEditMoodFlag) {
            }
        }

        //In Edit Mood make the 'Remove Milestone' icon appear
        if (inEditMoodFlag) {

            //Change all milestone status icon
            holder.milestoneStatusIcon!!.visibility = View.GONE
            holder.editMilestoneExpandableButton!!.visibility = View.VISIBLE

            holder.editMilestoneExpandableButton!!.setButtonEventListener(object :
                ButtonEventListener {
                override fun onButtonClicked(buttonNumber: Int) {
                    when (buttonNumber) {

                        1 -> {
                            editMilestoneTitle()
                        }
                        2 -> {
                            markMilestoneAsComplete()
                        }
                        3 -> {
                            sortMilestone()
                        }
                        4 -> {
                            deleteMilestone(position)
                        }
                    }
                }

                override fun onExpand() {
                    println("a")
                }

                override fun onCollapse() {
                    println("v")
                }

            })


        } else {

            holder.milestoneStatusIcon!!.visibility = View.VISIBLE
            holder.editMilestoneExpandableButton!!.visibility = View.GONE

        }

        AdapterUtils().setFadeAnimation(holder.itemView, 950)

    }

    private fun deleteMilestone(position: Int) {


        if (currentGoal.goalTitle == goalMilestoneTitleList[position].milestoneTitle) {//Removing the whole goal
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


                        (context as Activity).finish()


                    }).setNegativeButton("Cancel", null)

            deleteGoalAlert.create().show()
        } else {//Delete just milestone
            val deleteMilestoneAlert = AlertDialog.Builder(context)
            deleteMilestoneAlert.setMessage("Are you sure?")
                .setPositiveButton(
                    "Delete Milestone",
                    DialogInterface.OnClickListener { dialog, which ->

                        goalMilestoneTitleList.removeAt(position)// remove the milestone from current goal's milestones

                        //Creating new map with goal milestones to update the goal in db
                        var newMilestoneMap: HashMap<String, GoalMilestone> = HashMap()

                        //New milestone order after deleting the milestone
                        for (position in 0 until goalMilestoneTitleList.size - 1) {
                            var tempGoalMilestone = GoalMilestone(
                                UUID.randomUUID().toString(),
                                goalMilestoneTitleList[position].milestoneTitle,
                                position + 1
                            )

                            newMilestoneMap[tempGoalMilestone.goalMilestoneId] = tempGoalMilestone
                        }

                        //Update the milestone DB
                        FirebaseDatabase.getInstance()
                            .reference.child("goals/milestones/${currentGoal.userId}/${currentGoal.goalId}")
                            .setValue(newMilestoneMap)

                        //Update number of steps in the goal DB
                        FirebaseDatabase.getInstance()
                            .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}/goalSteps")
                            .setValue(newMilestoneMap.size)

                        notifyDataSetChanged()

                    }).setNegativeButton("Cancel", null)

            deleteMilestoneAlert.create().show()
        }


    }

    private fun sortMilestone() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun markMilestoneAsComplete() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun editMilestoneTitle() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun expendMilestoneList() {
        if (minimizedMilestones) {

            minimizedMilestones = false
            commentSectionLayout.visibility = View.GONE

        } else {
            minimizedMilestones = true
            commentSectionLayout.visibility = View.VISIBLE
        }


        notifyDataSetChanged()
    }

    fun inEditMood() {
        minimizedMilestones = false
        inEditMoodFlag = true
        commentSectionLayout.visibility = View.GONE

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return goalMilestoneTitleList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): MilestoneTitle {
        return goalMilestoneTitleList[position]
    }


//    fun sortByAsc() {
//        val comparator: Comparator<User> =
//            Comparator { object1: User, object2: User ->
//                object1.firstName.compareTo(object2.firstName, true)
//            }
//        Collections.sort(searchUsersResultList, comparator)
//        notifyDataSetChanged()
//    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var milestoneTitleLabel: TextView? = null
        var lowerDivider: View? = null
        var milestoneStatusIcon: Button? = null
        var editMilestoneExpandableButton: AllAngleExpandableButton? = null
        var iconLinearLayout: LinearLayout? = null


        init {

            milestoneTitleLabel = mView.findViewById(R.id.milestoneTitleLabel) as TextView
            lowerDivider = mView.findViewById(R.id.lowerDivider) as View
            milestoneStatusIcon = mView.findViewById(R.id.milestoneStatusIcon) as Button
            editMilestoneExpandableButton =
                mView.findViewById(R.id.editMilestoneExpandableButton) as AllAngleExpandableButton
            iconLinearLayout =
                mView.findViewById(R.id.iconLinearLayout) as LinearLayout

        }

    }

}