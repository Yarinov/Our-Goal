package com.yarinov.ourgoal.user.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.suke.widget.SwitchButton
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    var defaultLayout: LinearLayout? = null
    var editLayout: CardView? = null

    var userInProfileId: String? = null
    var currentUserProfileFlag: Boolean? = null

    var profileUserNameLabel: TextView? = null
    var profileUserInfoLabel: TextView? = null
    var goalsTitleLabel: TextView? = null

    var editProfileLabel: TextView? = null
    var profileImage: CircleImageView? = null

    var friendAndFollowStatusSection: LinearLayout? = null
    var friendStatusLabel: TextView? = null
    var followStatusLabel: TextView? = null
    var friendStatusIcon: ImageView? = null
    var followStatusIcon: ImageView? = null

    var userInProfileGoalsList: ArrayList<Goal>? = null
    lateinit var goalsInProfileAdapter: GoalsInProfileAdapter
    var inProfileGoalsList: RecyclerView? = null

    var currentUser: FirebaseUser? = null

    var friendPathFlag: Boolean? = null
    var blockedPathFlag: Boolean? = null
    var friendRequestSentPathFlag: Boolean? = null
    var friendRequestReceivedPathFlag: Boolean? = null
    var friendFollowFlag: Boolean? = null
    var userFollowFlag: Boolean? = null

    var friendStatusSection: LinearLayout? = null
    var followStatusSection: LinearLayout? = null

    lateinit var userFirstName: String
    lateinit var userLastName: String
    lateinit var userInfo: String
    lateinit var userEmail: String

    //FriendSectionOptionMenu
    var removeFriendInFriendSectionOptionMenu: TextView? = null
    var cancelFriendRequestInFriendSectionOptionMenu: TextView? = null
    var cancelInFriendSectionOptionMenu: TextView? = null

    //Edit Profile Mood
    var inEditProfileMoodFlag = false
    //Name section
    var currentUserFullNameInEditProfileMoodLabel: TextView? = null
    var currentUserFirstNameInEditProfileMoodEditView: TextView? = null
    var currentUserLastNameInEditProfileMoodEditView: TextView? = null
    var nameEditLayout: LinearLayout? = null
    var changeNameEditLayout: LinearLayout? = null
    var changeNameButton: Button? = null
    //Info section
    var currentUserInfoInEditProfileMoodLabel: TextView? = null
    var currentUserInfoInEditProfileMoodEditView: EditText? = null
    var infoEditLayout: LinearLayout? = null
    var changeInfoEditLayout: LinearLayout? = null
    var changeInfoButton: Button? = null
    //Email section
    var currentUserEmailInEditProfileMoodLabel: TextView? = null
    var currentUserEmailInEditProfileMoodEditView: EditText? = null
    var emailEditLayout: LinearLayout? = null
    var changeEmailEditLayout: LinearLayout? = null
    var changeEmailButton: Button? = null
    //Password Section
    var passwordEditLayout: LinearLayout? = null
    var changePasswordEditLayout: LinearLayout? = null
    var changePasswordButton: Button? = null
    var changePasswordCurrentPasswordEditView: EditText? = null
    var changePasswordNewPasswordEditView: EditText? = null
    var changePasswordReNewPasswordEditView: EditText? = null
    //Account Privacy Section
    var privacySwitchButton: SwitchButton? = null
    var privateAccountFlag: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var userAchievementCardView = findViewById<CardView>(R.id.userAchievementCardView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            userAchievementCardView.outlineSpotShadowColor = Color.parseColor("#D56D6E")
        }
        currentUser = FirebaseAuth.getInstance().currentUser

        userInProfileGoalsList = ArrayList()
        goalsInProfileAdapter = GoalsInProfileAdapter(this, userInProfileGoalsList!!)

        defaultLayout = findViewById(R.id.defaultLayout)
        editLayout = findViewById(R.id.editLayout)

        profileUserNameLabel = findViewById(R.id.profileUserNameLabel)
        profileUserInfoLabel = findViewById(R.id.profileUserInfoLabel)
        goalsTitleLabel = findViewById(R.id.goalsTitleLabel)
        inProfileGoalsList = findViewById(R.id.inProfileGoalsList)
        editProfileLabel = findViewById(R.id.editProfileLabel)
        friendAndFollowStatusSection = findViewById(R.id.friendAndFollowStatusSection)
        friendStatusLabel = findViewById(R.id.friendStatusLabel)
        followStatusLabel = findViewById(R.id.followStatusLabel)
        friendStatusIcon = findViewById(R.id.friendStatusIcon)
        followStatusIcon = findViewById(R.id.followStatusIcon)
        profileImage = findViewById(R.id.profileImage)
        friendStatusSection = findViewById(R.id.friendStatusSection)
        followStatusSection = findViewById(R.id.followStatusSection)

        //Edit profile mood
        //Name section
        currentUserFullNameInEditProfileMoodLabel =
            findViewById(R.id.currentUserFullNameInEditProfileMoodLabel)
        currentUserFirstNameInEditProfileMoodEditView =
            findViewById(R.id.currentUserFirstNameInEditProfileMoodEditView)
        currentUserLastNameInEditProfileMoodEditView =
            findViewById(R.id.currentUserLastNameInEditProfileMoodEditView)
        nameEditLayout = findViewById(R.id.nameEditLayout)
        changeNameEditLayout = findViewById(R.id.changeNameEditLayout)
        changeNameButton = findViewById(R.id.changeNameButton)

        //Info section
        currentUserInfoInEditProfileMoodLabel =
            findViewById(R.id.currentUserInfoInEditProfileMoodLabel)
        currentUserInfoInEditProfileMoodEditView =
            findViewById(R.id.currentUserInfoInEditProfileMoodEditView)
        infoEditLayout = findViewById(R.id.infoEditLayout)
        changeInfoEditLayout = findViewById(R.id.changeInfoEditLayout)
        changeInfoButton = findViewById(R.id.changeInfoButton)

        //Email section
        currentUserEmailInEditProfileMoodLabel =
            findViewById(R.id.currentUserEmailInEditProfileMoodLabel)
        currentUserEmailInEditProfileMoodEditView =
            findViewById(R.id.currentUserEmailInEditProfileMoodEditView)
        emailEditLayout = findViewById(R.id.emailEditLayout)
        changeEmailEditLayout = findViewById(R.id.changeEmailEditLayout)
        changeEmailButton = findViewById(R.id.changeEmailButton)

        //Password Section
        passwordEditLayout = findViewById(R.id.passwordEditLayout)
        changePasswordEditLayout = findViewById(R.id.changePasswordEditLayout)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        changePasswordCurrentPasswordEditView =
            findViewById(R.id.changePasswordCurrentPasswordEditView)
        changePasswordNewPasswordEditView = findViewById(R.id.changePasswordNewPasswordEditView)
        changePasswordReNewPasswordEditView = findViewById(R.id.changePasswordReNewPasswordEditView)

        //Account Privacy Section
        privacySwitchButton = findViewById(R.id.privacySwitchButton)


        var extra = intent.extras
        userInProfileId = extra!!.getString("userId")

        //Check if the current user is in his profile
        currentUserProfileFlag = currentUser?.uid.equals(userInProfileId)

        //If current user went to his profile, disable 'Follow', 'Add friend' and show edit profile
        if (currentUserProfileFlag!!) {

            //Show edit profile
            editProfileLabel!!.visibility = View.VISIBLE

            //hide add friend and follow section
            friendAndFollowStatusSection!!.visibility = View.GONE
        }

        getUserInProfileData()

        inProfileGoalsList?.adapter = goalsInProfileAdapter
        inProfileGoalsList?.layoutManager = LinearLayoutManager(this)

        AdapterUtils().loadUserProfilePic(profileImage, userInProfileId!!)

        friendStatusSection!!.setOnClickListener {
            friendSectionPressed()
        }

        followStatusSection!!.setOnClickListener {
            followSectionPressed()
        }


        editProfileLabel!!.setOnClickListener {
            editProfileMood()
        }
    }

    private fun setupAccountPrivacy() {

        //Setup the toggle button -> if account is private set the button as 'Active'
        privacySwitchButton!!.isChecked = privateAccountFlag!!

        //TODO Add more private elements here

    }

    private fun editProfileMood() {

        var changeNameFlag = false
        var changeInfoFlag = false
        var changeEmailFlag = false
        var changePasswordFlag = false

        if (!inEditProfileMoodFlag) {

            enterEditProfileMoodUpdateUI()

            //Change name section
            nameEditLayout!!.setOnClickListener {

                changeNameFlag = if (!changeNameFlag) {

                    //Open 'Change name' section
                    fadeInView(changeNameEditLayout!!)

                    changeNameButton!!.setOnClickListener {

                        val newFirstName =
                            currentUserFirstNameInEditProfileMoodEditView!!.text.toString()
                        val newLastName =
                            currentUserLastNameInEditProfileMoodEditView!!.text.toString()

                        val changeFirstAndLastNameAlert = AlertDialog.Builder(this)
                        changeFirstAndLastNameAlert.setMessage("This Action Will Change Your Name, Are You Sure?")
                            .setPositiveButton(
                                "Yes",
                                DialogInterface.OnClickListener { dialog, which ->

                                    //Update new first and last name in db
                                    FirebaseDatabase.getInstance()
                                        .reference.child(
                                        "users/$userInProfileId/firstName"
                                    ).setValue(newFirstName)

                                    FirebaseDatabase.getInstance()
                                        .reference.child(
                                        "users/$userInProfileId/lastName"
                                    ).setValue(newLastName)

                                    //close the change name section
                                    hideKeypad()
                                    getUserInProfileData()
                                    fadeOutView(changeNameEditLayout!!)

                                    changeNameFlag = false

                                }).setNegativeButton(
                                "Cancel", null
                            )

                        changeFirstAndLastNameAlert.create().show()
                    }

                    true
                } else {

                    //Close 'Chane name' section
                    fadeOutView(changeNameEditLayout!!)

                    false
                }

            }

            infoEditLayout!!.setOnClickListener {

                if (!changeInfoFlag) {

                    fadeOutAndInViews(
                        currentUserInfoInEditProfileMoodLabel!!,
                        changeInfoEditLayout!!
                    )

                    changeInfoButton!!.setOnClickListener {
                        val newUserInfo = currentUserInfoInEditProfileMoodEditView!!.text.toString()

                        val changeInfoAlert = AlertDialog.Builder(this)
                        changeInfoAlert.setMessage("This Action Will Change Your Info, Are You Sure?")
                            .setPositiveButton(
                                "Yes",
                                DialogInterface.OnClickListener { dialog, which ->

                                    //Update new user info in db
                                    FirebaseDatabase.getInstance()
                                        .reference.child(
                                        "users/$userInProfileId/userInfo"
                                    ).setValue(newUserInfo)


                                    //close the change info section
                                    hideKeypad()
                                    getUserInProfileData()
                                    fadeOutAndInViews(
                                        changeInfoEditLayout!!,
                                        currentUserInfoInEditProfileMoodLabel!!
                                    )

                                    changeInfoFlag = false

                                }).setNegativeButton(
                                "Cancel", null
                            )

                        changeInfoAlert.create().show()
                    }
                    changeInfoFlag = true
                } else {

                    fadeOutAndInViews(
                        changeInfoEditLayout!!,
                        currentUserInfoInEditProfileMoodLabel!!
                    )

                    changeInfoFlag = false
                }

            }

            emailEditLayout!!.setOnClickListener {

                if (!changeEmailFlag) {

                    fadeInView(changeEmailEditLayout!!)

                    changeEmailButton!!.setOnClickListener {

                        val newUserEmail =
                            currentUserEmailInEditProfileMoodEditView!!.text.toString()

                        //TODO Add confirmation email
                        val changeUserEmailAlert = AlertDialog.Builder(this)
                        changeUserEmailAlert.setMessage("This Action Will Change Your Email, Are You Sure?")
                            .setPositiveButton(
                                "Yes",
                                DialogInterface.OnClickListener { dialog, which ->

                                    //Update new email in db
                                    FirebaseDatabase.getInstance()
                                        .reference.child(
                                        "users/$userInProfileId/userEmail"
                                    ).setValue(newUserEmail)

                                    //Update email in firebase auth
                                    currentUser!!.updateEmail(newUserEmail)

                                    //close the change name section
                                    hideKeypad()
                                    getUserInProfileData()
                                    fadeOutView(changeEmailEditLayout!!)

                                    changeEmailFlag = false

                                }).setNegativeButton(
                                "Cancel", null
                            )

                        changeUserEmailAlert.create().show()
                    }
                    changeEmailFlag = true
                } else {

                    fadeOutView(changeEmailEditLayout!!)

                    changeEmailFlag = false
                }
            }

            passwordEditLayout!!.setOnClickListener {

                if (!changePasswordFlag) {

                    fadeInView(changePasswordEditLayout!!)

                    changePasswordButton!!.setOnClickListener {

                        val currentUserEmail =
                            currentUserEmailInEditProfileMoodLabel!!.text.toString()
                        val oldPassword = changePasswordCurrentPasswordEditView!!.text.toString()
                        val newPassword = changePasswordNewPasswordEditView!!.text.toString()
                        val reNewPassword = changePasswordReNewPasswordEditView!!.text.toString()

                        //Check if current password value is the right one
                        val credential = EmailAuthProvider
                            .getCredential(currentUserEmail, oldPassword)

                        currentUser!!.reauthenticate(credential).addOnCompleteListener {

                            //If its the right password
                            if (it.isSuccessful) {

                                //check if the two new password input is larger then 6 and equal
                                if (newPassword == reNewPassword && newPassword.length >= 6) {
                                    val changeInfoAlert = AlertDialog.Builder(this)
                                    changeInfoAlert.setMessage("This Action Will Change Your Password, Are You Sure?")
                                        .setPositiveButton(
                                            "Yes",
                                            DialogInterface.OnClickListener { dialog, which ->

                                                currentUser!!.updatePassword(newPassword)

                                                //close the change info section
                                                hideKeypad()
                                                getUserInProfileData()
                                                fadeOutView(changePasswordEditLayout!!)

                                                changePasswordFlag = false

                                            }).setNegativeButton(
                                            "Cancel", null
                                        )

                                    changeInfoAlert.create().show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "New Passwords Don't Match Or Too Short(Min 6).",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                Toast.makeText(
                                    this,
                                    "Wrong Current Password Entered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }

                    changePasswordFlag = true
                } else {

                    fadeOutView(changePasswordEditLayout!!)

                    changePasswordFlag = false
                }
            }



            privacySwitchButton!!.setOnCheckedChangeListener { view, isChecked ->

                val alertDialogString =
                    "Change To Public Account?\n Anyone will be able to see your goals."
                if (!isChecked) {

                    val changeAccountPrivacyAlert = AlertDialog.Builder(this)
                    changeAccountPrivacyAlert.setMessage(alertDialogString)
                        .setPositiveButton(
                            "Yes",
                            DialogInterface.OnClickListener { dialog, which ->

                                FirebaseDatabase.getInstance()
                                    .reference.child(
                                    "users/$userInProfileId/privateAccount"
                                ).setValue(false)

                            }).setNegativeButton(
                            "Cancel",
                            DialogInterface.OnClickListener { dialog, which ->

                                //If user cancel switching account privacy return to private state
                                privacySwitchButton!!.isChecked = true
                            })

                    changeAccountPrivacyAlert.create().show()
                } else {

                    FirebaseDatabase.getInstance()
                        .reference.child(
                        "users/$userInProfileId/privateAccount"
                    ).setValue(true)
                }
            }

            inEditProfileMoodFlag = true

        } else {

            exitEditProfileMoodUpdateUI()

            inEditProfileMoodFlag = false

        }


    }

    private fun fadeInView(view: View) {

        view.visibility = View.VISIBLE
        view.alpha = 0.0f

        view.animate()
            .alpha(1.0f)
            .setListener(null)

    }

    private fun fadeOutView(view: View) {

        view.animate()
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    view.visibility = View.GONE
                }
            })

    }

    private fun fadeOutAndInViews(viewToFadeOut: View, viewToFadeIn: View) {

        viewToFadeOut.animate()
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    viewToFadeOut.visibility = View.GONE

                    viewToFadeIn.visibility = View.VISIBLE
                    viewToFadeIn.alpha = 0.0f

                    viewToFadeIn.animate()
                        .alpha(1.0f)
                        .setListener(null)
                }
            })

    }

    private fun hideKeypad() {
        //Soft Hide Keypad
        val view = this.currentFocus
        if (view != null) {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun exitEditProfileMoodUpdateUI() {

        editLayout!!.animate()
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    editLayout!!.visibility = View.GONE

                    defaultLayout!!.visibility = View.VISIBLE
                    defaultLayout!!.alpha = 0.0f

                    defaultLayout!!.animate()
                        .translationX(0f)
                        .alpha(1.0f)
                        .setListener(null)
                }
            })
    }

    private fun enterEditProfileMoodUpdateUI() {

        defaultLayout!!.animate()
            .translationX(-defaultLayout!!.width.toFloat())
            .alpha(0.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    defaultLayout!!.visibility = View.GONE

                    editLayout!!.visibility = View.VISIBLE
                    editLayout!!.alpha = 0.0f

                    editLayout!!.animate()
                        .alpha(1.0f)
                        .setListener(null)
                }
            })

    }


    private fun followSectionPressed() {

        //Setup option menu for follow section
        val optionPopupView = layoutInflater.inflate(
            R.layout.follow_status_options_menu_layout,
            null
        )

        val followUserInFollowSectionOptionMenu: TextView =
            optionPopupView.findViewById(R.id.followUserInFollowSectionOptionMenu)
        val unfollowUserInFollowSectionOptionMenu: TextView =
            optionPopupView.findViewById(R.id.unfollowUserInFollowSectionOptionMenu)
        val cancelInFollowSectionOptionMenu: TextView =
            optionPopupView.findViewById(R.id.cancelInFollowSectionOptionMenu)

        var followSectionOptionMenu = PopupWindow(this)
        followSectionOptionMenu.contentView = optionPopupView
        followSectionOptionMenu.width = LinearLayout.LayoutParams.MATCH_PARENT
        followSectionOptionMenu.height = LinearLayout.LayoutParams.WRAP_CONTENT
        followSectionOptionMenu.isFocusable = true
        followSectionOptionMenu.setBackgroundDrawable(ColorDrawable())
        followSectionOptionMenu.animationStyle = R.style.popup_window_animation_bottom

        cancelInFollowSectionOptionMenu.setOnClickListener {
            followSectionOptionMenu.dismiss()
        }

        if (userFollowFlag!!) {//If current user already following user in profile

            followUserInFollowSectionOptionMenu.visibility = View.GONE

            val root = window.decorView.rootView as ViewGroup
            AdapterUtils().applyDim(root, 0.5f)
            followSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

            followSectionOptionMenu.setOnDismissListener {
                AdapterUtils().clearDim(root)
            }

            //Remove follow
            unfollowUserInFollowSectionOptionMenu.setOnClickListener {

                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                    .removeValue()

                followSectionOptionMenu.dismiss()
            }

        } else {//If current user isn't following user in profile

            unfollowUserInFollowSectionOptionMenu.visibility = View.GONE

            val root = window.decorView.rootView as ViewGroup
            AdapterUtils().applyDim(root, 0.5f)
            followSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

            followSectionOptionMenu.setOnDismissListener {
                AdapterUtils().clearDim(root)
            }

            //Follow user in profile
            followUserInFollowSectionOptionMenu.setOnClickListener {

                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                    .setValue(true)

                followSectionOptionMenu.dismiss()
            }
        }
    }

    private fun friendSectionPressed() {


        //Setup option menu for friend section
        val optionPopupView = layoutInflater.inflate(
            R.layout.friend_status_options_menu_layout,
            null
        )

        removeFriendInFriendSectionOptionMenu =
            optionPopupView.findViewById(R.id.removeFriendInFriendSectionOptionMenu)
        cancelFriendRequestInFriendSectionOptionMenu =
            optionPopupView.findViewById(R.id.cancelFriendRequestInFriendSectionOptionMenu)
        cancelInFriendSectionOptionMenu =
            optionPopupView.findViewById(R.id.cancelInFriendSectionOptionMenu)


        var nonRespondLayout: LinearLayout = optionPopupView.findViewById(R.id.nonRespondLayout)
        var respondLayout: LinearLayout = optionPopupView.findViewById(R.id.respondLayout)

        val acceptFriendRequestLabel: TextView =
            optionPopupView.findViewById(R.id.acceptFriendRequestLabel)
        val deleteFriendRequestLabel: TextView =
            optionPopupView.findViewById(R.id.deleteFriendRequestLabel)

        var friendSectionOptionMenu = PopupWindow(this)
        friendSectionOptionMenu.contentView = optionPopupView
        friendSectionOptionMenu.width = LinearLayout.LayoutParams.MATCH_PARENT
        friendSectionOptionMenu.height = LinearLayout.LayoutParams.WRAP_CONTENT
        friendSectionOptionMenu.isFocusable = true
        friendSectionOptionMenu.setBackgroundDrawable(ColorDrawable())
        friendSectionOptionMenu.animationStyle = R.style.popup_window_animation_bottom

        cancelInFriendSectionOptionMenu!!.setOnClickListener {
            friendSectionOptionMenu.dismiss()
        }

        //If already friend -> open menu
        when {
            friendPathFlag!! -> {

                nonRespondLayout.visibility = View.VISIBLE
                cancelFriendRequestInFriendSectionOptionMenu!!.visibility = View.GONE

                val root = window.decorView.rootView as ViewGroup
                AdapterUtils().applyDim(root, 0.5f)
                friendSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

                friendSectionOptionMenu.setOnDismissListener {
                    AdapterUtils().clearDim(root)
                }

                //Remove friend
                removeFriendInFriendSectionOptionMenu!!.setOnClickListener {

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friends/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friends/${currentUser!!.uid}")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/follow/${currentUser!!.uid}")
                        .removeValue()

                    friendSectionOptionMenu.dismiss()

                }

            }
            friendRequestSentPathFlag!! -> { //if current user already sent friend request -> open menu

                nonRespondLayout.visibility = View.VISIBLE
                removeFriendInFriendSectionOptionMenu!!.visibility = View.GONE

                val root = window.decorView.rootView as ViewGroup
                AdapterUtils().applyDim(root, 0.5f)
                friendSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

                friendSectionOptionMenu.setOnDismissListener {
                    AdapterUtils().clearDim(root)
                }

                //Cancel friend request
                cancelFriendRequestInFriendSectionOptionMenu!!.setOnClickListener {

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friend_request/sent/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friend_request/received/${currentUser!!.uid}")
                        .removeValue()

                    friendSectionOptionMenu.dismiss()
                }

            }
            friendRequestReceivedPathFlag!! -> {//if current user received from user in profile a friend request

                respondLayout.visibility = View.VISIBLE

                val root = window.decorView.rootView as ViewGroup
                AdapterUtils().applyDim(root, 0.5f)
                friendSectionOptionMenu.showAtLocation(optionPopupView, Gravity.BOTTOM, 0, 0)

                friendSectionOptionMenu.setOnDismissListener {
                    AdapterUtils().clearDim(root)
                }

                //Accept friend request
                acceptFriendRequestLabel.setOnClickListener {

                    //Remove request from current user DB
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friend_request/received/$userInProfileId")
                        .removeValue()

                    //Remove request from the user who sent the friend request
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friend_request/sent/${currentUser!!.uid}")
                        .removeValue()

                    //Adding the connection to both users DB
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friends/$userInProfileId")
                        .setValue(true)

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friends/${currentUser!!.uid}")
                        .setValue(true)

                    //Add follow to both user
                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/follow/$userInProfileId")
                        .setValue(true)

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/follow/${currentUser!!.uid}")
                        .setValue(true)

                    friendSectionOptionMenu.dismiss()
                }

                //Delete friend request
                deleteFriendRequestLabel.setOnClickListener {

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/${currentUser!!.uid}/friend_request/received/$userInProfileId")
                        .removeValue()

                    FirebaseDatabase.getInstance()
                        .reference.child("connections/$userInProfileId/friend_request/sent/${currentUser!!.uid}")
                        .removeValue()


                    friendSectionOptionMenu.dismiss()
                }


            }
            else -> {//No connection between current user and user in profile

                //Send friend request
                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}/friend_request/sent/$userInProfileId")
                    .setValue(true)

                FirebaseDatabase.getInstance()
                    .reference.child("connections/$userInProfileId/friend_request/received/${currentUser!!.uid}")
                    .setValue(true)

            }
        }

    }

    private fun getUserInProfileData() {

        userInProfileGoalsList?.clear()

        //Get the user first name, last name and info
        val profileUserDB =
            FirebaseDatabase.getInstance().reference.child("users/$userInProfileId")

        val getProfileUserDataListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                userFirstName = p0.child("firstName").value.toString()
                userLastName = p0.child("lastName").value.toString()
                userInfo = p0.child("userInfo").value.toString()
                userEmail = p0.child("userEmail").value.toString()
                privateAccountFlag = p0.child("privateAccount").value as Boolean

                setupAccountPrivacy()

                profileUserNameLabel!!.text = "$userFirstName $userLastName"
                profileUserInfoLabel!!.text = userInfo

                //Set full name and info in edit profile mood
                currentUserFullNameInEditProfileMoodLabel!!.text = "$userFirstName $userLastName"
                currentUserFirstNameInEditProfileMoodEditView!!.text = userFirstName
                currentUserLastNameInEditProfileMoodEditView!!.text = userLastName

                currentUserInfoInEditProfileMoodLabel!!.text = userInfo
                currentUserInfoInEditProfileMoodEditView!!.setText(userInfo)

                currentUserEmailInEditProfileMoodLabel!!.text = userEmail

                if (currentUserProfileFlag!!) {
                    goalsTitleLabel!!.text = "My Goals"
                } else {
                    goalsTitleLabel!!.text = "$userFirstName's Goals"
                }
            }

        }

        profileUserDB.addListenerForSingleValueEvent(getProfileUserDataListener)

        //Get user's goals
        val profileUserGoalsDB =
            FirebaseDatabase.getInstance().reference.child("goals/$userInProfileId")

        val getProfileUserGoalsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                userInProfileGoalsList?.clear()

                if (p0.exists()) {//If user in profile have goals

                    for (goal in p0.children) {

                        val userGoalMap = goal.value as HashMap<*, *>
                        val userGoal = Goal(
                            userGoalMap["goalId"].toString(),
                            userGoalMap["goalTitle"].toString(),
                            userGoalMap["goalDescription"].toString(),
                            userGoalMap["goalProgress"] as Long,
                            userGoalMap["goalSteps"] as Long,
                            userGoalMap["commentSectionId"].toString(),
                            userGoalMap["goalStatus"].toString(),
                            userGoalMap["datePosted"].toString(),
                            userGoalMap["userId"].toString()
                        )

                        userInProfileGoalsList?.add(userGoal)
                    }

                } else {//If user in profile dont have goals
                    //TODO Add 'No goals' label
                }

                goalsInProfileAdapter.notifyDataSetChanged()
            }

        }

        profileUserGoalsDB.addListenerForSingleValueEvent(getProfileUserGoalsListener)

        if (!currentUserProfileFlag!!) {//If current user isn't the user in the profile

            //Get relationship between current user to user in profile
            val currentUserDB =
                FirebaseDatabase.getInstance()
                    .reference.child("connections/${currentUser!!.uid}")

            val getRelationshipListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    friendPathFlag = p0.child("friends/$userInProfileId").exists()
                    blockedPathFlag = p0.child("blocked/$userInProfileId").exists()
                    friendRequestSentPathFlag =
                        p0.child("friend_request/sent/$userInProfileId").exists()
                    friendRequestReceivedPathFlag =
                        p0.child("friend_request/received/$userInProfileId").exists()
                    friendFollowFlag = p0.child("hidden_friends/$userInProfileId").exists()
                    userFollowFlag = p0.child("follow/$userInProfileId").exists()

                    //Check if friends
                    when {
                        friendPathFlag!! -> setFriendSection(1)
                        friendRequestSentPathFlag!! -> setFriendSection(2)
                        friendRequestReceivedPathFlag!! -> setFriendSection(3)
                        blockedPathFlag!! -> setFriendSection(-1)
                        else -> setFriendSection(0)
                    }

                    //Check follow status.
                    if (userFollowFlag!!) {
                        setFollowSection(1)
                    } else {

                        setFollowSection(0)
                    }
                }

            }

            currentUserDB.addValueEventListener(getRelationshipListener)

        }
    }

    private fun setFollowSection(i: Int) {

        //0 - Not Following | 1 - Following

        if (i == 1) {
            followStatusLabel!!.text = "Following"

            ImageViewCompat.setImageTintList(
                followStatusIcon!!,
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary))
            )
        } else if (i == 0) {
            followStatusLabel!!.text = "Follow"

            ImageViewCompat.setImageTintList(
                followStatusIcon!!,
                ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.black))
            )
        }

    }

    private fun setFriendSection(i: Int) {

        //0 - None | 1 - Friends | 2 - Sent friend request | 3 - Friend request received | -1 - Blocked

        when (i) {
            0 -> {
                friendStatusLabel!!.text = "Add Friend"
                friendStatusIcon!!.setImageResource(R.drawable.none_friend_status_ic)
            }
            1 -> {
                friendStatusLabel!!.text = "Friends"
                friendStatusIcon!!.setImageResource(R.drawable.accepted_friend_status_ic)
            }
            2 -> {
                friendStatusLabel!!.text = "Friend Request Sent"
                friendStatusIcon!!.setImageResource(R.drawable.wating_friend_status_ic)
            }
            3 -> {
                friendStatusLabel!!.text = "Respond"
                friendStatusIcon!!.setImageResource(R.drawable.wating_friend_status_ic)
            }
        }

    }
}
