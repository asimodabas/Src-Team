package com.asimodabas.src_team.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asimodabas.Constants.IMAGES
import com.asimodabas.Constants.USERS
import com.asimodabas.src_team.model.SrcProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class CreateViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    val errorMessage = MutableLiveData<String?>()
    val dataConfirmation = MutableLiveData<Boolean>()

    fun registerToApp(
        email: String,
        password: String,
        name: String,
        surname: String,
        selectedImage: Uri?,
    ) {
        registerToAppUsingFirebase(email, password, name, surname, selectedImage)
    }

    private fun registerToAppUsingFirebase(
        email: String,
        password: String,
        name: String,
        surname: String,
        selectedImage: Uri?,
    ) {
        errorMessage.value = null
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { authState ->
            if (authState.isSuccessful) {
                val registrationTime = Timestamp.now()
                val activeUserUid = auth.currentUser?.uid
                if (selectedImage != null) {
                    var imageReferenceLink: String?
                    val profileImageName: String?
                    val reference = storage.reference
                    val uuid = UUID.randomUUID()
                    profileImageName = "${uuid}.jpeg"
                    val imageReference = reference.child(IMAGES).child(profileImageName)
                    imageReference.putFile(selectedImage).addOnSuccessListener {
                        val uploadedImageReference =
                            reference.child(IMAGES).child(profileImageName)
                        uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                            imageReferenceLink = uri.toString()
                            if (imageReferenceLink != null) {
                                val user = SrcProfile(
                                    name,
                                    surname,
                                    email,
                                    activeUserUid!!,
                                    imageReferenceLink,
                                    profileImageName,
                                    registrationTime
                                )
                                db.collection(USERS)
                                    .document(activeUserUid).set(user)
                                    .addOnCompleteListener { success ->
                                        if (success.isSuccessful) {
                                            dataConfirmation.value = true
                                        }
                                    }.addOnFailureListener { exception ->
                                        errorMessage.value = exception.localizedMessage
                                    }
                            }
                        }
                    }.addOnFailureListener { exception ->
                        errorMessage.value = exception.localizedMessage
                    }
                } else {
                    val user = SrcProfile(
                        name,
                        surname,
                        email,
                        activeUserUid!!,
                        null,
                        null,
                        registrationTime

                    )
                    db.collection(USERS)
                        .document(activeUserUid).set(user).addOnCompleteListener { success ->
                            if (success.isSuccessful) {
                                dataConfirmation.value = true
                            }
                        }.addOnFailureListener { exception ->
                            errorMessage.value = exception.localizedMessage
                        }
                }
            }
        }.addOnFailureListener { authError ->
            errorMessage.value = authError.localizedMessage
        }
    }


}