package com.yarinov.ourgoal.goal.milestone

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.step_layout, parent, false)


        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        holder.milestoneTitleLabel!!.text = goalMilestoneTitleList[position].milestoneTitle

        if (currentGoal.goalSteps > 0) {

            if (position == currentGoalMilestoneNumber.toInt())
                holder.milestoneStatusIcon!!.setImageResource(R.drawable.current_milestone_ic)
            else if (position < currentGoalMilestoneNumber.toInt())
                holder.milestoneStatusIcon!!.setImageResource(R.drawable.complete_milestone_ic)
        } else {
            if (currentGoal.goalProgress == 100.toLong())
                holder.milestoneStatusIcon!!.setImageResource(R.drawable.complete_milestone_ic)
            else
                holder.milestoneStatusIcon!!.setImageResource(R.drawable.current_milestone_ic)

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

            if (currentGoal.goalSteps != 0.toLong()) {

                if (minimizedMilestones) {

                    minimizedMilestones = false
                    commentSectionLayout.visibility = View.GONE

                } else {
                    minimizedMilestones = true
                    commentSectionLayout.visibility = View.VISIBLE
                }


                notifyDataSetChanged()
            }
        }

        AdapterUtils().setFadeAnimation(holder.itemView, 950)

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
        var milestoneStatusIcon: ImageView? = null

        init {

            milestoneTitleLabel = mView.findViewById(R.id.milestoneTitleLabel) as TextView
            lowerDivider = mView.findViewById(R.id.lowerDivider) as View
            milestoneStatusIcon = mView.findViewById(R.id.milestoneStatusIcon) as ImageView

        }

    }

}