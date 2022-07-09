package com.asimodabas.src_team.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asimodabas.Constants
import com.asimodabas.src_team.R
import com.asimodabas.src_team.adapter.SearchRecyclerAdapter
import com.asimodabas.src_team.databinding.FragmentSearchBinding
import com.asimodabas.src_team.model.SrcSearch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: SearchRecyclerAdapter
    private var searchs = arrayListOf<SrcSearch>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        auth = Firebase.auth
        firestore = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchRecyclerAdapter()
        binding.searchRecycler.adapter = adapter
        binding.searchRecycler.layoutManager = LinearLayoutManager(requireContext())

        swipeRefresh()
        getData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.account_item) {
            val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment()
            findNavController().navigate(action)
        } else if (item.itemId == R.id.logOut_item) {

            auth.signOut()
            val action = SearchFragmentDirections.actionSearchFragmentToLoginFragment()
            findNavController().navigate(action)
        }
        return super.onOptionsItemSelected(item)
    }


    fun getData() {
        firestore.collection(Constants.SEARCH).addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            } else {
                if (value != null) {
                    if (value.isEmpty) {
                        Toast.makeText(requireContext(), "Kayıt Bulunamadı", Toast.LENGTH_SHORT)
                            .show()
                    } else {

                        val documents = value.documents

                        searchs.clear()

                        for (document in documents) {
                            val address = document.get("Adres") as String
                            val clock = document.get("Clock") as String
                            val Activtiydate = document.get("Activtiydate") as String
                            val SearchActivity = document.get("SearchActivity") as String
                            val Notes = document.get("Notes") as String

                            val searcher =
                                SrcSearch(SearchActivity, clock, Activtiydate, address, Notes)

                            searchs.add(searcher)
                            adapter.searchs = searchs
                        }
                    }
                }
            }
        }
    }

    fun swipeRefresh() {
        binding.swipeToRefresh.setOnRefreshListener {
            Toast.makeText(requireContext(), "Sayfa Yenilendi", Toast.LENGTH_SHORT).show()

            binding.swipeToRefresh.isRefreshing = false
        }
    }
}