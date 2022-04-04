package com.asimodabas.src_team

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_advert.*
import kotlinx.android.synthetic.main.fragment_create.*


class AdvertFragment : Fragment() {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        advSearchButton.setOnClickListener {

            if (
                editTextTextPersonName2.text.toString() != "" &&
                editTextTextPersonName4.text.toString() != "" &&
                editTextTextPersonName3.text.toString() != "" &&
                editTextTextPersonName5.text.toString() != "" &&
                editTextTextPersonName6.text.toString() != ""
            ) {
                auth.currentUser?.let {

                    val address = editTextTextPersonName2.text.toString()
                    val clock = editTextTextPersonName4.text.toString()
                    val Activtiydate = editTextTextPersonName3.text.toString()
                    val SearchActivity = editTextTextPersonName5.text.toString()
                    val Notes = editTextTextPersonName6.text.toString()

                    val dataMap = HashMap<String, Any>()

                    dataMap.put("Adres", address)
                    dataMap.put("Clock", clock)
                    dataMap.put("Activtiydate", Activtiydate)
                    dataMap.put("SearchActivity", SearchActivity)
                    dataMap.put("Notes", Notes)

                    firestore.collection("Search").add(dataMap).addOnSuccessListener {

                        val action = AdvertFragmentDirections.actionAdvertFragmentToSearchFragment()
                        findNavController().navigate(action)

                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
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
        locationButton.setOnClickListener {
            val action = AdvertFragmentDirections.actionAdvertFragmentToMapsActivity()
            findNavController().navigate(action)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            val action = AdvertFragmentDirections.actionAdvertFragmentToProfileFragment()
            findNavController().navigate(action)
        } else if (item.itemId == R.id.logOut_item) {

            auth.signOut()
            val action = AdvertFragmentDirections.actionAdvertFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }



}