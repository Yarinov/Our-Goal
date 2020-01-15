package com.yarinov.ourgoal.connection

class FriendRequest(myId: String, otherUserId: String) :
    Connection(myId, otherUserId) {
    init {
        connectionType = CONNECTION_TYPE.FRIEND_REQUEST
    }
}