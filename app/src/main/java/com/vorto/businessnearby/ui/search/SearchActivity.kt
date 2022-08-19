package com.vorto.businessnearby.ui.search

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.vorto.businessnearby.databinding.ActivitySearchBinding
import com.vorto.businessnearby.ui.detail.DetailActivity
import com.vorto.businessnearby.ui.search.adapter.BusinessAdapter
import com.vorto.businessnearby.util.Permission
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var permission: Permission
    private val viewModel: SearchViewModel by viewModels()

    var debouncePeriod: Long = 600
    private val coroutineScope = lifecycle.coroutineScope
    private var searchJob: Job? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permission = Permission(this)

        //trigger search using debouncer
        binding.etSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = coroutineScope.launch {
                it?.let {
                    delay(debouncePeriod)
                    if (it.toString().isEmpty()) {
                        binding.seekbar.visibility = View.GONE
                        binding.tvRadius.visibility = View.GONE
                        viewModel.resetResult()
                    } else {
                        binding.seekbar.visibility = View.VISIBLE
                        binding.tvRadius.visibility = View.VISIBLE
                        viewModel.searchBusiness(it.toString(), binding.seekbar.progress.toString())
                    }
                }
            }
        }

        //request permission and get user last location
        permission.locationCallback = {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location != null) {
                    viewModel.setLatitude(location.latitude.toString())
                    viewModel.setLongitude(location.longitude.toString())
                    //viewModel.searchBusiness("tom yam")
                }
            }
        }

        permission.requestLocation()

        //listen to radius seekbar changes
        binding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val distanceKM = p1.toDouble().div(1000)
                binding.tvRadius.text = "Radius: ${distanceKM}km"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                viewModel.searchBusiness(null, p0?.progress.toString())
            }

        })

        initRecyclerView()
    }

    private fun initRecyclerView(){
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if(isLoading) {
                binding.loading.visibility = View.VISIBLE
            }else{
                binding.loading.visibility = View.GONE

                if(viewModel.businessList.value.isNullOrEmpty()){
                    if(!viewModel.searchTerm.value.isNullOrEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
                }else{
                    binding.tvEmpty.visibility = View.GONE
                }

                val businessAdapter = BusinessAdapter(ArrayList(viewModel.businessList.value!!)){ type, businessModel ->
                    if(type == "view") {

                        val distance =  businessModel.distance
                        val distanceKM = distance?.div(1000) ?: 0

                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("id", businessModel.id)
                        intent.putExtra("distance", "${String.format("%.2f", distanceKM)} KM")
                        startActivity(intent)
                    }
                }
                binding.recyclerView.adapter = businessAdapter
            }
        })
    }

}