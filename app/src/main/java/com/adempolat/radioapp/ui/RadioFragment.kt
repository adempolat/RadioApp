package com.adempolat.radioapp.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adempolat.radioapp.data.RadioList
import com.adempolat.radioapp.databinding.FragmentRadioBinding

class RadioFragment : Fragment(), OnRadioPlayListener {

    private var _binding: FragmentRadioBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: RadioAdapter

    private val radioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val radioName = intent?.getStringExtra("RADIO_NAME")
            binding.radioNameTextView.text = "Current Radio: $radioName"
        }
    }

    private val notificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val radioIndex = intent?.getIntExtra("RADIO_INDEX", -1)
            radioIndex?.let {
                if (it != -1) {
                    adapter.updateCurrentPlayingIndex(it)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRadioBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = RadioAdapter(requireContext(), RadioList.radioNames.toMutableList(), this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        // BroadcastReceiver'ı kaydet
        val intentFilter = IntentFilter("RADIO_UPDATE")
        context?.registerReceiver(radioReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

        val notificationFilter = IntentFilter("RADIO_PLAY_FROM_NOTIFICATION")
        context?.registerReceiver(notificationReceiver, notificationFilter, Context.RECEIVER_NOT_EXPORTED)

        return view
    }

    override fun onRadioPlay(radioName: String) {
        binding.currentRadioNameTextView.text = "Playing: $radioName"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // BroadcastReceiver'ı kaldır
        context?.unregisterReceiver(radioReceiver)
        context?.unregisterReceiver(notificationReceiver)
        _binding = null
    }
}
