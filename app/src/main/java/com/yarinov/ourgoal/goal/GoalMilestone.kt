package com.yarinov.ourgoal.goal

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class GoalMilestone(var goalMilestoneTitle: String, var goalMilestoneOrder: Int) : Parcelable {

    var goalMilestoneId: String

    init {
        this.goalMilestoneId = UUID.randomUUID().toString()
    }

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt()
    ) {
        goalMilestoneId = parcel.readString().toString()
    }

    constructor(goalMilestoneId: String, goalMilestoneTitle: String, goalMilestoneOrder: Int) : this(
        goalMilestoneTitle,
        goalMilestoneOrder
    ) {
        this.goalMilestoneId = goalMilestoneId

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(goalMilestoneTitle)
        parcel.writeInt(goalMilestoneOrder)
        parcel.writeString(goalMilestoneId)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "GoalMilestone(goalMilestoneTitle='$goalMilestoneTitle', goalMilestoneOrder=$goalMilestoneOrder, goalMilestoneId='$goalMilestoneId')"
    }

    companion object CREATOR : Parcelable.Creator<GoalMilestone> {
        override fun createFromParcel(parcel: Parcel): GoalMilestone {
            return GoalMilestone(parcel)
        }

        override fun newArray(size: Int): Array<GoalMilestone?> {
            return arrayOfNulls(size)
        }
    }


}