package com.yarinov.ourgoal.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yarinov.ourgoal.user.profile.ProfileActivity
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.Comparator

class SearchResultAdapter(
    private val context: Context,
    private var searchUsersResultList: List<User>
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_result_layout, parent, false)


        return ViewHolder(view)

    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.userNameLabel!!.text =
            "${searchUsersResultList[position].firstName} ${searchUsersResultList[position].lastName}"
        //holder.userNameLabel!!.isSelected = true


        holder.itemView.setOnClickListener {
            var moveToUserIntent = Intent(context, ProfileActivity::class.java)
            moveToUserIntent.putExtra("userId", searchUsersResultList[position].userId)
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
        return searchUsersResultList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getItem(position: Int): User {
        return searchUsersResultList[position]
    }


    fun sortByAsc() {
        val comparator: Comparator<User> =
            Comparator { object1: User, object2: User ->
                object1.firstName.compareTo(object2.firstName, true)
            }
        Collections.sort(searchUsersResultList, comparator)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val mView: View) : RecyclerView.ViewHolder(mView) {

        var userNameLabel: TextView? = null
        var searchUserProfilePic: CircleImageView? = null

        var resultSearchUserView: LinearLayout? = null

        init {

            userNameLabel = mView.findViewById(R.id.userNameLabel) as TextView
            searchUserProfilePic = mView.findViewById(R.id.searchUserProfilePic) as CircleImageView

            resultSearchUserView = mView.findViewById(R.id.resultSearchUserView) as LinearLayout
        }
    }

}