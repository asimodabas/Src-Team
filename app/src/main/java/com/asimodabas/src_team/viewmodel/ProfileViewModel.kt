package com.asimodabas.src_team.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asimodabas.Constants.USERS
import com.asimodabas.src_team.model.SrcProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    val dataConfirmation = MutableLiveData<Boolean>()
    var isThereEntry = MutableLiveData<Boolean>()
    lateinit var userInfo: SrcProfile
    val userUid = auth.currentUser?.uid
    val updatePasswordData = MutableLiveData<Boolean>()
    val updatePasswordError = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()
    val changesSaved = MutableLiveData<Boolean>()

    fun getProfileInfo() {
        pullUserInfo()
    }

    private fun pullUserInfo() {
        val documentReference = db.collection(USERS).document(userUid!!)
        documentReference.get()
            .addOnSuccessListener { data ->
                if (data != null) {
                    val user = SrcProfile(
                        data["name"] as String,
                        data["surname"] as String,
                        data["email"] as String,
                        data["userUid"] as String,
                        data["profileImage"] as String?,
                        data["profileImageName"] as String?,
                        data["registrationTime"] as Timestamp
                    )
                    userInfo = user
                    dataConfirmation.value = true
                }
            }.addOnFailureListener { error ->
                errorMessage.value = error.localizedMessage
            }
    }


}