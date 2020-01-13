package com.yarinov.ourgoal.goal

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shuhart.stepview.StepView
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.comment.Comment
import com.yarinov.ourgoal.goal.comment.CommentAdapter
import com.yarinov.ourgoal.goal.milestone.MilestoneTitle
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SingleGoalActivity : AppCompatActivity() {

    var currentGoal: Goal? = null
    var currentUser: FirebaseUser? = null

    var commentsRecyclerView: RecyclerView? = null
    var commentsArrayList: ArrayList<Comment>? = null
    var commentsAdapter: CommentAdapter? = null

    var commentInputEditText: EditText? = null
    var sendCommentButton: Button? = null

    var goalUserFullNameLabel: TextView? = null
    var goalDescriptionLabel: TextView? = null
    var goalTitleLabel: TextView? = null

    var supportCountLabel: TextView? = null
    var commentCountLabel: TextView? = null

    var supportSection: CardView? = null
    var supportIcon: ImageView? = null

    var currentUserSupportCurrentGoalFlag: Boolean? = null

    var noCommentsLayout: LinearLayout? = null

    var stepView: StepView? = null
    var currentGoalMilestonesTitle: ArrayList<MilestoneTitle>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_goal)

        currentUser = FirebaseAuth.getInstance().currentUser
        currentUserSupportCurrentGoalFlag = false

        val extra = intent.extras
        currentGoal = extra!!.getParcelable("currentGoal")

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        goalUserFullNameLabel = findViewById(R.id.goalUserFullNameLabel)
        goalDescriptionLabel = findViewById(R.id.goalDescriptionLabel)
        goalTitleLabel = findViewById(R.id.goalTitleLabel)
        supportCountLabel = findViewById(R.id.supportCountLabel)
        commentCountLabel = findViewById(R.id.commentCountLabel)
        commentInputEditText = findViewById(R.id.commentInputEditText)
        sendCommentButton = findViewById(R.id.sendCommentButton)
        supportSection = findViewById(R.id.supportSection)
        supportIcon = findViewById(R.id.supportIcon)

        noCommentsLayout = findViewById(R.id.noCommentsLayout)

        stepView = findViewById(R.id.stepView)

        setupStepView()

        commentsArrayList = ArrayList()
        commentsAdapter = CommentAdapter(this, commentsArrayList!!, currentUser!!.uid)

        commentsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView!!.adapter = commentsAdapter

        sendCommentButton!!.setOnClickListener {
            sendComment()
        }

        supportSection!!.setOnClickListener {
            supportSectionPressed()
        }
        initUI()
    }

    private fun setupStepView() {

        currentGoalMilestonesTitle = ArrayList()

        val currentGoalMilestonesDB =
            FirebaseDatabase.getInstance()
                .reference.child("goals/milestones/${currentGoal!!.userId}/${currentGoal!!.goalId}")

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
                        currentGoal!!.goalTitle,
                        currentGoalMilestonesTitle!!.size
                    )
                )

                currentGoalMilestonesTitle!!.sortBy { it.milestoneOrder }

                stepView!!.setStepsNumber(currentGoalMilestonesTitle!!.size)

                //Set the current goal progress
                if (currentGoal!!.goalProgress == 100.toLong()) {//If goal accomplished
                    stepView!!.go(currentGoalMilestonesTitle!!.size - 1, true)
                    stepView!!.done(true)


                } else if (currentGoal!!.goalSteps != 0.toLong() && currentGoal!!.goalProgress != 0.toLong()) {

                    val stepWeight = 100 / (currentGoal!!.goalSteps + 1)
                    val milestonesAccomplished = currentGoal!!.goalProgress / stepWeight

                    stepView!!.go(milestonesAccomplished.toInt() + 1, true)

                } else {
                    //Skip the first 'Start' Step
                    stepView!!.go(1, true)
                }
            }

        }
        currentGoalMilestonesDB.addListenerForSingleValueEvent(getGoalMilestonesListener)


    }

    private fun supportSectionPressed() {

        val currentGoalSupportDB =
            FirebaseDatabase.getInstance()
                .reference.child("supports/${currentGoal!!.userId}/goals/${currentGoal!!.goalId}/${currentUser!!.uid}")

        val getSupportStatusListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {//If user already support the goal and press again -> Remove support
                    currentGoalSupportDB.removeValue()

                } else {//Current user support the goal
                    currentGoalSupportDB.setValue(true)
                }

                getSupportersCountAndStatus()
            }

        }

        currentGoalSupportDB.addListenerForSingleValueEvent(getSupportStatusListener)
    }

    private fun sendComment() {

        var commentText = commentInputEditText!!.text.toString()

        if (commentText.isEmpty()) return //TODO Add message

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'")
        val datePosted = dateFormat.format(Date())

        var commentToSend =
            Comment(currentGoal!!.commentSectionId, currentUser!!.uid, commentText, datePosted)

        FirebaseDatabase.getInstance()
            .reference.child("comments/${currentGoal!!.userId}/goals/${currentGoal!!.goalId}/${currentGoal!!.commentSectionId}/${commentToSend.commentId}")
            .setValue(commentToSend).addOnCompleteListener {
                commentInputEditText!!.text.clear()

                //Soft Hide Keypad
                val imm =
                    this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(commentInputEditText?.windowToken, 0)

            }
    }

    private fun initUI() {
        getGoalUserName()
        setGoalTitleAndDescription()
        getSupportersCountAndStatus()
        getComments()
    }

    private fun setGoalTitleAndDescription() {
        goalTitleLabel!!.text = currentGoal!!.goalTitle
        goalDescriptionLabel!!.text = currentGoal!!.goalDescription
    }


    fun getGoalUserName() {
        val currentUserGoalDB =
            FirebaseDatabase.getInstance()
                .reference.child("users/${currentGoal!!.userId}")

        val getUserNameListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userFullName =
                    "${p0.child("firstName").value.toString()} ${p0.child("lastName").value.toString()}"
                goalUserFullNameLabel!!.text = userFullName
            }

        }

        currentUserGoalDB.addListenerForSingleValueEvent(getUserNameListener)
    }

    private fun getSupportersCountAndStatus() {

        val currentUserGoalSupportDB =
            FirebaseDatabase.getInstance()
                .reference.child("supports/${currentGoal!!.userId}/goals/${currentGoal!!.goalId}")

        val getSupportCountListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    supportCountLabel!!.text = "${p0.children.count()} Supporters"

                    //If current user is support this goal, mark as supported
                    currentUserSupportCurrentGoalFlag = p0.child(currentUser!!.uid).exists()

                    if (currentUserSupportCurrentGoalFlag!!)
                        supportIcon!!.setImageResource(R.drawable.support_full_ic)
                    else {
                        supportIcon!!.setImageResource(R.drawable.support_empty_ic)
                    }

                    supportCountLabel!!.visibility = View.VISIBLE

                } else {

                    supportCountLabel!!.visibility = View.GONE

                    supportIcon!!.setImageResource(R.drawable.support_empty_ic)

                    currentUserSupportCurrentGoalFlag = false
                }

            }

        }

        currentUserGoalSupportDB.addListenerForSingleValueEvent(getSupportCountListener)
    }

    private fun getComments() {
        val currentUserGoalCommentsDB =
            FirebaseDatabase.getInstance()
                .reference.child("comments/${currentGoal!!.userId}/goals/${currentGoal!!.goalId}/${currentGoal!!.commentSectionId}")

        val getCommentsCountListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                commentsArrayList!!.clear()

                if (p0.exists()) {
                    //Get Comment Count
                    commentCountLabel!!.text = "${p0.children.count()} Comments"
                    commentCountLabel!!.visibility = View.VISIBLE

                    noCommentsLayout!!.visibility = View.GONE
                    commentsRecyclerView!!.visibility = View.VISIBLE

                    //Get Comments
                    for (comment in p0.children) {

                        val commentMap = comment.value as HashMap<*, *>
                        val commentObject = Comment(
                            commentMap["commentId"].toString(),
                            commentMap["commentSectionId"].toString(),
                            commentMap["commentUserId"].toString(),
                            commentMap["commentText"].toString(),
                            commentMap["datePosted"].toString()
                        )

                        commentsArrayList!!.add(commentObject)

                    }

                    commentsAdapter!!.sortByAsc()
                    commentsAdapter!!.notifyDataSetChanged()
                } else {
                    commentCountLabel!!.visibility = View.GONE

                    noCommentsLayout!!.visibility = View.VISIBLE
                    commentsRecyclerView!!.visibility = View.GONE
                }
            }

        }

        currentUserGoalCommentsDB.addValueEventListener(getCommentsCountListener)
    }
}
