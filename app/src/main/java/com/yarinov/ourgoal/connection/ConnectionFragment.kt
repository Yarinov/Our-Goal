package com.yarinov.ourgoal.connection


import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.R
import com.yarinov.ourgoal.connection.friend_request.FriendRequestAdapter
import com.yarinov.ourgoal.user.User

/**
 * A simple [Fragment] subclass.
 */
class ConnectionFragment : Fragment() {

    var friendRequestsSection: CardView? = null
    var friendRequestCount: Button? = null

    var usersRequestList: ArrayList<User>? = null

    var friendRequestPopupWindow: PopupWindow? = null

    var adapter: FriendRequestAdapter? = null

    var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val connectionFragmentView =
            inflater.inflate(R.layout.fragment_connection, container, false)

        currentUser = FirebaseAuth.getInstance().currentUser

        usersRequestList = ArrayList()

        friendRequestsSection = connectionFragmentView.findViewById(R.id.friendRequestsSection)
        friendRequestCount = connectionFragmentView.findViewById(R.id.friendRequestCount)


        getFriendRequests()

        friendRequestsSection!!.setOnClickListener {
            if (usersRequestList!!.isNotEmpty())
                openFriendRequestsPopupWindow()
        }

        adapter = context?.let { FriendRequestAdapter(it, usersRequestList!!, currentUser!!.uid) }

        return connectionFragmentView
    }

    private fun openFriendRequestsPopupWindow() {

        val popupView = layoutInflater.inflate(R.layout.friend_requests_popup_layout, null)
        popupView

        val friendRequestRecyclerView =
            popupView.findViewById<RecyclerView>(R.id.friendRequestRecyclerView)

        friendRequestPopupWindow = PopupWindow(activity)
        friendRequestPopupWindow!!.contentView = popupView
        friendRequestPopupWindow!!.width = LinearLayout.LayoutParams.MATCH_PARENT
        friendRequestPopupWindow!!.height = LinearLayout.LayoutParams.WRAP_CONTENT
        friendRequestPopupWindow!!.isFocusable = true
        friendRequestPopupWindow!!.setBackgroundDrawable(ColorDrawable())
        friendRequestPopupWindow!!.showAtLocation(popupView, Gravity.TOP, 0, 0)
        //friendRequestPopupWindow!!.showAsDropDown(popupView)

        friendRequestRecyclerView.adapter = adapter
        friendRequestRecyclerView.layoutManager = LinearLayoutManager(context)
        friendRequestRecyclerView.hasFixedSize()

        friendRequestPopupWindow!!.setOnDismissListener {
            getFriendRequests()
        }

    }

    private fun getFriendRequests() {

        val currentUserConnectionDB =
            FirebaseDatabase.getInstance()
                .reference.child("connections/${currentUser!!.uid}/friend_request/received")

        val getFriendRequestsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    friendRequestsSection?.visibility = View.VISIBLE

                    usersRequestList?.clear()


                    for (userRequest in p0.children) {

                        val userRequestId = userRequest.key.toString()

                        val userRequestIdDB =
                            FirebaseDatabase.getInstance()
                                .reference.child("users/$userRequestId")

                        val getUserRequestIdListener = object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {}

                            override fun onDataChange(p0: DataSnapshot) {

                                usersRequestList?.add(
                                    User(
                                        p0.child("userId").value.toString(),
                                        p0.child("userEmail").value.toString(),
                                        p0.child("firstName").value.toString(),
                                        p0.child("lastName").value.toString(),
                                        p0.child("userImageUri").value.toString()
                                    )
                                )

                                friendRequestCount!!.text = usersRequestList!!.size.toString()
                            }


                        }

                        userRequestIdDB.addListenerForSingleValueEvent(getUserRequestIdListener)
                    }

                    adapter!!.notifyDataSetChanged()


                }else{//If there is no friend requests
                    friendRequestCount!!.text = "0"
                    usersRequestList?.clear()
                }
            }

        }

        currentUserConnectionDB.addListenerForSingleValueEvent(getFriendRequestsListener)


    }


}
