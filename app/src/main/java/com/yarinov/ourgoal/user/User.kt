package com.yarinov.ourgoal.user

class User(
    var userId: String,
    var userEmail: String,
    var firstName: String,
    var lastName: String
) {

    var userImageUri: String
    var userInfo: String

    init {
        this.userImageUri = ""
        this.userInfo = ""
    }

    constructor(
        userId: String,
        userEmail: String,
        firstName: String,
        lastName: String,
        userImageUri: String
    ) : this(userId, userEmail, firstName, lastName) {
        this.userImageUri = userImageUri
        this.userInfo = ""
    }

    constructor(
        userId: String,
        userImageUri: String,
        userEmail: String,
        firstName: String,
        lastName: String,
        userInfo: String
    ) : this(userId, userEmail, firstName, lastName) {
        this.userImageUri = userImageUri
        this.userInfo = userInfo
    }


}