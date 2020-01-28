package com.yarinov.ourgoal.goal.milestone

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.ourgoal.R

class GoalMilestoneAdapter(
    private val context: Context,
    private var goalMilestoneList: ArrayList<GoalMilestone>
) : RecyclerView.Adapter<GoalMilestoneAdapter.ViewHolder>() {

    var milestoneNumberTitle: TextView? = null
    var milestoneTitleInputEditText: EditText? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.new_milestone_layout, parent, false)

        milestoneNumberTitle = view.findViewById(R.id.milestoneNumberTitle) as TextView
        milestoneTitleInputEditText =
            view.findViewById(R.id.milestoneTitleInputEditText) as EditText


        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        milestoneNumberTitle!!.text = "Milestone #${goalMilestoneList[position].goalMilestoneOrder}"

    }


    @SuppressLint("SetTextI18n")
    fun rearrangeMilestoneArray() {

        for (milestone in goalMilestoneList) {
            milestoneNumberTitle!!.text = "Milestone #${milestone.goalMilestoneOrder}"
        }


    }

    fun insertMilestoneData() {

        for (milestone in goalMilestoneList) {
            milestone.goalMilestoneOrder = goalMilestoneList.indexOf(milestone) + 1
        }

    }


    override fun getItemCount(): Int {
        return goalMilestoneList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): GoalMilestone {
        return goalMilestoneList[position]
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

        var milestoneNumberTitle: TextView? = null
        var milestoneTitleInputEditText: EditText? = null

        init {

            milestoneNumberTitle = mView.findViewById(R.id.milestoneNumberTitle) as TextView
            milestoneTitleInputEditText =
                mView.findViewById(R.id.milestoneTitleInputEditText) as EditText

            milestoneTitleInputEditText!!.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {}

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    goalMilestoneList[adapterPosition].goalMilestoneTitle = p0.toString()
                }

            })
        }

    }

}