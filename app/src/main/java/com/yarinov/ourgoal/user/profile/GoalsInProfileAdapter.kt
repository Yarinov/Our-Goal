package com.yarinov.ourgoal.user.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.goal.SingleGoalActivity
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils

class GoalsInProfileAdapter(
    private val context: Context,
    private var myGoalsList: ArrayList<Goal>
) : RecyclerView.Adapter<GoalsInProfileAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.goal_in_profile_layout, parent, false)

        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var currentGoal = myGoalsList[position]

        holder.goalTitleLabel!!.text = currentGoal.goalTitle
        holder.goalDescriptionLabel!!.text = currentGoal.goalDescription

        AdapterUtils().setFadeAnimation(holder.itemView, 950)

        holder.itemView.setOnClickListener {

            val moveToSingleGoalIntent = Intent(context, SingleGoalActivity::class.java)

            moveToSingleGoalIntent.putExtra("currentGoal", currentGoal)

            context.startActivity(moveToSingleGoalIntent)
        }
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


//    fun sortByAsc() {
//        val comparator: Comparator<User> =
//            Comparator { object1: User, object2: User ->
//                object1.firstName.compareTo(object2.firstName, true)
//            }
//        Collections.sort(searchUsersResultList, comparator)
//        notifyDataSetChanged()
//    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var goalTitleLabel: TextView? = null
        var goalDescriptionLabel: TextView? = null

        init {

            goalTitleLabel = mView.findViewById(R.id.goalTitleLabel) as TextView
            goalDescriptionLabel = mView.findViewById(R.id.goalDescriptionLabel) as TextView

        }

    }

}