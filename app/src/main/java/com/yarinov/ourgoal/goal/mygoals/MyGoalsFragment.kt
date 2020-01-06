package com.yarinov.ourgoal.goal.mygoals


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal


class MyGoalsFragment : Fragment() {

    var createNewGoalSection: CardView? = null
    var noGoalsLabel: TextView? = null

    var myGoalsRecyclerView: RecyclerView? = null
    var myGoalsAdapter: MyGoalsAdapter? = null
    var myGoalsList: ArrayList<Goal>? = null

    var currentUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myGoalsFragmentView = inflater.inflate(R.layout.fragment_my_goals, container, false)

        currentUser = FirebaseAuth.getInstance().currentUser

        createNewGoalSection = myGoalsFragmentView.findViewById(R.id.createNewGoalSection)
        myGoalsRecyclerView = myGoalsFragmentView.findViewById(R.id.myGoalsRecyclerView)
        noGoalsLabel = myGoalsFragmentView.findViewById(R.id.noGoalsLabel)

        myGoalsList = ArrayList()
        myGoalsAdapter = context?.let { MyGoalsAdapter(it, myGoalsList!!, currentUser!!.uid) }

        myGoalsRecyclerView!!.layoutManager = LinearLayoutManager(context)
        myGoalsRecyclerView!!.adapter = myGoalsAdapter
        myGoalsRecyclerView!!.hasFixedSize()

        //Move to creating new goal activity
        createNewGoalSection?.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    CreateNewGoalActivity::class.java
                )
            )
        }

        getMyGoals()

        return myGoalsFragmentView
    }


    fun getMyGoals() {

        myGoalsList?.clear()

        val currentUserGoalDB =
            FirebaseDatabase.getInstance().reference.child("goals/${currentUser!!.uid}")

        val getMyGoalsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                myGoalsList?.clear()

                //If user have goals
                if (dataSnapshot.exists()) {

                    //Update UI
                    myGoalsRecyclerView!!.visibility = View.VISIBLE
                    noGoalsLabel!!.visibility = View.GONE

                    for (goal in dataSnapshot.children) {

                        val userGoalMap = goal.value as HashMap<*, *>
                        val userGoal = Goal(userGoalMap["goalId"].toString(),
                            userGoalMap["goalTitle"].toString(),
                            userGoalMap["goalDescription"].toString(),
                            userGoalMap["goalProgress"] as Long,
                            userGoalMap["goalSteps"] as Long,
                            userGoalMap["commentSectionId"].toString(),
                            userGoalMap["goalStatus"].toString())

                        myGoalsList?.add(userGoal)

                    }

                } else {//If user don't have goals

                    //Update UI
                    myGoalsRecyclerView!!.visibility = View.GONE
                    noGoalsLabel!!.visibility = View.VISIBLE

                }

                myGoalsAdapter!!.notifyDataSetChanged()
            }

        }

        currentUserGoalDB.addListenerForSingleValueEvent(getMyGoalsListener)

    }

    override fun onResume() {
        super.onResume()

        getMyGoals()
    }

}
