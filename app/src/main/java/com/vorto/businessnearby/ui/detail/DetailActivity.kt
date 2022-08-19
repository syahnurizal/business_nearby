package com.vorto.businessnearby.ui.detail

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.vorto.businessnearby.R
import com.vorto.businessnearby.databinding.ActivityMapsBinding
import com.vorto.businessnearby.util.Permission


class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var permission: Permission
    private lateinit var businessId: String

    private val viewModel: DetailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permission = Permission(this)

        businessId = intent.getStringExtra("id").toString()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true

        //request permission and get user last location
        permission.locationCallback = {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if(location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                userLocation.latitude,
                                userLocation.longitude,
                            ), 14f
                        )
                    )

                    getBusinessDetail(userLocation)
                }
            }
        }

        permission.requestLocation()
    }

    private fun getBusinessDetail(currentLocation: LatLng){

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if(isLoading) {
                binding.loading.visibility = View.VISIBLE
            }else{
                binding.loading.visibility = View.GONE
                val item = viewModel.businessDetail.value

                binding.layoutDetail.visibility = View.VISIBLE
                binding.tvName.text = item?.name ?: "-"
                binding.tvRating.text = item?.rating.toString()
                binding.tvReview.text = "${item?.review_count.toString()} review(s)"

                val address = item?.location?.display_address?.joinToString(",")
                binding.tvAddress.text = address

                Glide.with(this)
                    .load(item?.image_url)
                    .centerCrop()
                    .placeholder(R.drawable.img_placeholder)
                    .into(binding.bgImg)

                binding.tvDistance.text = "Distance: ${intent.getStringExtra("distance")}"

                val builder = LatLngBounds.Builder()
                builder.include(currentLocation)

                val businessLocation = LatLng(item?.coordinates?.latitude!!, item.coordinates?.longitude!!)
                builder.include(businessLocation)

                mMap.addMarker(
                    MarkerOptions()
                        .position(businessLocation)
                        .title(item.name ?: "Business")
                )

                val bounds = builder.build()
                val width = resources.displayMetrics.widthPixels
                val padding = (width * 0.20).toInt() // offset from edges of the map 10% of screen

                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, width, padding))

                val displayMetrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(displayMetrics)
                val height: Int = displayMetrics.heightPixels
                mMap.moveCamera(CameraUpdateFactory.scrollBy(0F,(height*0.23).toFloat()))

            }
        })

        viewModel.getBusinessDetail(businessId)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}