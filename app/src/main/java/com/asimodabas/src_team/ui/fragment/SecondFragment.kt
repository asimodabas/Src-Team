package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asimodabas.Constants
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentSecondBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.button2.setOnClickListener {
            Constants.selectedCountry = "Elazig"
            findNavController().navigate(R.id.action_secondFragment_to_thirdFragment)
        }
        binding.button3.setOnClickListener {
            Constants.selectedCountry = "Istanbul"
            findNavController().navigate(R.id.action_secondFragment_to_thirdFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            findNavController().navigate(R.id.action_secondFragment_to_profileFragment)
        } else if (item.itemId == R.id.logOut_item) {
            auth.signOut()
            findNavController().navigate(R.id.action_secondFragment_to_loginFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}