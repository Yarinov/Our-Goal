package com.yarinov.ourgoal

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.squareup.picasso.Picasso
import com.yarinov.ourgoal.user.User
import com.yarinov.ourgoal.user.profile.ProfileActivity
import com.yarinov.ourgoal.utils.adapter_utils.AdapterUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.Comparator

class SimpleUserAdapter(
    private val context: Context,
    private var simpleUsersList: List<User>
) : RecyclerView.Adapter<SimpleUserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_user_layout, parent, false)


        return ViewHolder(view)

    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (position == 0)
            holder.divider!!.visibility = View.GONE

        holder.userNameLabel!!.text =
            "${simpleUsersList[position].firstName} ${simpleUsersList[position].lastName}"
        //holder.userNameLabel!!.isSelected = true


        holder.itemView.setOnClickListener {
            var moveToUserIntent = Intent(context, ProfileActivity::class.java)
            moveToUserIntent.putExtra("userId", simpleUsersList[position].userId)
            context.startActivity(moveToUserIntent)
        }

        AdapterUtils().loadUserProfilePic(holder.userProfilePic, simpleUsersList[position].userId)

    }

    override fun getItemCount(): Int {
        return simpleUsersList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): User {
        return simpleUsersList[position]
    }


    fun sortByAsc() {
        val comparator: Comparator<User> =
            Comparator { object1: User, object2: User ->
                object1.firstName.compareTo(object2.firstName, true)
            }
        Collections.sort(simpleUsersList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var userNameLabel: TextView? = null
        var userProfilePic: CircleImageView? = null

        var resultSearchUserView: LinearLayout? = null

        var divider: View? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            userProfilePic = mView.findViewById(R.id.searchUserProfilePic) as CircleImageView

            resultSearchUserView = mView.findViewById(R.id.resultSearchUserView) as LinearLayout

            divider = mView.findViewById(R.id.divider) as View
        }
    }

}