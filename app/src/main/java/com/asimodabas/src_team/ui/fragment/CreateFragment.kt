package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.databinding.FragmentCreateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class CreateFragment : Fragment() {

    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fcreateButton.setOnClickListener {

            if (
                binding.nameEditText.text.toString() != "" &&
                binding.surnameEditText.text.toString() != "" &&
                binding.emailEditText.text.toString() != "" &&
                binding.passwordEditText.text.toString() != ""
            ) {

                auth.createUserWithEmailAndPassword(
                    binding.emailEditText.text.toString(),
                    binding.passwordEditText.text.toString()
                ).addOnSuccessListener {

                    firebaseSaver()

                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Lütfen boşlukları eksiksiz doldurun.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }



    fun firebaseSaver() {

        val user = auth.currentUser
        user?.let {

            val name = binding.nameEditText.text.toString()
            val surname = binding.surnameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val gender = binding.radioGroup.checkedRadioButtonId

            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())

            val dataMap = HashMap<String, Any>()
            dataMap.put("id", auth.currentUser?.uid.toString())
            dataMap.put("name", name)
            dataMap.put("surname", surname)
            dataMap.put("email", email)
            dataMap.put("gender", gender)
            dataMap.put("date", currentDate)

            firestore.collection("Records").document(auth.currentUser?.uid!!).set(dataMap).addOnSuccessListener {
                binding.nameEditText.setText("")
                binding.surnameEditText.setText("")
                binding.emailEditText.setText("")
                binding.passwordEditText.setText("")
                binding.radioGroup.clearCheck()

                val action =CreateFragmentDirections.actionCreateFragmentToSecondFragment()
                findNavController().navigate(action)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

}