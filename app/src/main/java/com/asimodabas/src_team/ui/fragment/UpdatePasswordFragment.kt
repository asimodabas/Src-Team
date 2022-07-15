package com.asimodabas.src_team.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentUpdatePasswordBinding
import com.asimodabas.src_team.toastMessage
import com.asimodabas.src_team.ui.activity.MainActivity
import com.asimodabas.src_team.viewmodel.UpdatePasswordViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class UpdatePasswordFragment : Fragment() {

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UpdatePasswordFragmentArgs>()
    private lateinit var viewModel: UpdatePasswordViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = args.email
        viewModel = ViewModelProvider(requireActivity())[UpdatePasswordViewModel::class.java]

        binding.buttonChangePassword.setOnClickListener {
            if (checkEmptyView()) {
                val newPassword = binding.editTextNewPassword.text.toString()
                val newPasswordAgain = binding.editTextNewPasswordAgain.text.toString()
                val oldPassword = binding.editTextOldPassword.text.toString()
                if (oldPassword == newPassword) {
                    requireContext().toastMessage(requireContext().getString(R.string.please_enter_different_password))
                } else {
                    if (newPassword == newPasswordAgain) {
                        val credential = EmailAuthProvider
                            .getCredential(email, oldPassword)
                        val user = Firebase.auth.currentUser!!
                        user.reauthenticate(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                viewModel.updatePassword(newPassword)

                                viewModel.updatePasswordData.observe(viewLifecycleOwner) { data ->
                                    data?.let {
                                        if (it) {
                                            requireContext().toastMessage(
                                                requireContext().getString(
                                                    R.string.password_changed
                                                )
                                            )
                                            viewModel.signOut()
                                            observeDataSignOut()
                                        }
                                    }
                                }
                                viewModel.updatePasswordError.observe(viewLifecycleOwner) { error ->
                                    error?.let {
                                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                it.localizedMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        requireContext().toastMessage(requireContext().getString(R.string.password_must_match))
                    }
                }
            } else {
                requireContext().toastMessage(requireContext().getString(R.string.fill_in_the_blanks))
            }
        }
    }

    private fun observeDataSignOut() {
        viewModel.isThereEntry.observe(viewLifecycleOwner) { login ->
            login?.let {
                if (it) {
                    activity?.let { activity ->
                        val intent = Intent(activity, MainActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    }
                }
            }
        }
    }

    private fun checkEmptyView(): Boolean = binding.editTextNewPassword.text.isNotEmpty() &&
            binding.editTextNewPasswordAgain.text.isNotEmpty() && binding.editTextOldPassword.text.isNotEmpty()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}