package com.yarinov.ourgoal.goal.comment

import java.util.*

class Comment(
    var commentSectionId: String,
    var commentUserId: String,
    var commentText: String,
    var datePosted: String
) {


    var commentId: String

    init {
        this.commentId = UUID.randomUUID().toString()
    }

    constructor(
        commentId: String,
        commentSectionId: String,
        commentUserId: String,
        commentText: String,
        datePosted: String
    ) : this(commentSectionId, commentUserId, commentText, datePosted) {
        this.commentId = commentId

    }


}