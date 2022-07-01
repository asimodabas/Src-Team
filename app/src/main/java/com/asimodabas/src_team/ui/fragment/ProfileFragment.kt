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
import com.asimodabas.src_team.databinding.FragmentProfileBinding
import com.asimodabas.src_team.model.SrcProfile
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        db = Firebase.firestore
        auth = Firebase.auth

        pullUserInfo(auth.currentUser!!.uid)

        binding.EditProfileTextView.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToEditFragment()
            findNavController().navigate(action)
        }
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
        val documentReference = db.collection("Users").document(userUid).get()
            .addOnSuccessListener { data ->
                if (data != null) {

                    val user = SrcProfile(
                        name = data["name"] as String,
                        surname = data["surname"] as String,
                        email = data["email"] as String,
                        userUid = data["userUid"] as String,
                        profileImage = data["profileImage"] as String,
                        profileImageName = data["profileImageName"] as String,
                        registrationTime = data["registrationTime"] as Timestamp,

                        )
                    val sdf = SimpleDateFormat("dd/M/yyyy")
                    val currentDate = sdf.format(Date())

                    binding.nameTextViewXD.setText(user.name)
                    binding.surnameTextViewXD.setText(user.surname)
                    binding.emailTextViewXD.setText(user.email)
                    binding.dateTextViewXD.setText(currentDate)
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }
    }

}