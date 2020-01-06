package com.yarinov.ourgoal


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * A simple [Fragment] subclass.
 */
class ConnectionFragment : Fragment() {

    var friendRequestsSection: CardView? = null
    var friendRequestCount: Button? = null

    var usersRequestIdList: ArrayList<String>? = null

    var currentUser: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val connectionFragmentView =
            inflater.inflate(R.layout.fragment_connection, container, false)

        currentUser = FirebaseAuth.getInstance().currentUser

        usersRequestIdList = ArrayList()

        friendRequestsSection = connectionFragmentView.findViewById(R.id.friendRequestsSection)
        friendRequestCount = connectionFragmentView.findViewById(R.id.friendRequestCount)


        getFriendRequests()
        return connectionFragmentView
    }

    private fun getFriendRequests() {

        val currentUserConnectionDB =
            FirebaseDatabase.getInstance()
                .reference.child("connections/${currentUser!!.uid}/friend_requests")

        val getFriendRequestsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    friendRequestsSection?.visibility = View.VISIBLE

                    for (userRequest in p0.children) {
                        usersRequestIdList?.add(userRequest.key.toString())
                    }

                    friendRequestCount!!.text = usersRequestIdList!!.size.toString()

                }
            }

        }

        currentUserConnectionDB.addListenerForSingleValueEvent(getFriendRequestsListener)


    }


}
