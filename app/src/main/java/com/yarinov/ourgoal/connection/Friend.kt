package com.yarinov.ourgoal.connection

import java.util.*

class Friend(myId: String, otherUserId: String) :
    Connection(myId, otherUserId) {
    init {
        connectionType = CONNECTION_TYPE.FRIEND
    }
}