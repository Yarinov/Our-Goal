package com.yarinov.ourgoal.user

class User(
    var userId: String
) {

    var userImageUri: String
    var userInfo: String
    var userEmail: String
    var firstName: String
    var lastName: String

    init {
        this.userEmail = ""
        this.firstName = ""
        this.lastName = ""
        this.userImageUri = ""
        this.userInfo = ""
    }

    constructor(
        userId: String,
        userEmail: String,
        firstName: String,
        lastName: String
    ) : this(userId) {
        this.userEmail = userEmail
        this.firstName = firstName
        this.lastName = lastName
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


    fun getUserFullName(): String {
        return "$firstName $lastName"
    }

}