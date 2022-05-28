package com.asimodabas.src_team.ui.fragment

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
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        //Remember username password

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val action =LoginFragmentDirections.actionLoginFragmentToSecondFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {

            if (loginEmailEditText.text.toString()
                    .equals("") || loginPasswordEditText.text.toString().equals("")
            ) {
                Toast.makeText(
                    context,
                    "Lütfen Src-Team girişi için bilgilerinizi doğru giriniz.",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                auth.signInWithEmailAndPassword(
                    loginEmailEditText.text.toString(),
                    loginPasswordEditText.text.toString()
                ).addOnSuccessListener {

                    val action =
                     LoginFragmentDirections.actionLoginFragmentToSecondFragment()
                    findNavController().navigate(action)

                }.addOnFailureListener {

                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        createButton.setOnClickListener {

            val action =
               LoginFragmentDirections.actionLoginFragmentToCreateFragment()
            findNavController().navigate(action)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}