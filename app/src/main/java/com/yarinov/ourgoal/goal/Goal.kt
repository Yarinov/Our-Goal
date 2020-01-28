package com.yarinov.ourgoal.goal

import android.os.Parcel
import android.os.Parcelable
import java.util.*


class Goal(
    var goalTitle: String,
    var goalDescription: String,
    var goalSteps: Long,
    var goalStatus: String,
    var datePosted: String,
    var userId: String
) : Parcelable {

    var goalId: String
    var goalProgress: Long
    var commentSectionId: String

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
        goalId = parcel.readString().toString()
        goalProgress = parcel.readLong()
        commentSectionId = parcel.readString().toString()
    }


    init {
        this.goalId = UUID.randomUUID().toString()
        this.goalProgress = 0
        this.commentSectionId = UUID.randomUUID().toString()

    }

    constructor(
        goalId: String,
        goalTitle: String,
        goalDescription: String,
        goalProgress: Long,
        goalSteps: Long,
        commentSectionId: String,
        goalStatus: String,
        datePosted: String,
        userId: String
    ) : this(goalTitle, goalDescription, goalSteps, goalStatus, datePosted, userId) {
        this.goalId = goalId
        this.goalProgress = goalProgress
        this.commentSectionId = commentSectionId
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(goalTitle)
        parcel.writeString(goalDescription)
        parcel.writeLong(goalSteps)
        parcel.writeString(goalStatus)
        parcel.writeString(datePosted)
        parcel.writeString(userId)
        parcel.writeString(goalId)
        parcel.writeLong(goalProgress)
        parcel.writeString(commentSectionId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Goal(goalTitle='$goalTitle', goalDescription='$goalDescription', goalSteps=$goalSteps, goalStatus='$goalStatus', datePosted='$datePosted', userId='$userId', goalId='$goalId', goalProgress=$goalProgress, commentSectionId='$commentSectionId')"
    }

    companion object CREATOR : Parcelable.Creator<Goal> {
        override fun createFromParcel(parcel: Parcel): Goal {
            return Goal(parcel)
        }

        override fun newArray(size: Int): Array<Goal?> {
            return arrayOfNulls(size)
        }
    }


}