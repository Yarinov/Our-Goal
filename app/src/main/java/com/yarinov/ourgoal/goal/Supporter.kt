package com.yarinov.ourgoal.goal

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Supporter(var supporterUserId: String, var supportGoalId: String) : Parcelable {

    var supportId: String

    init {
        this.supportId = UUID.randomUUID().toString()
    }

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
        supportId = parcel.readString().toString()
    }

    constructor(supportId: String, supporterUserId: String, supportGoalId: String) : this(supporterUserId, supportGoalId) {
        this.supportId = supportId

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(supporterUserId)
        parcel.writeString(supportGoalId)
        parcel.writeString(supportId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Supporter> {
        override fun createFromParcel(parcel: Parcel): Supporter {
            return Supporter(parcel)
        }

        override fun newArray(size: Int): Array<Supporter?> {
            return arrayOfNulls(size)
        }
    }


}