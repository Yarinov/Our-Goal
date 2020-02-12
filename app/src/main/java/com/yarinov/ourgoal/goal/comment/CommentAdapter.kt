package com.yarinov.ourgoal.goal.comment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.profile.ProfileActivity
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.Comparator


class CommentAdapter(
    private val context: Context,
    private var commentsList: ArrayList<Comment>,
    private val currentUserUid: String
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_main_layout, parent, false)


        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentComment = commentsList[position]

        holder.commentLabel!!.text = currentComment.commentText

        getCommentUserName(currentComment, holder)

        AdapterUtils().loadUserProfilePic(
            holder.profileImageInComment,
            currentComment.commentUserId
        )
        AdapterUtils().setFadeAnimation(holder.itemView, 950)

        holder.timeStampLabel!!.text =
            AdapterUtils().getTimeSincePosted(position, commentsList[position].datePosted)

        //Move to user profile on user's name click
        holder.userNameLabel!!.setOnClickListener {
            val moveToUserProfileIntent = Intent(context, ProfileActivity::class.java)
            moveToUserProfileIntent.putExtra("userId", currentComment.commentUserId)
            (context as Activity).startActivity(moveToUserProfileIntent)
        }

    }


    fun getCommentUserName(
        currentComment: Comment,
        holder: ViewHolder
    ) {
        val currentUserGoalDB =
            FirebaseDatabase.getInstance()
                .reference.child("users/${currentComment.commentUserId}")

        val getUserNameListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                val userFullName =
                    "${p0.child("firstName").value.toString()} ${p0.child("lastName").value.toString()}"
                holder.userNameLabel!!.text = userFullName
            }

        }

        currentUserGoalDB.addListenerForSingleValueEvent(getUserNameListener)
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): Comment {
        return commentsList[position]
    }


    fun sortByAsc() {
        val comparator: Comparator<Comment> =
            Comparator { object1: Comment, object2: Comment ->
                object1.datePosted.compareTo(object2.datePosted, true)
            }
        Collections.sort(commentsList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var userNameLabel: TextView? = null
        var commentLabel: TextView? = null
        var profileImageInComment: CircleImageView? = null

        var replyLabel: TextView? = null
        var timeStampLabel: TextView? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            commentLabel = mView.findViewById(R.id.commentLabel) as TextView
            profileImageInComment =
                mView.findViewById(R.id.profile_image) as CircleImageView

            replyLabel = mView.findViewById(R.id.replyLabel) as TextView
            timeStampLabel = mView.findViewById(R.id.timeStampLabel) as TextView

        }

    }

}