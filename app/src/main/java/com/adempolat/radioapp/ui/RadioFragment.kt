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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adempolat.radioapp.R
import com.adempolat.radioapp.data.RadioList

class RadioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var radioNameTextView: TextView

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

        radioNameTextView = view.findViewById(R.id.radioNameTextView)
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RadioAdapter(requireContext(), RadioList.radioNames.toMutableList())

        // BroadcastReceiver'ı kaydet
        val intentFilter = IntentFilter("RADIO_UPDATE")
        context?.registerReceiver(radioReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // BroadcastReceiver'ı kaldır
        context?.unregisterReceiver(radioReceiver)
    }
}
