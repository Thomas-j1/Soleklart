package com.example.himmeltitting.ui.bottomsheet

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.example.himmeltitting.databinding.DataoutputItemBinding
import com.example.himmeltitting.utils.airQualityImageCalculator
import com.example.himmeltitting.utils.cloudImageCalculator


class DataOutputAdapter(private val dataSet: List<OutputData>) :
    RecyclerView.Adapter<DataOutputAdapter.ViewHolder>()  {

    private lateinit var context: Context
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(binding: DataoutputItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val headerText = binding.header
        val cloudText = binding.cloudText
        val cloudImage = binding.cloudIcon
        val temperatureText = binding.temperatureText
        val rainText = binding.rainText
        val windText = binding.windText
        val airText = binding.airText
        val airImage = binding.airIcon

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val binding = DataoutputItemBinding.inflate(LayoutInflater.from(viewGroup.context.applicationContext), viewGroup, false)
        context = viewGroup.context.applicationContext
        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val airQualityValue = dataSet[position].airQuality
        val cloudCoverValue = dataSet[position].cloudCover
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.headerText.text = dataSet[position].header
        viewHolder.cloudText.text = cloudCoverValue
        viewHolder.temperatureText.text = dataSet[position].temperature
        viewHolder.rainText.text = dataSet[position].precipitation6Hours
        viewHolder.windText.text = dataSet[position].wind_speed
        viewHolder.airText.text = airQualityValue

        val airDrawableString = airQualityImageCalculator(airQualityValue)
        viewHolder.airImage.setImageDrawable(getImageDrawable(airDrawableString))


        val cloudDrawableString = cloudImageCalculator(cloudCoverValue)
        viewHolder.cloudImage.setImageDrawable(getImageDrawable(cloudDrawableString))

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    private fun getImageDrawable(imageString: String): Drawable? {
        val imageId = context.resources.getIdentifier(imageString, "drawable", context.packageName)
        return AppCompatResources.getDrawable(context, imageId)
    }

}