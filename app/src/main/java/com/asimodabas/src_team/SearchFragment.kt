package com.asimodabas.src_team

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchRecyclerAdapter()
        searchRecycler.adapter = adapter
        searchRecycler.layoutManager = LinearLayoutManager(requireContext())

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
        firestore.collection("Search").addSnapshotListener { value, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), error.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    if (value != null) {
                        if (value.isEmpty) {
                            Toast.makeText(requireContext(), "Kayıt Bulunamadı", Toast.LENGTH_SHORT).show()
                        } else {

                            val documents = value.documents

                            searchs.clear()

                            for (document in documents) {
                                val address = document.get("Adres") as String
                                val clock = document.get("Clock") as String
                                val Activtiydate = document.get("Activtiydate") as String
                                val SearchActivity = document.get("SearchActivity") as String
                                val Notes = document.get("Notes") as String

                                val searcher = SrcSearch(SearchActivity,clock,Activtiydate,address,Notes)

                                searchs.add(searcher)
                                adapter.searchs = searchs

                            }

                        }
                    }
                }
            }
    }

    fun swipeRefresh(){
        swipeToRefresh.setOnRefreshListener {
            Toast.makeText(requireContext(),"Sayfa Yenilendi",Toast.LENGTH_SHORT).show()

            swipeToRefresh.isRefreshing = false
        }
    }


}