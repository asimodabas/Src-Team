package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {

            if (binding.loginEmailEditText.text.toString()
                    .equals("") || binding.loginPasswordEditText.text.toString().equals("")
            ) {
                Toast.makeText(
                    context,
                    "Lütfen Src-Team girişi için bilgilerinizi doğru giriniz.",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                auth.signInWithEmailAndPassword(
                    binding.loginEmailEditText.text.toString(),
                    binding.loginPasswordEditText.text.toString()
                ).addOnSuccessListener {

                    val action =
                     LoginFragmentDirections.actionLoginFragmentToSecondFragment()
                    findNavController().navigate(action)

                }.addOnFailureListener {

                    Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.createButton.setOnClickListener {

            val action =
               LoginFragmentDirections.actionLoginFragmentToCreateFragment()
            findNavController().navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}