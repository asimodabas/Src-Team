package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asimodabas.src_team.R
import com.asimodabas.src_team.databinding.FragmentAdvertBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class AdvertFragment : Fragment() {

    private var _binding: FragmentAdvertBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvertBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.advSearchButton.setOnClickListener {

            if (
                binding.editTextTextPersonName2.text.toString() != "" &&
                binding.editTextTextPersonName4.text.toString() != "" &&
                binding.editTextTextPersonName3.text.toString() != "" &&
                binding.editTextTextPersonName5.text.toString() != "" &&
                binding.editTextTextPersonName6.text.toString() != ""
            ) {
                auth.currentUser?.let {

                    val address = binding.editTextTextPersonName2.text.toString()
                    val clock = binding.editTextTextPersonName4.text.toString()
                    val Activtiydate = binding.editTextTextPersonName3.text.toString()
                    val SearchActivity = binding.editTextTextPersonName5.text.toString()
                    val Notes = binding.editTextTextPersonName6.text.toString()

                    val dataMap = HashMap<String, Any>()

                    dataMap.put("Adres", address)
                    dataMap.put("Clock", clock)
                    dataMap.put("Activtiydate", Activtiydate)
                    dataMap.put("SearchActivity", SearchActivity)
                    dataMap.put("Notes", Notes)

                    firestore.collection("Search").add(dataMap).addOnSuccessListener {

                        binding.editTextTextPersonName2.setText("")
                        binding.editTextTextPersonName4.setText("")
                        binding.editTextTextPersonName3.setText("")
                        binding.editTextTextPersonName5.setText("")
                        binding.editTextTextPersonName6.setText("")

                        val action =
                          AdvertFragmentDirections.actionAdvertFragmentToSearchFragment()
                        findNavController().navigate(action)

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }

            } else {
                Toast.makeText(
                    requireContext(),
                    "Lütfen boşlukları eksiksiz doldurun.",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
        binding.locationButton.setOnClickListener {
            val action =
             AdvertFragmentDirections.actionAdvertFragmentToMapsActivity()
            findNavController().navigate(action)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            val action =
                AdvertFragmentDirections.actionAdvertFragmentToProfileFragment()
            findNavController().navigate(action)
        } else if (item.itemId == R.id.logOut_item) {

            auth.signOut()
            val action =
               AdvertFragmentDirections.actionAdvertFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }


}