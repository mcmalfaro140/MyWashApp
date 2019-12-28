package com.example.myfinalproject


import android.app.IntentService
import android.content.ContentValues.TAG
import android.content.Intent
import  android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.provider.SyncStateContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.myfinalproject.Adapter.ProviderAdapter
import com.example.myfinalproject.R.layout.*
import com.example.myfinalproject.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException


import kotlin.collections.ArrayList

class MapFragment : Fragment() ,  OnMapReadyCallback,   GoogleMap.OnMarkerClickListener{


    //to get the database reference
    var  db = FirebaseFirestore.getInstance()


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1


        //is used as the request code passed to onActivityResult
        private const val REQUEST_CHECK_SETTINGS = 2
    }


    //Declare a LocationCallback property
    private lateinit var locationCallback: LocationCallback
    // Declare a LocationRequest property and a location updated state property
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var lastLocation: Location
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var listView : ListView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentMapBinding = DataBindingUtil.inflate(inflater, fragment_map, container, false)
        binding.providersList.setVisibility(View.GONE)


       // val model= ViewModelProviders.of(FragmentActivity())[Communicator::class.java]

        listView = binding.providersList
        binding.findProvidersButton.setOnClickListener {
            if(binding.providersList.isVisible){
                binding.providersList.setVisibility(View.GONE);
                binding.findProvidersButton.text = "Show List of Providers"
            }else{
                binding.providersList.setVisibility(View.VISIBLE);
                binding.findProvidersButton.text = "Hide List"
            }

        }

        //for the map
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //this with requireActivity()
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())



        //for the database
        var arrayList = ArrayList<String>()
      //  var arrayprice = ArrayList<String>()

        //for the database
        var ref = db.collection("Providers")

        ref.get().addOnSuccessListener { documents ->
            for (doc in documents) {
                var str = "${doc.get("Name")}-${doc.get("basicPrice")}-${doc.get("Email")} "
                arrayList.add(str)
            }
            //This displays the RecyclerView with the custom row view
            val adapter = ProviderAdapter(context!!, arrayList)
            listView.adapter = adapter

        }



        return binding.root
    }



    //function
    override fun onMarkerClick(p0: Marker?) = false

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        //CSUL marker

        val myPlace = LatLng(34.0668, -118.1684)
        map.addMarker(MarkerOptions().position(myPlace).title("MyWash Customer"))
        //for the zoom level
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)//to react to marker click


        setUpMap()

        //to add the blue dot for current loc/ button to center map
        map.isMyLocationEnabled = true


        //to get the most current location available
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            // move camera to the user current location
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                Log.i("User Current Position", currentLatLng.toString())//hast the user current position
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

    }



    //fix the this issue with requireActivity
    // checks if the app has been granted the ACCESS_FINE_LOCATION permission.
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true

        //to set diferent map views
        //map.mapType = GoogleMap.MAP_TYPE_TERRAIN
       // map.mapType = GoogleMap.MAP_TYPE_NORMAL
        //map.mapType = GoogleMap.MAP_TYPE_HYBRID
       // map.mapType = GoogleMap.MAP_TYPE_SATELLITE

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }



    //Markers
    private fun placeMarkerOnMap(location: LatLng) {

        // sets the current ps as marker pos
        val markerOptions = MarkerOptions().position(location)


       //to return the address string
         val titleStr = getAddress(location)
        Log.i("User address", titleStr)//hast the user current position//hs hello
         markerOptions.title(titleStr)

        Log.i("User marker", titleStr)//hast the user current position//has hello

        //to change the color of the marker
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))


//to put the image on the map
       // markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
        //    BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_round)))



        // adds marker to map
        map.addMarker(markerOptions)



    }



//This takes the coordinates of a location and returns a readable address and vice versa
    private fun getAddress(latLng: LatLng): String {


        val geocoder = Geocoder(requireActivity())
        var addresses: List<Address> = emptyList()
        lateinit var address: Address

        var addressText = "  " // shows the address of user when maker is touch

       try {

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)//gets one location

            if ((null != addresses && addresses.isNotEmpty())) {
                Log.i("misael", "addresses inside")
                address = addresses[0]

                for (i in 0 until address.maxAddressLineIndex) {
                    Log.i("misael", "inside build")
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
       } catch (ioException: IOException) {

            Log.e("MapsActivity", ioException.localizedMessage)
        }



        return addressText
    }




    private fun startLocationUpdates() {
        //In startLocationUpdates(), if the ACCESS_FINE_LOCATION permission has not been granted,
        // request it now and return
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //if there is permission, request for location updates.
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }




}
