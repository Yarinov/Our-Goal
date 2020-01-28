package com.yarinov.ourgoal.goal.comment

import java.util.*
import kotlin.collections.ArrayList

class CommentSection(
    var goalId: String
) {

    var commentSectionId: String
    var CommentsList: List<Comment>

    init {

        this.commentSectionId = UUID.randomUUID().toString()
        this.CommentsList = ArrayList()
    }

    constructor(
        commentSectionId: String,
        goalId: String,
        CommentsList: List<Comment>
    ) : this(goalId) {
        this.commentSectionId = commentSectionId
        this.CommentsList = CommentsList
    }


}