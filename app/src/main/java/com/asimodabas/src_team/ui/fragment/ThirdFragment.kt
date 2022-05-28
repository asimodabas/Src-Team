package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_third.*

class ThirdFragment : Fragment() {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchImageView.setOnClickListener {
            val action =ThirdFragmentDirections.actionThirdFragmentToAdvertFragment()
            findNavController().navigate(action)
        }

        findImageView.setOnClickListener {
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