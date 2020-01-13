package com.yarinov.ourgoal.goal.milestone

import android.os.Parcel
import android.os.Parcelable

class MilestoneTitle(var milestoneTitle: String, var milestoneOrder: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(milestoneTitle)
        parcel.writeInt(milestoneOrder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MilestoneTitle> {
        override fun createFromParcel(parcel: Parcel): MilestoneTitle {
            return MilestoneTitle(parcel)
        }

        override fun newArray(size: Int): Array<MilestoneTitle?> {
            return arrayOfNulls(size)
        }
    }
}