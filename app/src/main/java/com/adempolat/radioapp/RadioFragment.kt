package com.adempolat.radioapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class RadioFragment : Fragment() {

    private lateinit var playButton: Button
    private lateinit var stopButton: Button
    private lateinit var statusTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_radio, container, false)

        playButton = view.findViewById(R.id.playButton)
        stopButton = view.findViewById(R.id.stopButton)
        statusTextView = view.findViewById(R.id.statusTextView)

        playButton.setOnClickListener {
            playRadio()
        }

        stopButton.setOnClickListener {
            stopRadio()
        }

        return view
    }

    private fun playRadio() {
        activity?.startService(Intent(activity, RadioService::class.java))
        statusTextView.text = "Radio Status: Playing"
    }

    private fun stopRadio() {
        activity?.let {
            val intent = Intent(it, RadioService::class.java).apply {
                action = "STOP_RADIO"
            }
            it.startService(intent)
        }
        statusTextView.text = "Radio Status: Stopped"
    }
}
