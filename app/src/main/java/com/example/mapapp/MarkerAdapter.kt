package com.example.mapapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapapp.databinding.ItemMarkerBinding

class MarkerAdapter(val names: ArrayList<Marker>, private val clickListener: (Marker) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMarkerBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return names.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(names[position], clickListener)
    }

    class ViewHolder(private val binding: ItemMarkerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(part: Marker, clickListener: (Marker) -> Unit) {
            binding.editLat.text = part.latit
            binding.editLng.text = part.lontit
            binding.adress.text = part.adress
            binding.root.setOnClickListener { clickListener(part) }
        }
    }
}
