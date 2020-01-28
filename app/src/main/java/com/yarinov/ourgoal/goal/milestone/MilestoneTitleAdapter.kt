package com.yarinov.ourgoal.goal.milestone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fangxu.allangleexpandablebutton.AllAngleExpandableButton
import com.fangxu.allangleexpandablebutton.ButtonData
import com.fangxu.allangleexpandablebutton.ButtonEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MilestoneTitleAdapter(
    private val context: Context,
    private var goalMilestoneTitleList: ArrayList<GoalMilestone>,
    private var currentGoal: Goal,
    private var currentGoalMilestoneNumber: Long,
    private var commentSectionLayout: LinearLayout
) : RecyclerView.Adapter<MilestoneTitleAdapter.ViewHolder>() {

    private var minimizedMilestones = true
    private var inEditMoodFlag = false
    private var inEditMoodSortFlag = false

    var menuButtonIconsArrayList: ArrayList<ButtonData>? = null

    var milestoneStatusIcon: Button? = null
    var editMilestoneExpandableButton: AllAngleExpandableButton? = null

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

        milestoneStatusIcon = view.findViewById(R.id.milestoneStatusIcon) as Button
        editMilestoneExpandableButton =
            view.findViewById(R.id.editMilestoneExpandableButton) as AllAngleExpandableButton

        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        holder.editMilestoneExpandableButton!!.buttonDatas = menuButtonIconsArrayList

        //Popup menu for milestone can only open in edit mood
        holder.milestoneStatusIcon!!.isEnabled = false


        holder.milestoneTitleLabel!!.text = goalMilestoneTitleList[position].goalMilestoneTitle


        setupMilestonesIcons(position, holder)

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
        if (currentGoal.goalTitle == goalMilestoneTitleList[position].goalMilestoneTitle)
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
                            editMilestoneTitle(position)
                        }
                        2 -> {
                            markMilestoneAsComplete(position)
                        }
                        3 -> {
                            sortMilestone(position, holder)
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

    fun exitEditMood() {

        milestoneStatusIcon!!.visibility = View.VISIBLE
        editMilestoneExpandableButton!!.visibility = View.GONE

        inEditMoodFlag = false
        minimizedMilestones = true

        commentSectionLayout.visibility = View.VISIBLE

    }

    private fun setupMilestonesIcons(position: Int, holder: ViewHolder) {
        if (currentGoal.goalSteps > 0) {

            when {
                currentGoal.goalProgress == 100.toLong() -> holder.milestoneStatusIcon!!.setBackgroundResource(
                    R.drawable.complete_milestone_ic
                )
                position == currentGoalMilestoneNumber.toInt() -> holder.milestoneStatusIcon!!.setBackgroundResource(
                    R.drawable.current_milestone_ic
                )
                position < currentGoalMilestoneNumber.toInt() -> holder.milestoneStatusIcon!!.setBackgroundResource(
                    R.drawable.complete_milestone_ic
                )
            }
        } else {
            if (currentGoal.goalProgress == 100.toLong())
                holder.milestoneStatusIcon!!.setBackgroundResource(R.drawable.complete_milestone_ic)
            else
                holder.milestoneStatusIcon!!.setBackgroundResource(R.drawable.current_milestone_ic)
        }

    }

    private fun deleteMilestone(position: Int) {

        if (position < currentGoalMilestoneNumber) {
            Toast.makeText(
                context,
                "You Can't Edit Milestone That Was Marked As Complete.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (currentGoal.goalTitle == goalMilestoneTitleList[position].goalMilestoneTitle) {//Removing the whole goal
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

                        //Update goal progress in goal DB
                        println(currentGoal.goalSteps)

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
                                goalMilestoneTitleList[position].goalMilestoneTitle,
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

                        updateGoalAfterDelete()

                    }).setNegativeButton("Cancel", null)

            deleteMilestoneAlert.create().show()
        }


    }

    private fun sortMilestone(position: Int, holder: ViewHolder) {

        if (position < currentGoalMilestoneNumber) {
            Toast.makeText(
                context,
                "You Can't Edit Milestone That Was Marked As Complete.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (position == goalMilestoneTitleList.size - 1) {
            Toast.makeText(
                context,
                "You Can't Rearrange The Goal Itself.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (inEditMoodSortFlag) {
            Toast.makeText(
                context,
                "You Can't Rearrange More Then One Milestone At The Same Time.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (!inEditMoodSortFlag) {
            holder.sortMilestoneLayout!!.visibility = View.VISIBLE
            holder.itemView.clearFocus()

            inEditMoodSortFlag = true
        }

    }

    private fun markMilestoneAsComplete(position: Int) {

        if (position < currentGoalMilestoneNumber) {
            Toast.makeText(
                context,
                "You Can't Edit Milestone That Was Marked As Complete.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val currentGoalStepWeight = AdapterUtils().getStepWeight(currentGoal)

        val goalPercentDone = (position + 1) * currentGoalStepWeight

        if (goalPercentDone < currentGoal.goalProgress) {//Marking a milestone as done while he already marked as done
            Toast.makeText(
                context,
                "You Can't Mark A Complete Milestone As Complete Again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (currentGoal.goalTitle == goalMilestoneTitleList[position].goalMilestoneTitle && position == goalMilestoneTitleList.size - 1) {//Mark the goal as complete

            val markGoalCompleteAlert = AlertDialog.Builder(context)
            markGoalCompleteAlert.setMessage("This Action Will Mark The Goal Itself As Complete, Are You Sure?")
                .setPositiveButton(
                    "Mark Goal As Complete",
                    DialogInterface.OnClickListener { dialog, which ->

                        //Update goal progress percent in the goal DB
                        FirebaseDatabase.getInstance()
                            .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}/goalProgress")
                            .setValue(100)

                        //Update goal status in the goal DB
                        FirebaseDatabase.getInstance()
                            .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}/goalStatus")
                            .setValue("DONE")

                        notifyDataSetChanged()

                    }).setNegativeButton("Cancel", null)

            markGoalCompleteAlert.create().show()

        } else {//Mark current milestone as complete
            val markMilestoneCompleteAlert = AlertDialog.Builder(context)
            markMilestoneCompleteAlert.setMessage("Mark Current Milestone As Complete?")
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, which ->


                        //Update goal progress percent in the goal DB
                        FirebaseDatabase.getInstance()
                            .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}/goalProgress")
                            .setValue(goalPercentDone)

                        currentGoalMilestoneNumber = (position + 1).toLong()

                        notifyDataSetChanged()

                    }).setNegativeButton("Cancel", null)

            markMilestoneCompleteAlert.create().show()
        }
    }

    private fun editMilestoneTitle(position: Int) {

        if (position == goalMilestoneTitleList.size - 1) {
            Toast.makeText(
                context,
                "You Can't Edit The Goal Title Here.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (position < currentGoalMilestoneNumber) {
            Toast.makeText(
                context,
                "You Can't Edit Goal Here.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val popupView = (context as Activity).layoutInflater.inflate(
            R.layout.milestone_edit_dialog_layout,
            null
        )

        val milestoneInEditTitle = popupView.findViewById<TextView>(R.id.milestoneInEditTitle)
        val milestoneInEditText = popupView.findViewById<EditText>(R.id.milestoneInEditText)
        val okButton = popupView.findViewById<CardView>(R.id.okButton)

        var editMilestoneTitlePopupWindow = PopupWindow((context))
        editMilestoneTitlePopupWindow.contentView = popupView
        editMilestoneTitlePopupWindow.width = LinearLayout.LayoutParams.MATCH_PARENT
        editMilestoneTitlePopupWindow.height = LinearLayout.LayoutParams.WRAP_CONTENT
        editMilestoneTitlePopupWindow.isFocusable = true
        editMilestoneTitlePopupWindow.setBackgroundDrawable(ColorDrawable())
        editMilestoneTitlePopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        //Set current milestone text in the editview label
        milestoneInEditText.setText(goalMilestoneTitleList[position].goalMilestoneTitle)

        milestoneInEditTitle.text = "Milestone ${position + 1}"

        okButton.setOnClickListener {

            val editMilestoneAlert = AlertDialog.Builder(context)
            editMilestoneAlert.setMessage("This Action Will Change The Milestone, Are You Sure?")
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, which ->

                        //Update the milestone DB
                        FirebaseDatabase.getInstance()
                            .reference.child("goals/milestones/${currentGoal.userId}/${currentGoal.goalId}/${goalMilestoneTitleList[position].goalMilestoneId}/goalMilestoneTitle")
                            .setValue(milestoneInEditText.text.toString())

                        notifyDataSetChanged()
                        editMilestoneTitlePopupWindow.dismiss()

                    }).setNegativeButton("Cancel", null)

            editMilestoneAlert.create().show()
        }
    }


    override fun getItemCount(): Int {
        return goalMilestoneTitleList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): GoalMilestone {
        return goalMilestoneTitleList[position]
    }

    fun updateGoalAfterDelete() {

        val oldStepWeight: Long
        var stepsCompleted: Long = 0

        if (currentGoal.goalProgress != 0.toLong()) {
            oldStepWeight = AdapterUtils().getStepWeight(currentGoal)
            stepsCompleted = currentGoal.goalProgress / oldStepWeight
        }

        val goalDB =
            FirebaseDatabase.getInstance()
                .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}")

        val getNewGoalDataListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val newGoalMap = p0.value as HashMap<*, *>
                currentGoal = Goal(
                    newGoalMap["goalId"].toString(),
                    newGoalMap["goalTitle"].toString(),
                    newGoalMap["goalDescription"].toString(),
                    newGoalMap["goalProgress"] as Long,
                    newGoalMap["goalSteps"] as Long,
                    newGoalMap["commentSectionId"].toString(),
                    newGoalMap["goalStatus"].toString(),
                    newGoalMap["datePosted"].toString(),
                    newGoalMap["userId"].toString()
                )

                if (currentGoal.goalProgress != 0.toLong()) {
                    val newStepWeight = AdapterUtils().getStepWeight(currentGoal)
                    val newGoalProgress = newStepWeight * stepsCompleted

                    FirebaseDatabase.getInstance()
                        .reference.child("goals/${currentGoal.userId}/${currentGoal.goalId}/goalProgress")
                        .setValue(newGoalProgress)
                }

            }

        }

        goalDB.addListenerForSingleValueEvent(getNewGoalDataListener)
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var milestoneTitleLabel: TextView? = null
        var lowerDivider: View? = null
        var milestoneStatusIcon: Button? = null
        var editMilestoneExpandableButton: AllAngleExpandableButton? = null
        var iconLinearLayout: LinearLayout? = null
        var sortMilestoneLayout: LinearLayout? = null

        var upButton: Button? = null
        var downButton: Button? = null
        var okButton: Button? = null

        init {

            milestoneTitleLabel = mView.findViewById(R.id.milestoneTitleLabel) as TextView
            lowerDivider = mView.findViewById(R.id.lowerDivider) as View
            milestoneStatusIcon = mView.findViewById(R.id.milestoneStatusIcon) as Button
            editMilestoneExpandableButton =
                mView.findViewById(R.id.editMilestoneExpandableButton) as AllAngleExpandableButton
            iconLinearLayout =
                mView.findViewById(R.id.iconLinearLayout) as LinearLayout
            sortMilestoneLayout =
                mView.findViewById(R.id.sortMIlestoneLayout) as LinearLayout

            upButton = mView.findViewById(R.id.upButton) as Button
            downButton = mView.findViewById(R.id.downButton) as Button
            okButton = mView.findViewById(R.id.okButton) as Button
            upButton!!.setOnClickListener(moveUp())
            downButton!!.setOnClickListener(moveDown())
            okButton!!.setOnClickListener(confirmMilestoneMove())


        }

        private fun confirmMilestoneMove(): (View) -> Unit = {
            layoutPosition.also {

                for (position in 0 until goalMilestoneTitleList.size - 1) {

                    FirebaseDatabase.getInstance()
                        .reference.child("goals/milestones/${currentGoal.userId}/${currentGoal.goalId}/${goalMilestoneTitleList[position].goalMilestoneId}/goalMilestoneOrder")
                        .setValue(position + 1)
                }

                sortMilestoneLayout!!.visibility = View.GONE
                inEditMoodSortFlag = false


            }
        }

        private fun moveUp(): (View) -> Unit = {
            layoutPosition.takeIf { it > 0 && it < goalMilestoneTitleList.size - 1 }
                ?.also { currentPosition ->
                    goalMilestoneTitleList.removeAt(currentPosition).also {
                        goalMilestoneTitleList.add(currentPosition - 1, it)
                    }
                    notifyItemMoved(currentPosition, currentPosition - 1)

                }
        }

        private fun moveDown(): (View) -> Unit = {
            layoutPosition.takeIf { it < goalMilestoneTitleList.size - 2 }
                ?.also { currentPosition ->
                    goalMilestoneTitleList.removeAt(currentPosition).also {
                        goalMilestoneTitleList.add(currentPosition + 1, it)
                    }
                    notifyItemMoved(currentPosition, currentPosition + 1)

                }
        }

    }

}