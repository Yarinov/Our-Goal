package com.yarinov.ourgoal.connection

import java.util.*

class Block(myId: String, otherUserId: String) :
    Connection(myId, otherUserId) {
    init {
        connectionType = CONNECTION_TYPE.BLOCK
    }
}