package com.asimodabas.src_team.ui.fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_advert.*
import kotlinx.android.synthetic.main.fragment_create.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class CreateFragment : Fragment() {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fcreateButton.setOnClickListener {

            if (
                nameEditText.text.toString() != "" &&
                surnameEditText.text.toString() != "" &&
                emailEditText.text.toString() != "" &&
                passwordEditText.text.toString() != ""
            ) {

                auth.createUserWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
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

            val name = nameEditText.text.toString()
            val surname = surnameEditText.text.toString()
            val email = emailEditText.text.toString()
            val gender = radioGroup.checkedRadioButtonId

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
                nameEditText.setText("")
                surnameEditText.setText("")
                emailEditText.setText("")
                passwordEditText.setText("")
                radioGroup.clearCheck()

                val action =CreateFragmentDirections.actionCreateFragmentToSecondFragment()
                findNavController().navigate(action)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

}