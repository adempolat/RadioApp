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

class RadioFragment : Fragment() {

    private var _binding: FragmentRadioBinding? = null
    private val binding get() = _binding!!

    private val radioReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val radioName = intent?.getStringExtra("RADIO_NAME")
            binding.radioNameTextView.text = "Current Radio: $radioName"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRadioBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = RadioAdapter(requireContext(), RadioList.radioNames.toMutableList())

        // BroadcastReceiver'ı kaydet
        val intentFilter = IntentFilter("RADIO_UPDATE")
        context?.registerReceiver(radioReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // BroadcastReceiver'ı kaldır
        context?.unregisterReceiver(radioReceiver)
        _binding = null
    }
}
