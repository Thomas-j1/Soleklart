package com.example.himmeltitting.main_data_source

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.himmeltitting.databinding.ObservationElementBinding

class ObservationAdapter (
    private var context: Context, private var dataset: MutableList<Observation>
) : RecyclerView.Adapter<ObservationAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val elementBinding: ObservationElementBinding): RecyclerView.ViewHolder(elementBinding.root){

        fun bind(observation: Observation, context: Context){
            elementBinding.temperatureText.text = observation.temperature
            elementBinding.cloudText.text = observation.cloudCover
            elementBinding.windText.text = observation.windSpeed
            elementBinding.airText.text = observation.airQuality
            elementBinding.sunsetText.text = observation.sunsetTime

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {

        val from = LayoutInflater.from(parent.context)
        val binding = ObservationElementBinding.inflate(from, parent, false)
        return ItemViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]

        holder.bind(item, context)
    }



    override fun getItemCount() = dataset.size
}
