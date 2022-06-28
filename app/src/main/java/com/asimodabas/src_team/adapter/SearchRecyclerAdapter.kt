package com.asimodabas.src_team.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asimodabas.src_team.R
import com.asimodabas.src_team.model.SrcSearch

class SearchRecyclerAdapter : RecyclerView.Adapter<SearchRecyclerAdapter.SearchHolder>() {

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    private val diffUtil = object : DiffUtil.ItemCallback<SrcSearch>() {
        override fun areItemsTheSame(oldItem: SrcSearch, newItem: SrcSearch): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SrcSearch, newItem: SrcSearch): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var searchs: List<SrcSearch>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)
        return SearchHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val activityTextView = holder.itemView.findViewById<TextView>(R.id.activtyText)
        val noteTextView = holder.itemView.findViewById<TextView>(R.id.notetextView)
        val hourTextView = holder.itemView.findViewById<TextView>(R.id.hourTextView)
        val dateTextView = holder.itemView.findViewById<TextView>(R.id.dateTextView)
        val adressTextView = holder.itemView.findViewById<TextView>(R.id.adresTextView)

        activityTextView.text = "${searchs.get(position).activity}"
        noteTextView.text = "${searchs.get(position).notes}"
        hourTextView.text = "${searchs.get(position).clock}"
        dateTextView.text = "${searchs.get(position).date}"
        adressTextView.text = "${searchs.get(position).address}"


    }

    override fun getItemCount(): Int {
        return searchs.size
    }
}