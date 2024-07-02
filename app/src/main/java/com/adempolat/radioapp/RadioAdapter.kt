package com.adempolat.radioapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RadioAdapter(
    private val context: Context,
    private val radioList: List<String>
) : RecyclerView.Adapter<RadioAdapter.RadioViewHolder>() {

    var currentPlayingIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.radio_item, parent, false)
        return RadioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        val radioName = radioList[position]
        holder.radioNameTextView.text = radioName

        holder.playButton.setOnClickListener {
            val intent = Intent(context, RadioService::class.java).apply {
                action = "PLAY_RADIO"
                putExtra("RADIO_INDEX", position)
            }
            context.startService(intent)
            currentPlayingIndex = position
            notifyDataSetChanged()
        }

        holder.stopButton.setOnClickListener {
            val intent = Intent(context, RadioService::class.java).apply {
                action = "STOP_RADIO"
            }
            context.startService(intent)
            currentPlayingIndex = -1
            notifyDataSetChanged()
        }

        if (position == currentPlayingIndex) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }
    }

    override fun getItemCount(): Int {
        return radioList.size
    }

    class RadioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val radioNameTextView: TextView = itemView.findViewById(R.id.radioNameTextView)
        val playButton: Button = itemView.findViewById(R.id.playButton)
        val stopButton: Button = itemView.findViewById(R.id.stopButton)
    }
}
