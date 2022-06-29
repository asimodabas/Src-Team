package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentThirdBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchImageView.setOnClickListener {
            val action =ThirdFragmentDirections.actionThirdFragmentToAdvertFragment()
            findNavController().navigate(action)
        }

        binding.findImageView.setOnClickListener {
            val action =ThirdFragmentDirections.actionThirdFragmentToSearchFragment()
            findNavController().navigate(action)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            val action =ThirdFragmentDirections.actionThirdFragmentToProfileFragment()
            findNavController().navigate(action)
        } else if (item.itemId == R.id.logOut_item) {

            auth.signOut()
            val action =ThirdFragmentDirections.actionThirdFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }
}