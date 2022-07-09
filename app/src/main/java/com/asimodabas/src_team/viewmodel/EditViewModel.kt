package com.asimodabas.src_team.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.asimodabas.Constants
import com.asimodabas.src_team.model.SrcProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class EditViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val storage = Firebase.storage

    lateinit var userInfo: SrcProfile
    val userUid = auth.currentUser?.uid
    val dataConfirmation = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val changesSaved = MutableLiveData<Boolean>()
    val deleteAccountAnimation = MutableLiveData<Boolean>()
    val deleteAccountError = MutableLiveData<Boolean>()
    val deleteAccountConfirmation = MutableLiveData<Boolean>()


    fun getProfileInfo() {
        pullUserInfo()
    }

    fun updateProfile(
        userInfo: SrcProfile, name: String, surname: String, selectedImage: Uri?,
    ) {
        updateProfileFirebase(userInfo, name, surname, selectedImage)
    }

    fun deleteAccount() {
        deleteEverything()
    }

    private fun pullUserInfo() {
        val documentReference = db.collection(Constants.USERS).document(userUid!!)
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

    private fun updateProfileFirebase(
        userInfo: SrcProfile,
        newName: String,
        newSurname: String,
        newSelectedImage: Uri?,
    ) {
        val reference = storage.reference
        val documentId = userUid
        if (newSelectedImage != null) {
            var imageReferenceLink: String?
            if (userInfo.profileImageName == null) {
                val uuid = UUID.randomUUID()
                val profileImageName = "${uuid}.jpeg"
                userInfo.profileImageName = profileImageName
            }
            val imageReference =
                reference.child(Constants.IMAGES).child(userInfo.profileImageName!!)
            imageReference.putFile(newSelectedImage).addOnSuccessListener {
                val uploadedImageReference =
                    reference.child(Constants.IMAGES).child(userInfo.profileImageName!!)
                uploadedImageReference.downloadUrl.addOnSuccessListener { uri ->
                    imageReferenceLink = uri.toString()
                    if (imageReferenceLink != null) {
                        val newUser = SrcProfile(
                            newName, newSurname, userInfo.email,
                            userUid!!, imageReferenceLink,
                            userInfo.profileImageName, userInfo.registrationTime
                        )
                        db.collection(Constants.USERS)
                            .document(documentId!!).set(newUser)
                            .addOnCompleteListener { registration ->
                                if (registration.isSuccessful) {
                                    dataConfirmation.value = true
                                    changesSaved.value = true
                                }
                            }.addOnFailureListener { error ->
                                errorMessage.value = error.localizedMessage
                            }
                    }
                }
            }.addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage
            }
        } else {
            val newUser = SrcProfile(
                newName, newSurname, userInfo.email,
                userUid!!, userInfo.profileImage,
                userInfo.profileImageName, userInfo.registrationTime
            )
            db.collection(Constants.USERS)
                .document(documentId!!).set(newUser).addOnCompleteListener { registration ->
                    if (registration.isSuccessful) {
                        dataConfirmation.value = true
                        changesSaved.value = true
                    }
                }.addOnFailureListener { error ->
                    errorMessage.value = error.localizedMessage
                }
        }
    }

    private fun deleteEverything() {
        deleteAccountAnimation.value = true
        val storageRef = storage.reference
        if (userUid != null) {
            val documentReference = db.collection(Constants.USERS).document(userUid)
            documentReference.get().addOnSuccessListener { data ->
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
                    if (user.profileImageName != null) {
                        storageRef.child(Constants.IMAGES).child(user.profileImageName!!)
                            .delete().addOnSuccessListener {
                                db.collection(Constants.USERS).document(userUid).delete()
                                    .addOnSuccessListener {
                                        val currentUser = Firebase.auth.currentUser!!
                                        Firebase.auth.signOut()
                                        currentUser.delete().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                deleteAccountAnimation.value = false
                                                deleteAccountConfirmation.value = true
                                            }
                                        }.addOnFailureListener {
                                            deleteAccountAnimation.value = false
                                            errorMessage.value = it.localizedMessage
                                        }
                                    }.addOnFailureListener {
                                        deleteAccountAnimation.value = false
                                        errorMessage.value = it.localizedMessage
                                    }
                            }.addOnFailureListener {
                                deleteAccountAnimation.value = false
                                errorMessage.value = it.localizedMessage
                            }
                    } else {
                        db.collection(Constants.USERS).document(userUid).delete()
                            .addOnSuccessListener {
                                auth.currentUser!!.delete().addOnSuccessListener {
                                    deleteAccountAnimation.value = false
                                    deleteAccountConfirmation.value = true
                                }.addOnFailureListener {
                                    deleteAccountAnimation.value = false
                                    errorMessage.value = it.localizedMessage
                                }
                            }.addOnFailureListener {
                                deleteAccountAnimation.value = false
                                errorMessage.value = it.localizedMessage
                            }
                    }
                }
            }.addOnFailureListener { error ->
                errorMessage.value = error.localizedMessage
            }
        } else {
            deleteAccountAnimation.value = false
            deleteAccountError.value = true
        }
    }
}