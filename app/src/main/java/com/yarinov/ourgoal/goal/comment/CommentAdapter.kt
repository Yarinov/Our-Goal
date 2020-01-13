package com.yarinov.ourgoal.goal.comment

import android.annotation.SuppressLint
import android.content.Context
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
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

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
                object2.datePosted.compareTo(object1.datePosted, true)
            }
        Collections.sort(commentsList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var userNameLabel: TextView? = null
        var commentLabel: TextView? = null
        var profileImage: CircleImageView? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            commentLabel = mView.findViewById(R.id.commentLabel) as TextView
            profileImage =
                mView.findViewById(R.id.profile_image) as CircleImageView

        }

    }

}