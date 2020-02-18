package com.yarinov.ourgoal.user.mentions_auto_complete

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.CharPolicy
import com.yarinov.ourgoal.goal.Goal
import com.yarinov.ourgoal.user.User


class MentionsAutoComplete(
    var currentUserId: String,
    var currentGoal: Goal,
    var editTextView: EditText,
    var usersIdList: ArrayList<String>,
    var context: Context
) {

    var mentionsAutocomplete: Autocomplete<User>
    val elevation: Float
    val backgroundDrawable: ColorDrawable
    val policy: CharPolicy
    val presenter: UserPresenter
    val callback: AutocompleteCallback<User>
    var mentionUsersList: ArrayList<User>


    init {
        this.elevation = 6f
        this.backgroundDrawable = ColorDrawable(Color.WHITE)
        this.policy = CharPolicy('@')

        this.mentionUsersList = ArrayList()
        convertUsersIdToUserObject()

        this.presenter = UserPresenter(mentionUsersList, context)

        this.callback = object : AutocompleteCallback<User> {
            override fun onPopupItemClicked(
                editable: Editable,
                item: User
            ): Boolean { // Replace query text with the full name.
                val range = CharPolicy.getQueryRange(editable) ?: return false
                val start = range[0]
                val end = range[1]
                val replacement: String = item.getUserFullName()
                editable.replace(start, end, replacement)
                // This is better done with regexes and a TextWatcher, due to what happens when
                // the user clears some parts of the text. Up to you.
                editable.setSpan(
                    StyleSpan(Typeface.BOLD), start, start + replacement.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                return true
            }

            override fun onPopupVisibilityChanged(shown: Boolean) {}
        }

        this.mentionsAutocomplete = Autocomplete.on<User>(editTextView)
            .with(elevation)
            .with(backgroundDrawable)
            .with(policy)
            .with(presenter)
            .with(callback)
            .build()
    }

    private fun convertUsersIdToUserObject() {

        //For each id in the list get the user data
        for (userId in this.usersIdList) {

            val userDatabase = FirebaseDatabase.getInstance().reference.child("users/$userId")

            val getUserDataListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(p0: DataSnapshot) {

                    val userToAdd = User(
                        p0.child("userId").value.toString(),
                        p0.child("userEmail").value.toString(),
                        p0.child("firstName").value.toString(),
                        p0.child("lastName").value.toString(),
                        p0.child("userImageUri").value.toString()
                    )

                    mentionUsersList.add(userToAdd)

                }

            }

            userDatabase.addListenerForSingleValueEvent(getUserDataListener)
        }
    }
}