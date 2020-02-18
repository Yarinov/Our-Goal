package com.yarinov.ourgoal.user.mentions_auto_complete

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.otaliastudios.autocomplete.RecyclerViewPresenter
import com.squareup.picasso.Picasso
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.user.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class UserPresenter(var usersList: ArrayList<User>, context: Context?) :
    RecyclerViewPresenter<User?>(context) {
    protected var adapter: Adapter? =
        null

    override fun getPopupDimensions(): PopupDimensions {
        val dims = PopupDimensions()
        dims.width = 600
        dims.height = ViewGroup.LayoutParams.WRAP_CONTENT
        return dims
    }

    override fun instantiateAdapter(): RecyclerView.Adapter<*>? {
        adapter = Adapter()
        return adapter
    }

    override fun onQuery(query: CharSequence?) {
        var query = query

        if (TextUtils.isEmpty(query)) {
            adapter!!.setData(usersList)
        } else {
            query = query.toString().toLowerCase()
            val list: MutableList<User> =
                ArrayList()
            for (u in usersList) {
                if (u.getUserFullName().toLowerCase().contains(query)
                ) {
                    list.add(u)
                }
            }
            adapter!!.setData(list)
        }
        adapter!!.notifyDataSetChanged()
    }

    inner class Adapter :
        RecyclerView.Adapter<Adapter.Holder>() {
        private var data: List<User>? = null

        inner class Holder(val root: View) :
            RecyclerView.ViewHolder(root) {
            val fullname: TextView
            val userProfilePic: CircleImageView

            init {
                fullname = root.findViewById<View>(R.id.fullname) as TextView
                userProfilePic = root.findViewById<View>(R.id.userProfilePic) as CircleImageView
            }
        }

        fun setData(data: List<User>?) {
            this.data = data
        }

        override fun getItemCount(): Int {
            return if (isEmpty) 1 else data!!.size
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): Holder {
            return Holder(
                LayoutInflater.from(context).inflate(R.layout.user_in_mention_layout, parent, false)
            )
        }

        private val isEmpty: Boolean
            private get() = data == null || data!!.isEmpty()

        override fun onBindViewHolder(
            holder: Holder,
            position: Int
        ) {
            if (isEmpty) {
                holder.fullname.text = "No user here!"
                holder.root.setOnClickListener(null)
                return
            }
            val user = data!![position]
            val userFullName = user.getUserFullName()
            holder.fullname.text = userFullName

            //Load user image
            val storage = FirebaseStorage.getInstance()

            val gsReference =
                storage.getReferenceFromUrl("gs://ourgoal-ebee9.appspot.com/users/profile_pic/${user.userId}")

            gsReference.downloadUrl
                .addOnSuccessListener {

                    Picasso.get().load(it).placeholder(R.drawable.default_user_ic).noFade()
                        .into(holder.userProfilePic)

                }
                .addOnFailureListener { exception ->
                    val errorCode = (exception as StorageException).errorCode
                    if (errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                        //Not Found
                    }
                }

            holder.root.setOnClickListener { dispatchClick(user) }
        }
    }
}