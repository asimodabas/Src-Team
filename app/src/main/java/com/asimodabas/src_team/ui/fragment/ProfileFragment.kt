package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.asimodabas.Constants
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentProfileBinding
import com.asimodabas.src_team.model.SrcProfile
import com.asimodabas.src_team.toastMessage
import com.asimodabas.src_team.viewmodel.ProfileViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userProfileInfo: SrcProfile? = null
    private lateinit var viewModel: ProfileViewModel


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

        viewModel = ViewModelProvider(requireActivity())[ProfileViewModel::class.java]

        setHasOptionsMenu(true)

        db = Firebase.firestore
        auth = Firebase.auth

        viewModel.getProfileInfo()
        getProfileInfo()

        binding.EditProfileTextView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editFragment)
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

    private fun getProfileInfo() {

        viewModel.dataConfirmation.observe(viewLifecycleOwner) { dataConfirm ->
            dataConfirm?.let { data ->
                if (data) {
                    userProfileInfo = viewModel.userInfo
                    binding.emailTextViewXD.setText(userProfileInfo!!.email)
                    val name = userProfileInfo!!.name
                    val surname = userProfileInfo!!.surname

                    val sdf = SimpleDateFormat("dd/M/yyyy")
                    val currentDate = sdf.format(Date())

                    binding.dateTextViewXD.setText(currentDate)
                    binding.nameTextViewXD.text = name
                    binding.surnameTextViewXD.text = surname
                    if (userProfileInfo!!.profileImage != null) {
                        Picasso.get()
                            .load(userProfileInfo!!.profileImage)
                            .placeholder(R.drawable.src_logo)
                            .into(binding.imageView3)
                    } else {
                        binding.imageView3.setImageDrawable(
                            ActivityCompat.getDrawable(
                                requireContext(),
                                R.drawable.src_logo
                            )
                        )
                    }
                } else {
                    requireContext().toastMessage("Hata")
                }
            }
        }
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}