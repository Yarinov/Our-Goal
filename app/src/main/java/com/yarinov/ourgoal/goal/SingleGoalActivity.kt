package com.yarinov.ourgoal.goal

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.comment.Comment
import com.yarinov.ourgoal.goal.comment.CommentAdapter
import com.yarinov.ourgoal.goal.milestone.GoalMilestone
import com.yarinov.ourgoal.goal.milestone.MilestoneTitleAdapter
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SingleGoalActivity : AppCompatActivity() {

    var currentGoal: Goal? = null
    var currentGoalMilestoneNumber: Long? = null
    var currentUser: FirebaseUser? = null

    var commentsRecyclerView: RecyclerView? = null
    var commentsArrayList: ArrayList<Comment>? = null
    var commentsAdapter: CommentAdapter? = null

    var commentInputEditText: EditText? = null
    var sendCommentButton: Button? = null

    var goalUserFullNameLabel: TextView? = null
    var goalDescriptionLabel: TextView? = null
    var goalTitleLabel: TextView? = null
    var inGoalProfilePic: CircleImageView? = null

    var supportCountLabel: TextView? = null
    var commentCountLabel: TextView? = null

    var miniCube: CardView? = null
    var miniCubeIcon: ImageView? = null
    var miniCubeLabel: TextView? = null

    var titleInEditMood: EditText? = null
    var descriptionInEditMood: EditText? = null

    var currentUserSupportCurrentGoalFlag: Boolean? = null
    var currentUserGoalFlag: Boolean? = null

    var noCommentsLayout: LinearLayout? = null

    var currentGoalMilestonesTitle: ArrayList<GoalMilestone>? = null
    var milestonesRecyclerView: RecyclerView? = null
    var milestoneTitleAdapter: MilestoneTitleAdapter? = null

    var commentSectionLayout: LinearLayout? = null

    var inEditMoodFlag: Boolean = false
    var changedGoalTitleFlag: Boolean = false
    var changedGoalDescriptionFlag: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_goal)

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        goalUserFullNameLabel = findViewById(R.id.goalUserFullNameLabel)
        goalDescriptionLabel = findViewById(R.id.goalDescriptionLabel)
        goalTitleLabel = findViewById(R.id.goalTitleLabel)
        supportCountLabel = findViewById(R.id.supportCountLabel)
        commentCountLabel = findViewById(R.id.commentCountLabel)
        commentInputEditText = findViewById(R.id.commentInputEditText)
        sendCommentButton = findViewById(R.id.sendCommentButton)

        miniCube = findViewById(R.id.miniCube)
        miniCubeIcon = findViewById(R.id.miniCubeIcon)
        miniCubeLabel = findViewById(R.id.miniCubeLabel)

        titleInEditMood = findViewById(R.id.titleInEditMood)
        descriptionInEditMood = findViewById(R.id.descriptionInEditMood)

        milestonesRecyclerView = findViewById(R.id.milestonesRecyclerView)
        noCommentsLayout = findViewById(R.id.noCommentsLayout)
        commentSectionLayout = findViewById(R.id.commentSectionLayout)
        inGoalProfilePic = findViewById(R.id.inGoalProfilePic)

        //init current user
        currentUser = FirebaseAuth.getInstance().currentUser

        //init support flag as false
        currentUserSupportCurrentGoalFlag = false

        val extra = intent.extras
        currentGoal = extra!!.getParcelable("currentGoal")

        //Check if this goal is current user goal
        currentUserGoalFlag = (currentUser!!.uid == currentGoal!!.userId)

        /*
        Change mini cube.
        If current user create this goal change mini cube to 'Edit Goal' Section.
        Else keep the mini cube as 'Support' Section
         */
        if (currentUserGoalFlag!!) {
            miniCubeLabel!!.text = "Edit Goal"
            miniCubeIcon!!.setImageResource(R.drawable.edit_goal_ic)

        }

        currentGoalMilestoneNumber = if (currentGoal!!.goalProgress == 100.toLong()) {
            currentGoal!!.goalSteps
        } else {
            val stepWeight = 100 / (currentGoal!!.goalSteps + 1)
            currentGoal!!.goalProgress / stepWeight
        }

        setupStepView()

        //Milestones RecyclerView
        currentGoalMilestonesTitle = ArrayList()
        milestoneTitleAdapter = MilestoneTitleAdapter(
            this,
            currentGoalMilestonesTitle!!,
            currentGoal!!,
            currentGoalMilestoneNumber!!,
            commentSectionLayout!!
        )
        milestonesRecyclerView!!.layoutManager = LinearLayoutManager(this)
        milestonesRecyclerView!!.adapter = milestoneTitleAdapter
        milestonesRecyclerView!!.hasFixedSize()


        //Comments RecyclerView
        commentsArrayList = ArrayList()
        commentsAdapter = CommentAdapter(this, commentsArrayList!!, currentUser!!.uid)

        commentsRecyclerView!!.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView!!.adapter = commentsAdapter

        sendCommentButton!!.setOnClickListener {
            sendComment()
        }

        miniCube!!.setOnClickListener {

            if (currentUser!!.uid != currentGoal!!.userId)
                supportPressed()
            else
                editGoal()
        }

        initUI()
    }

    private fun editGoal() {

        if (!inEditMoodFlag) {//enter edit mood

            inEditMoodFlag = true

            //Change mini cube style
            var miniCubeEditColor = Color.parseColor("#C1D95C")
            miniCube!!.setCardBackgroundColor(miniCubeEditColor)
            miniCubeLabel!!.text = "Done"
            miniCubeIcon!!.setImageResource(R.drawable.accept_ic)

            //Open full goal's milestone list
            milestoneTitleAdapter!!.inEditMood()

            //Replace Title label and description label with editText
            goalTitleLabel!!.visibility = View.GONE
            titleInEditMood!!.setText(goalTitleLabel!!.text.toString())
            titleInEditMood!!.visibility = View.VISIBLE

            goalDescriptionLabel!!.visibility = View.GONE
            descriptionInEditMood!!.setText(goalDescriptionLabel!!.text.toString())
            descriptionInEditMood!!.visibility = View.VISIBLE
        } else {//Exit from edit mood

            inEditMoodFlag = false

            var miniCubeEditColor = Color.parseColor("#D56D6E")
            miniCube!!.setCardBackgroundColor(miniCubeEditColor)
            miniCubeLabel!!.text = "Edit Goal"
            miniCubeIcon!!.setImageResource(R.drawable.edit_goal_ic)

            changedGoalTitleFlag =
                (titleInEditMood!!.text.toString() != goalTitleLabel!!.text.toString())

            changedGoalDescriptionFlag =
                (descriptionInEditMood!!.text.toString() != goalDescriptionLabel!!.text.toString())

            getUpdatedGoalOnExitEditMood(changedGoalTitleFlag, changedGoalDescriptionFlag)


        }


    }

    fun getUpdatedGoalFromDB() {

        val view = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        val goalDB =
            FirebaseDatabase.getInstance()
                .reference.child("goals/${currentGoal!!.userId}/${currentGoal!!.goalId}")

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

                milestoneTitleAdapter!!.notifyDataSetChanged()

                titleInEditMood!!.visibility = View.GONE
                goalTitleLabel!!.visibility = View.VISIBLE

                descriptionInEditMood!!.visibility = View.GONE
                goalDescriptionLabel!!.visibility = View.VISIBLE

                milestoneTitleAdapter!!.exitEditMood()
            }

        }

        goalDB.addListenerForSingleValueEvent(getNewGoalDataListener)

    }

    private fun getUpdatedGoalOnExitEditMood(
        changedGoalTitleFlag: Boolean,
        changedGoalDescriptionFlag: Boolean
    ) {

        var alertDialogFlag = changedGoalDescriptionFlag || changedGoalTitleFlag

        var alertDialogString = ""

        if (alertDialogFlag) {

            if (changedGoalTitleFlag && changedGoalDescriptionFlag) {
                alertDialogString =
                    "This Action Will Change The Goal Description And Title, Are You Sure?"
            } else if (changedGoalTitleFlag) {
                alertDialogString = "This Action Will Change The Goal Title, Are You Sure?"
            } else if (changedGoalDescriptionFlag) {
                alertDialogString = "This Action Will Change The Goal Description, Are You Sure?"
            }

            val changeTitleAndDescriptionAlert = AlertDialog.Builder(this)
            changeTitleAndDescriptionAlert.setMessage(alertDialogString)
                .setPositiveButton(
                    "Yes",
                    DialogInterface.OnClickListener { dialog, which ->

                        //Update the Goal Title and description DB

                        if (changedGoalTitleFlag && changedGoalDescriptionFlag) {

                            goalTitleLabel!!.text = titleInEditMood!!.text.toString()
                            goalDescriptionLabel!!.text = descriptionInEditMood!!.text.toString()

                            FirebaseDatabase.getInstance()
                                .reference.child(
                                "goals/${currentGoal!!.userId}/${currentGoal!!.goalId}" +
                                        "/goalTitle"
                            )
                                .setValue(titleInEditMood!!.text.toString())

                            FirebaseDatabase.getInstance()
                                .reference.child(
                                "goals/${currentGoal!!.userId}/${currentGoal!!.goalId}" +
                                        "/goalDescription"
                            )
                                .setValue(descriptionInEditMood!!.text.toString())

                            getUpdatedGoalFromDB()

                        } else if (changedGoalTitleFlag) {
                            FirebaseDatabase.getInstance()
                                .reference.child(
                                "goals/${currentGoal!!.userId}/${currentGoal!!.goalId}" +
                                        "/goalTitle"
                            )
                                .setValue(titleInEditMood!!.text.toString()).addOnCompleteListener {
                                    getUpdatedGoalFromDB()
                                }

                            goalTitleLabel!!.text = titleInEditMood!!.text.toString()
                        } else if (changedGoalDescriptionFlag) {
                            FirebaseDatabase.getInstance()
                                .reference.child(
                                "goals/${currentGoal!!.userId}/${currentGoal!!.goalId}" +
                                        "/goalDescription"
                            )
                                .setValue(descriptionInEditMood!!.text.toString())
                                .addOnCompleteListener {
                                    getUpdatedGoalFromDB()
                                }

                            goalDescriptionLabel!!.text = descriptionInEditMood!!.text.toString()
                        }


                    }).setNegativeButton("Cancel", null)

            changeTitleAndDescriptionAlert.create().show()

        } else {

            getUpdatedGoalFromDB()
        }
    }

    private fun loadUserProfilePic() {
        val storage = FirebaseStorage.getInstance()

        val gsReference =
            storage.getReferenceFromUrl("gs://ourgoal-ebee9.appspot.com/users/profile_pic/${currentGoal!!.userId}.jpg")

        gsReference.downloadUrl
            .addOnSuccessListener {

                Picasso.get().load(it).placeholder(R.drawable.default_user_ic).noFade()
                    .into(inGoalProfilePic)

            }
            .addOnFailureListener { exception ->
                val errorCode = (exception as StorageException).errorCode
                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    //Not Found
                }
            }
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

                if (p0.exists()) {//Goal have milestone/s
                    for (milestone in p0.children) {

                        val currentMileId =
                            milestone.child("goalMilestoneId").value.toString()
                        val currentMileTitle =
                            milestone.child("goalMilestoneTitle").value.toString()
                        val currentMileOrder =
                            milestone.child("goalMilestoneOrder").value.toString()

                        currentGoalMilestonesTitle!!.add(
                            GoalMilestone(
                                currentMileId,
                                currentMileTitle,
                                currentMileOrder.toInt()
                            )
                        )

                    }
                } else {//If goal have no milestone add 'Starting Point'


                }

                currentGoalMilestonesTitle!!.add(
                    GoalMilestone(
                        "",
                        currentGoal!!.goalTitle,
                        currentGoalMilestonesTitle!!.size + 1
                    )
                )

                currentGoalMilestonesTitle!!.sortBy { it.goalMilestoneOrder }

                milestoneTitleAdapter!!.notifyDataSetChanged()
            }

        }
        currentGoalMilestonesDB.addValueEventListener(getGoalMilestonesListener)


    }

    private fun supportPressed() {

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

                //Move to last comment posted
                commentsRecyclerView!!.smoothScrollToPosition(commentsArrayList!!.size)

            }
    }

    private fun initUI() {
        getGoalUserName()
        setGoalTitleAndDescription()
        getSupportersCountAndStatus()
        getComments()
        loadUserProfilePic()
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

                    if (currentUserSupportCurrentGoalFlag!! && !currentUserGoalFlag!!)
                        miniCubeIcon!!.setImageResource(R.drawable.support_full_ic)
                    else if (!currentUserGoalFlag!!) {
                        miniCubeIcon!!.setImageResource(R.drawable.support_empty_ic)
                    }

                    supportCountLabel!!.visibility = View.VISIBLE

                } else {

                    supportCountLabel!!.visibility = View.GONE

                    if (!currentUserGoalFlag!!) {
                        miniCubeIcon!!.setImageResource(R.drawable.support_empty_ic)

                        currentUserSupportCurrentGoalFlag = false
                    }

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
