package com.adempolat.radioapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RadioFragment : Fragment() {

    private lateinit var playButton: Button
    private lateinit var stopButton: Button
    private lateinit var nextButton: Button
    private lateinit var prevButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var radioNameTextView: TextView
    private lateinit var recyclerView: RecyclerView

    private val radioList = listOf(
        "Power Pop",
        "A Spor Radyo",
        "Alem FM",
        "Altın Şarkılar",
        "Slow Karadeniz",
        "Kafa Radyo",
        "Doksanlar",
        "ClassicLand",
        "Baba Radyo",
        "Arabeskland",
        "90'lar"
    )

    private val radioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val radioName = intent?.getStringExtra("RADIO_NAME")
            radioNameTextView.text = "Current Radio: $radioName"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
        recyclerView = view.findViewById(R.id.recyclerView)

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

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RadioAdapter(requireContext(), radioList)

        // BroadcastReceiver'ı kaydet
        val intentFilter = IntentFilter("RADIO_UPDATE")
        context?.registerReceiver(radioReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

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
        // BroadcastReceiver'ı kaldır
        context?.unregisterReceiver(radioReceiver)
    }
}
