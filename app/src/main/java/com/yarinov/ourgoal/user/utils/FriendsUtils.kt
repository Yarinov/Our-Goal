package com.yarinov.ourgoal.user.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yarinov.ourgoal.user.User

class FriendsUtils(var currentUserId: String) {

    var friendsIdList: ArrayList<String>
    var usersList: ArrayList<User>

    init {
        this.friendsIdList = ArrayList()
        this.usersList = ArrayList()
    }

    private fun getFriendsList() {

        //Get all current user's friend
        //First step is to get all the user ids from the connection table
         friendsIdList = ArrayList()

        val currentUserConnectionDB =
            FirebaseDatabase.getInstance()
                .reference.child("connections/$currentUserId/friends")

        val getFriendsIdListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {//If user have friends -> Add each id to the list and pass the list to convert them to User object

                    for (friendId in p0.children)
                        friendsIdList.add(friendId.key.toString())

                    convertIdsToUsers(friendsIdList)
                }
            }

        }

        currentUserConnectionDB.addListenerForSingleValueEvent(getFriendsIdListener)

    }

    private fun convertIdsToUsers(usersIdList: ArrayList<String>) {

        //Reset the users list
        usersList.clear()

        //For each id in the list get the user data
        for (userId in usersIdList) {

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

                    usersList.add(userToAdd)

                }

            }

            userDatabase.addListenerForSingleValueEvent(getUserDataListener)
        }

    }
}