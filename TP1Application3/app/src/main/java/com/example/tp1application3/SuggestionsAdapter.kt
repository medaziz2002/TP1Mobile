package com.example.tp1application3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SuggestionsAdapter(
    private var stations: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {


    class SuggestionViewHolder(view: View, val onItemClick: (String) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val stationName: TextView = view.findViewById(R.id.item_station)

        fun bind(station: String) {
            stationName.text = station
            itemView.setOnClickListener { onItemClick(station) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggestion, parent, false)
        return SuggestionViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    override fun getItemCount(): Int = stations.size


    fun updateData(newStations: List<String>) {
        stations = newStations
        notifyDataSetChanged()
    }
}
