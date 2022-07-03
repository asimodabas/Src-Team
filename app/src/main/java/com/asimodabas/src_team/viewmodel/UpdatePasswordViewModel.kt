package com.asimodabas.src_team.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UpdatePasswordViewModel : ViewModel() {

    private val auth = Firebase.auth

    var isThereEntry = MutableLiveData<Boolean>()
    val updatePasswordData = MutableLiveData<Boolean>()
    val updatePasswordError = MutableLiveData<String>()


    fun updatePassword(newPassword: String) {
        updatePasswordFirebase(newPassword)
    }

    fun signOut() {
        userSignOutControl()
    }

    private fun updatePasswordFirebase(newPassword: String) {
        val currentUser = auth.currentUser
        currentUser!!.updatePassword(newPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                updatePasswordData.value = true
            }
        }.addOnFailureListener { error ->
            updatePasswordError.value = error.localizedMessage
        }
    }

    private fun userSignOutControl() {
        val activeUser = auth.currentUser
        if (activeUser != null) {
            isThereEntry.value = true
            auth.signOut()
        } else {
            isThereEntry.value = false
        }
    }
}