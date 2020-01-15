package com.yarinov.ourgoal.connection

class Friend(myId: String, otherUserId: String) :
    Connection(myId, otherUserId) {
    init {
        connectionType = CONNECTION_TYPE.FRIEND
    }
}