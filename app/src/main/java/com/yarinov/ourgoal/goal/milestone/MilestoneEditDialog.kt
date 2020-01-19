package com.yarinov.ourgoal.goal.milestone

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.yarinov.ourgoal.R

class MilestoneEditDialog : Dialog, View.OnClickListener {

    var singleGoalActivity: Activity
    lateinit var editMilestoneDialog: Dialog
    lateinit var optionMoodButton: Button


    constructor(
        singleGoalActivity: Activity
    ) : super(singleGoalActivity) {

        this.singleGoalActivity = singleGoalActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.milestone_edit_dialog_layout)

        optionMoodButton = findViewById(R.id.milestoneInEditOptionsButton)

        this.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        this.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        setupOptionsMenu()
    }

    private fun setupOptionsMenu() {
        //Creating the popupmenu for milestone
//        var menuItemCardLayoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT, 160
//        )
//        menuItemCardLayoutParams.setMargins(18, 30, 30, 25)
//
//        val textLayoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        textLayoutParams.gravity = Gravity.CENTER_VERTICAL
//        textLayoutParams.setMargins(20, -30, 0, 0)
//
//        //Edit current milestone card section
//        var editMilestoneLayout = LinearLayout(context)
//        editMilestoneLayout.orientation = LinearLayout.HORIZONTAL
//        editMilestoneLayout.layoutParams = menuItemCardLayoutParams
//
//        var editMilestoneImage = ImageView(context)
//        editMilestoneImage.setImageResource(R.drawable.popup_menu_edit_milestone_ic)
//        editMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
//        editMilestoneImage.maxWidth = 10
//
//        val editMilestoneText = TextView(context)
//        editMilestoneText.text = "Edit Milestone Title"
//        editMilestoneText.textSize = 17f
//        editMilestoneText.layoutParams = textLayoutParams
//
//        editMilestoneLayout.addView(editMilestoneImage)
//        editMilestoneLayout.addView(editMilestoneText)
//
//        var editMilestoneCard = CardView(context)
//        editMilestoneCard.layoutParams = menuItemCardLayoutParams
//        editMilestoneCard.addView(editMilestoneLayout)
//        editMilestoneCard.radius = 30f
//
//        //Set current milestone card section
//        var setMilestoneLayout = LinearLayout(context)
//        setMilestoneLayout.orientation = LinearLayout.HORIZONTAL
//        setMilestoneLayout.layoutParams = menuItemCardLayoutParams
//
//        var setMilestoneImage = ImageView(context)
//        setMilestoneImage.setImageResource(R.drawable.popup_menu_target_milestone_ic)
//        setMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
//        setMilestoneImage.maxWidth = 10
//
//        val setMilestoneText = TextView(context)
//        setMilestoneText.text = "Mark As Complete"
//        setMilestoneText.textSize = 17f
//        setMilestoneText.layoutParams = textLayoutParams
//
//        setMilestoneLayout.addView(setMilestoneImage)
//        setMilestoneLayout.addView(setMilestoneText)
//
//        var setMilestoneCard = CardView(context)
//        setMilestoneCard.layoutParams = menuItemCardLayoutParams
//        setMilestoneCard.addView(setMilestoneLayout)
//        setMilestoneCard.radius = 30f
//
//        //Move current milestone card section
//        var moveMilestoneLayout = LinearLayout(context)
//        moveMilestoneLayout.orientation = LinearLayout.HORIZONTAL
//        moveMilestoneLayout.layoutParams = menuItemCardLayoutParams
//
//        var moveMilestoneImage = ImageView(context)
//        moveMilestoneImage.setImageResource(R.drawable.popup_menu_sort_milestone_ic)
//        moveMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
//        moveMilestoneImage.maxWidth = 10
//
//        val moveMilestoneText = TextView(context)
//        moveMilestoneText.text = "Move Milestone"
//        moveMilestoneText.textSize = 17f
//        moveMilestoneText.layoutParams = textLayoutParams
//
//        moveMilestoneLayout.addView(moveMilestoneImage)
//        moveMilestoneLayout.addView(moveMilestoneText)
//
//        var moveMilestoneCard = CardView(context)
//        moveMilestoneCard.layoutParams = menuItemCardLayoutParams
//        moveMilestoneCard.addView(moveMilestoneLayout)
//        moveMilestoneCard.radius = 30f
//
//        //Delete current milestone card section
//        var deleteMilestoneLayout = LinearLayout(context)
//        deleteMilestoneLayout.orientation = LinearLayout.HORIZONTAL
//        deleteMilestoneLayout.layoutParams = menuItemCardLayoutParams
//
//        var deleteMilestoneImage = ImageView(context)
//        deleteMilestoneImage.setImageResource(R.drawable.popup_menu_delete_milestone_ic)
//        deleteMilestoneImage.layoutParams = LinearLayout.LayoutParams(90, 90)
//        deleteMilestoneImage.maxWidth = 10
//
//        val deleteMilestoneText = TextView(context)
//        deleteMilestoneText.text = "Delete Milestone"
//        deleteMilestoneText.textSize = 17f
//        deleteMilestoneText.layoutParams = textLayoutParams
//
//        deleteMilestoneLayout.addView(deleteMilestoneImage)
//        deleteMilestoneLayout.addView(deleteMilestoneText)
//
//        var deleteMilestoneCard = CardView(context)
//        deleteMilestoneCard.layoutParams = menuItemCardLayoutParams
//        deleteMilestoneCard.addView(deleteMilestoneLayout)
//        deleteMilestoneCard.radius = 30f
    }


    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}