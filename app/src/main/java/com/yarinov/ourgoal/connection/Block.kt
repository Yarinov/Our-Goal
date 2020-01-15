package com.yarinov.ourgoal.connection

class Block(myId: String, otherUserId: String) :
    Connection(myId, otherUserId) {
    init {
        connectionType = CONNECTION_TYPE.BLOCK
    }
}