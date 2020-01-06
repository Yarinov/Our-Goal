package com.yarinov.ourgoal.goal

import java.util.*
import kotlin.collections.HashMap


class Goal(
    var goalTitle: String,
    var goalDescription: String,
    var goalSteps: Long,
    var goalStatus: String
) {

    var goalId: String
    var goalProgress: Long
    var commentSectionId: String


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
        goalStatus: String
    ) : this(goalTitle, goalDescription, goalSteps, goalStatus) {
        this.goalId = goalId
        this.goalProgress = goalProgress
        this.commentSectionId = commentSectionId
    }



}