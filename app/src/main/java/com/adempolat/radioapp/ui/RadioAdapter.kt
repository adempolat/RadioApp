package com.adempolat.radioapp.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adempolat.radioapp.R
import com.adempolat.radioapp.databinding.RadioItemBinding
import com.adempolat.radioapp.service.RadioService

class RadioAdapter(
    private val context: Context,
    private val radioList: MutableList<String>
) : RecyclerView.Adapter<RadioAdapter.RadioViewHolder>() {

    var currentPlayingIndex: Int = -1
    private val favoriteRadios = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val binding = RadioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RadioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        val radioName = radioList[position]
        holder.binding.radioNameTextView.text = radioName

        holder.binding.playButton.setOnClickListener {
            val intent = Intent(context, RadioService::class.java).apply {
                action = "PLAY_RADIO"
                putExtra("RADIO_INDEX", position)
            }
            context.startService(intent)
            currentPlayingIndex = position
            notifyDataSetChanged()
        }

        holder.binding.stopButton.setOnClickListener {
            val intent = Intent(context, RadioService::class.java).apply {
                action = "STOP_RADIO"
            }
            context.startService(intent)
            currentPlayingIndex = -1
            notifyDataSetChanged()
        }

        holder.binding.favoriteButton.setOnClickListener {
            if (favoriteRadios.contains(position)) {
                favoriteRadios.remove(position)
                holder.binding.favoriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_24)
            } else {
                favoriteRadios.add(position)
                holder.binding.favoriteButton.setBackgroundResource(R.drawable.baseline_favorite_24)
            }
            updateRadioList()
        }

        // Update favorite button icon
        if (favoriteRadios.contains(position)) {
            holder.binding.favoriteButton.setBackgroundResource(R.drawable.baseline_favorite_24)
        } else {
            holder.binding.favoriteButton.setBackgroundResource(R.drawable.baseline_favorite_border_24)
        }

        if (position == currentPlayingIndex) {
            holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
        } else {
            holder.binding.root.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
    }

    override fun getItemCount(): Int {
        return radioList.size
    }

    private fun updateRadioList() {
        val favoriteList = favoriteRadios.map { radioList[it] }
        val nonFavoriteList = radioList.filterIndexed { index, _ -> !favoriteRadios.contains(index) }
        radioList.clear()
        radioList.addAll(favoriteList)
        radioList.addAll(nonFavoriteList)
        notifyDataSetChanged()
    }

    class RadioViewHolder(val binding: RadioItemBinding) : RecyclerView.ViewHolder(binding.root)
}
