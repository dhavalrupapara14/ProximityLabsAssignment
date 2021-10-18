package com.proximity.labs

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.proximity.labs.databinding.ItemAqiListBinding
import com.proximity.labs.models.AirQuality
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class AirQualityAdapter : ListAdapter<AirQuality, AirQualityAdapter.ViewHolder>(AirQualityDiffCallback()) {

    private val decimalFormat = DecimalFormat("0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ItemAqiListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAqiListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AirQuality) {
            binding.airQuality = item
            binding.executePendingBindings()

            binding.tvAqi.text = decimalFormat.format(item.aqi)

            binding.clRoot.setBackgroundColor(
                    ContextCompat.getColor(
                            binding.tvAqi.context,
                            when (item.aqi.toInt()) {
                                in 0..50 -> R.color.good_green
                                in 51..100 -> R.color.satisfactory_green
                                in 101..200 -> R.color.moderate_yellow
                                in 201..300 -> R.color.poor_orange
                                in 301..400 -> R.color.very_poor_red
                                else -> R.color.severe_red
                            }
                    )
            )

            binding.tvTime.text = getFormattedTime(item.updatedTime)
        }

        private fun getFormattedTime(timeStamp: Long): String? {
            val diff = System.currentTimeMillis() - timeStamp
            val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
            var result: String? = null

            if(seconds >= 60) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

                if (minutes > 1) {
                    try {
                        result = DateFormat.format("h:mm a", timeStamp).toString()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    result = "A minute ago"
                }
            } else {
                result = "A few seconds ago"
            }

            return result
        }
    }

    class AirQualityDiffCallback : DiffUtil.ItemCallback<AirQuality>() {
        override fun areItemsTheSame(oldItem: AirQuality, newItem: AirQuality): Boolean {
            return oldItem.city == newItem.city
        }

        override fun areContentsTheSame(oldItem: AirQuality, newItem: AirQuality): Boolean {
            return oldItem == newItem
        }
    }
}