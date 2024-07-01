package com.adempolat.radioapp

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var radioNameTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_radio, container, false)

        playButton = view.findViewById(R.id.playButton)
        stopButton = view.findViewById(R.id.stopButton)
        nextButton = view.findViewById(R.id.nextButton)
        prevButton = view.findViewById(R.id.prevButton)
        statusTextView = view.findViewById(R.id.statusTextView)
        radioNameTextView = view.findViewById(R.id.radioNameTextView)

        playButton.setOnClickListener {
            playRadio()
        }

        stopButton.setOnClickListener {
            stopRadio()
        }

        nextButton.setOnClickListener {
            nextRadio()
        }

        prevButton.setOnClickListener {
            prevRadio()
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

    private fun nextRadio() {
        activity?.let {
            val intent = Intent(it, RadioService::class.java).apply {
                action = "NEXT_RADIO"
            }
            it.startService(intent)
        }
    }

    private fun prevRadio() {
        activity?.let {
            val intent = Intent(it, RadioService::class.java).apply {
                action = "PREV_RADIO"
            }
            it.startService(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}
