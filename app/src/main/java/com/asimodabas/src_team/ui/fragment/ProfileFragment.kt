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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var profiles = arrayListOf<SrcProfile>()

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
            val action =ProfileFragmentDirections.actionProfileFragmentToEditFragment()
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
        val documentReference = db.collection("Records").document(userUid).get()
            .addOnSuccessListener { data ->
                if (data != null) {
                    val user = SrcProfile(
                        name = data["name"] as String,
                        surname = data["surname"] as String,
                        email = data["email"] as String,
                        date = data["date"] as String
                    )

                    binding.nameTextViewXD.setText(user.name)
                    binding.surnameTextViewXD.setText(user.surname)
                    binding.emailTextViewXD.setText(user.email)
                    binding.dateTextViewXD.setText(user.date)
                }
            }.addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
            }
    }
}