package com.yarinov.ourgoal.connection.friend_request

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User
import com.yarinov.ourgoal.user.profile.ProfileActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.Comparator

class FriendRequestAdapter(
    private val context: Context,
    private var friendRequestsList: ArrayList<User>,
    private val currentUserId: String
) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.friend_request_layout, parent, false)


        return ViewHolder(view)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == friendRequestsList.size - 1)
            holder.divider!!.visibility = View.GONE


        holder.userNameLabel!!.text =
            "${friendRequestsList[position].firstName} ${friendRequestsList[position].lastName}"
        //holder.userNameLabel!!.isSelected = true


        holder.userNameLabel!!.setOnClickListener {
            var moveToUserIntent = Intent(context, ProfileActivity::class.java)
            moveToUserIntent.putExtra("userId", friendRequestsList[position].userId)
            context.startActivity(moveToUserIntent)
        }


        //Get show pic (if exits) TODO Return to this part later
//        val storage = FirebaseStorage.getInstance()
//
//        val gsReference =
//            storage.getReferenceFromUrl("gs://random-episode-generator.appspot.com/$showNameLowerCase.jpg")
//
//        gsReference.downloadUrl
//            .addOnSuccessListener {
//
//                Picasso.get().load(it).noPlaceholder().into(holder.showImg)
//
//            }
//            .addOnFailureListener { exception ->
//                val errorCode = (exception as StorageException).errorCode
//                if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
//                    //Not Found
//                }
//            }

    }


    override fun getItemCount(): Int {
        return friendRequestsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): User {
        return friendRequestsList[position]
    }


    fun sortByAsc() {
        val comparator: Comparator<User> =
            Comparator { object1: User, object2: User ->
                object1.firstName.compareTo(object2.firstName, true)
            }
        Collections.sort(friendRequestsList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var userNameLabel: TextView? = null
        var searchUserProfilePic: CircleImageView? = null

        var resultSearchUserView: LinearLayout? = null
        var divider: View? = null

        var acceptButton: Button? = null
        var denyButton: Button? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            searchUserProfilePic = mView.findViewById(R.id.searchUserProfilePic) as CircleImageView

            resultSearchUserView = mView.findViewById(R.id.resultSearchUserView) as LinearLayout
            divider = mView.findViewById(R.id.divider) as View

            acceptButton = mView.findViewById(R.id.acceptButton) as Button
            denyButton = mView.findViewById(R.id.denyButton) as Button

            //If current user accept the friend request
            acceptButton!!.setOnClickListener {

                //Remove request from current user DB
                FirebaseDatabase.getInstance()
                    .reference.child("connections/$currentUserId/friend_request/received/${friendRequestsList[position].userId}")
                    .removeValue()

                //Remove request from the user who sent the friend request
                FirebaseDatabase.getInstance()
                    .reference.child("connections/${friendRequestsList[position].userId}/friend_request/sent/$currentUserId")
                    .removeValue()

                //Adding the connection to both users DB
                FirebaseDatabase.getInstance()
                    .reference.child("connections/$currentUserId/friends/${friendRequestsList[position].userId}")
                    .setValue(true)

                FirebaseDatabase.getInstance()
                    .reference.child("connections/${friendRequestsList[position].userId}/friends/$currentUserId")
                    .setValue(true)

                denyButton!!.visibility = View.GONE
                acceptButton!!.isEnabled = false

            }

            //If current user deny the friend request
            denyButton!!.setOnClickListener {

                //Remove request from current user DB
                FirebaseDatabase.getInstance()
                    .reference.child("connections/$currentUserId/friend_request/received/${friendRequestsList[position].userId}")
                    .removeValue()

                //Remove request from the user who sent the friend request
                FirebaseDatabase.getInstance()
                    .reference.child("connections/${friendRequestsList[position].userId}/friend_request/sent/$currentUserId")
                    .removeValue()

                acceptButton!!.visibility = View.GONE
                denyButton!!.isEnabled = false

            }
        }
    }

}