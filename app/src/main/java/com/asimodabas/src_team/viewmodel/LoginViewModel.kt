package com.asimodabas.src_team.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel()  {

    private val auth = Firebase.auth

    val errorMessage = MutableLiveData<String>()
    val success = MutableLiveData<Boolean>()

    fun loginToApp(email: String, password: String) {
        loginUsingFirebase(email, password)
    }

    private fun loginUsingFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            success.value = true
        }.addOnFailureListener { error ->
            errorMessage.value = error.localizedMessage
        }

    }

}