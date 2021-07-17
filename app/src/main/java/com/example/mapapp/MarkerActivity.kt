package com.example.mapapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mapapp.databinding.ActivityMarkerBinding


class MarkerActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMarkerBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMarkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sourceList.layoutManager = LinearLayoutManager(this)
        binding.sourceList.setHasFixedSize(true)
        val markerData = fillList()
        binding.sourceList.adapter = MarkerAdapter(markerData!!) { markerItem: Marker ->
            markerItemClicked(
                markerItem
            )
        }
    }

    private fun fillList(): java.util.ArrayList<Marker>? {
        val extras = intent.extras
        val markerList = extras?.getParcelableArrayList<Marker>("markerList")
        return markerList
    }


    private fun markerItemClicked(markerItem: Marker) {
        Toast.makeText(
            this,
            " ${markerItem.adress + "\n" + markerItem.latit + "\n" + markerItem.lontit}",
            Toast.LENGTH_LONG
        ).show()
        SimpleFragment.newInstance(getString(R.string.label_logout), getString(R.string.msg_logout))
            .show(supportFragmentManager, SimpleFragment.TAG)
    }
}



