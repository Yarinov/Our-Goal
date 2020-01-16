package com.yarinov.ourgoal.goal.milestone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils


class MilestoneTitleAdapter(
    private val context: Context,
    private var goalMilestoneTitleList: ArrayList<MilestoneTitle>,
    private val currentGoal: Goal,
    private var currentGoalMilestoneNumber: Long,
    private var commentSectionLayout: LinearLayout
) : RecyclerView.Adapter<MilestoneTitleAdapter.ViewHolder>() {

    private var minimizedMilestones = true
    private var inEditMoodFlag = false

    var milestoneStatusIcon: Button? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.step_layout, parent, false)

        milestoneStatusIcon = view.findViewById(R.id.milestoneStatusIcon) as Button
        milestoneStatusIcon!!.isEnabled = inEditMoodFlag

        //Creating the popupmenu for milestone
        var menuItemCardLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, 160
        )
        menuItemCardLayoutParams.setMargins(18, 30, 30, 25)

        val textLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        textLayoutParams.gravity = Gravity.CENTER_VERTICAL
        textLayoutParams.setMargins(20, -30, 0, 0)

        //Edit current milestone card section
        var editMilestoneLayout = LinearLayout(context)
        editMilestoneLayout.orientation = LinearLayout.HORIZONTAL
        editMilestoneLayout.layoutParams = menuItemCardLayoutParams

        var editMilestoneImage = ImageView(context)
        editMilestoneImage.setImageResource(R.drawable.popup_menu_edit_milestone_ic)
        editMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
        editMilestoneImage.maxWidth = 10

        val editMilestoneText = TextView(context)
        editMilestoneText.text = "Edit Current Milestone"
        editMilestoneText.textSize = 17f
        editMilestoneText.layoutParams = textLayoutParams

        editMilestoneLayout.addView(editMilestoneImage)
        editMilestoneLayout.addView(editMilestoneText)

        var editMilestoneCard = CardView(context)
        editMilestoneCard.layoutParams = menuItemCardLayoutParams
        editMilestoneCard.addView(editMilestoneLayout)
        editMilestoneCard.radius = 30f

        //Set current milestone card section
        var setMilestoneLayout = LinearLayout(context)
        setMilestoneLayout.orientation = LinearLayout.HORIZONTAL
        setMilestoneLayout.layoutParams = menuItemCardLayoutParams

        var setMilestoneImage = ImageView(context)
        setMilestoneImage.setImageResource(R.drawable.popup_menu_target_milestone_ic)
        setMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
        setMilestoneImage.maxWidth = 10

        val setMilestoneText = TextView(context)
        setMilestoneText.text = "Set Current Milestone"
        setMilestoneText.textSize = 17f
        setMilestoneText.layoutParams = textLayoutParams

        setMilestoneLayout.addView(setMilestoneImage)
        setMilestoneLayout.addView(setMilestoneText)

        var setMilestoneCard = CardView(context)
        setMilestoneCard.layoutParams = menuItemCardLayoutParams
        setMilestoneCard.addView(setMilestoneLayout)
        setMilestoneCard.radius = 30f

        //Move current milestone card section
        var moveMilestoneLayout = LinearLayout(context)
        moveMilestoneLayout.orientation = LinearLayout.HORIZONTAL
        moveMilestoneLayout.layoutParams = menuItemCardLayoutParams

        var moveMilestoneImage = ImageView(context)
        moveMilestoneImage.setImageResource(R.drawable.popup_menu_sort_milestone_ic)
        moveMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
        moveMilestoneImage.maxWidth = 10

        val moveMilestoneText = TextView(context)
        moveMilestoneText.text = "Move Current Milestone"
        moveMilestoneText.textSize = 17f
        moveMilestoneText.layoutParams = textLayoutParams

        moveMilestoneLayout.addView(moveMilestoneImage)
        moveMilestoneLayout.addView(moveMilestoneText)

        var moveMilestoneCard = CardView(context)
        moveMilestoneCard.layoutParams = menuItemCardLayoutParams
        moveMilestoneCard.addView(moveMilestoneLayout)
        moveMilestoneCard.radius = 30f

        //Delete current milestone card section
        var deleteMilestoneLayout = LinearLayout(context)
        deleteMilestoneLayout.orientation = LinearLayout.HORIZONTAL
        deleteMilestoneLayout.layoutParams = menuItemCardLayoutParams

        var deleteMilestoneImage = ImageView(context)
        deleteMilestoneImage.setImageResource(R.drawable.popup_menu_delete_milestone_ic)
        deleteMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
        deleteMilestoneImage.maxWidth = 10

        val deleteMilestoneText = TextView(context)
        deleteMilestoneText.text = "Delete Current Milestone"
        deleteMilestoneText.textSize = 17f
        deleteMilestoneText.layoutParams = textLayoutParams

        deleteMilestoneLayout.addView(deleteMilestoneImage)
        deleteMilestoneLayout.addView(deleteMilestoneText)

        var deleteMilestoneCard = CardView(context)
        deleteMilestoneCard.layoutParams = menuItemCardLayoutParams
        deleteMilestoneCard.addView(deleteMilestoneLayout)
        deleteMilestoneCard.radius = 30f

        val subBuilder = SubActionButton.Builder(context as Activity?)

        var circleMenu =
            FloatingActionMenu.Builder(context).setStartAngle(-50).setEndAngle(50).setRadius(500)
                .addSubActionView(editMilestoneCard).addSubActionView(moveMilestoneCard)
                .addSubActionView(setMilestoneCard)
                .addSubActionView(deleteMilestoneCard).attachTo(milestoneStatusIcon).build()


        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        //Popup menu for milestone can only open in edit mood
        holder.milestoneStatusIcon!!.isEnabled = inEditMoodFlag


        holder.milestoneTitleLabel!!.text = goalMilestoneTitleList[position].milestoneTitle

        if (currentGoal.goalSteps > 0) {

            if (position == currentGoalMilestoneNumber.toInt())
                milestoneStatusIcon!!.setBackgroundResource(R.drawable.current_milestone_ic)
            else if (position < currentGoalMilestoneNumber.toInt())
                milestoneStatusIcon!!.setBackgroundResource(R.drawable.complete_milestone_ic)
        } else {
            if (currentGoal.goalProgress == 100.toLong())
                milestoneStatusIcon!!.setBackgroundResource(R.drawable.complete_milestone_ic)
            else
                milestoneStatusIcon!!.setBackgroundResource(R.drawable.current_milestone_ic)

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
            holder.lowerDivider!!.visibility = View.GONE

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
            milestoneStatusIcon!!.setBackgroundResource(R.drawable.edit_milestone_ic)


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


        init {

            milestoneTitleLabel = mView.findViewById(R.id.milestoneTitleLabel) as TextView
            lowerDivider = mView.findViewById(R.id.lowerDivider) as View
            milestoneStatusIcon = mView.findViewById(R.id.milestoneStatusIcon) as Button

        }

    }

}