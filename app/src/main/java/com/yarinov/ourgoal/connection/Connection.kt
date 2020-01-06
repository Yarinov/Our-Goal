package com.yarinov.ourgoal.connection

import android.os.Parcel
import android.os.Parcelable
import java.util.*

open class Connection(var myId: String, var otherUserId: String) : Parcelable {
    var connectionId: String = UUID.randomUUID().toString()
    var connectionType //1 - Friend //10 - Friend Request //100 - BLocked
            : CONNECTION_TYPE? = null

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
        connectionId = parcel.readString().toString()
    }

    override fun toString(): String {
        return "Connection{" +
                "connectionId=" + connectionId +
                ", myId=" + myId +
                ", otherUserId=" + otherUserId +
                ", connectionType=" + connectionType +
                '}'
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(myId)
        parcel.writeString(otherUserId)
        parcel.writeString(connectionId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Connection> {
        override fun createFromParcel(parcel: Parcel): Connection {
            return Connection(parcel)
        }

        override fun newArray(size: Int): Array<Connection?> {
            return arrayOfNulls(size)
        }
    }

}