package com.asimodabas.src_team.model

import com.google.firebase.Timestamp

data class SrcProfile(
    val name: String,
    val surname: String,
    val email: String,
    val userUid: String,
    var profileImage: String?,
    var profileImageName: String?,
    val registrationTime: Timestamp
)