package com.asimodabas.src_team.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentLoginBinding
import com.asimodabas.src_team.toastMessage
import com.asimodabas.src_team.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val action = LoginFragmentDirections.actionLoginFragmentToSecondFragment()
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

        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            if (dataControl()) {
                val email = binding.loginEmailEditText.text.toString()
                val password = binding.loginPasswordEditText.text.toString()

                viewModel.loginToApp(email, password)
                observeData()
            } else {
                requireContext().toastMessage(requireContext().getString(R.string.fill_in_the_blanks))
            }
        }

        binding.createButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_createFragment)
        }
    }

    private fun observeData() {
        viewModel.success.observe(viewLifecycleOwner) { success ->
            success?.let { value ->
                if (value) {
                    activity?.let {
                        findNavController().navigate(R.id.action_loginFragment_to_secondFragment)

                    }
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dataControl(): Boolean = binding.loginEmailEditText.text.isNotEmpty()
            && binding.loginPasswordEditText.text.isNotEmpty()

    override fun onDestroyView() {
        super.onDestroyView()
    }
}