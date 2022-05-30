package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.R
import com.asimodabas.src_team.model.SrcProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_create.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var profiles = arrayListOf<SrcProfile>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        db = Firebase.firestore
        auth = Firebase.auth
        pullUserInfo(auth.currentUser!!.uid)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logOut_item) {
            auth.signOut()
            val action =
                ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pullUserInfo(userUid: String) {
        val documentReference = db.collection("Records").document(userUid).get()
            .addOnSuccessListener { data ->
                if (data != null) {
                    val user = SrcProfile(
                        name = data["name"] as String,
                        surname = data["surname"] as String,
                        email = data["email"] as String
                    )
                    nameTextViewXD.setText(user.name)
                    surnameTextViewXD.setText(user.surname)
                    emailTextViewXD.setText(user.email)
                }
            }.addOnFailureListener { error ->
            }
    }


}