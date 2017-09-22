package com.wvn.cwan.pokemon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemon()

        //start autorun after loadPokemon
        var arThread = AutoRunThread()
        arThread.start()
        Log.d(tag, "createMyThread")

        //set location to abac
        myLocation!!.latitude = 13.6133
        myLocation!!.longitude = 100.837
    }

    val ACCESSLOCATION = 101
    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.
                    checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        , ACCESSLOCATION)
                return
            }
        }
        getUserLocation()
    }

    fun getUserLocation() {
        Toast.makeText(this, "User location access is ON", Toast.LENGTH_LONG).show()

        var myLocationListener = MylocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,15, 3f, myLocationListener)

        var myThread = MyThread()
        myThread.start()
    }

    var pokemonList = ArrayList<Pokemon>()
    fun loadPokemon() {

        pokemonList.add(Pokemon("Charmander",
                "Grass, Poison",
                R.drawable.charmander,
                13.611117, 100.839173, false, 120.0))

        pokemonList.add(Pokemon("Pikachu",
                "None",
                R.drawable.pikachu,
                13.613249, 100.838051, false, 120.0))

        pokemonList.add(Pokemon("Rattata",
                "Charmander (Japanese: ヒトカゲ Hitokage) is a Fire-type Pokémon introduced in Generation I.",
                R.drawable.rattata,
                13.610913, 100.837665, false, 120.0))

        pokemonList.add(Pokemon("Charmander",
                "Grass, Poison",
                R.drawable.zubat,
                13.612003, 100.835879, false, 120.0))

        pokemonList.add(Pokemon("Meowth (Japanese: ニャース Nyarth) is a Normal-type Pokémon introduced in Generation I.",
                "Grass, Poison",
                R.drawable.meowth,
                13.612916, 100.836193, false, 120.0))

        pokemonList.add(Pokemon("Charmander",
                "Grass, Poison",
                R.drawable.dratini,
                13.613009, 100.834125, false, 120.0))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "We cannot access to your location", Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    var myLocation:Location? = null
    inner class MylocationListener: LocationListener {
        constructor() {
            myLocation = Location("")
            myLocation!!.latitude = 0.0
            myLocation!!.longitude = 0.0
        }
        override fun onLocationChanged(location: Location?) {
            myLocation = location
//            Toast.makeText(applicationContext,"${location!!.latitude}, ${location!!.longitude}",Toast.LENGTH_LONG).show()
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
    var tag = "WvN"
    var oldLocation:Location? = null
    var powerOfUser: Double = 120.00
    inner class MyThread: Thread {
        constructor():super() {
            oldLocation = Location("")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0
            Log.d(tag, "constructorMyThread")
        }

        override fun run() {
            while (true) {
                try {
                    if (oldLocation!!.distanceTo(myLocation) == 0f) {
                        //continue
                    }

                    oldLocation = myLocation

                    //display
                    runOnUiThread {
                        mMap!!.clear()

                        val latLng = LatLng(myLocation!!.latitude,
                                myLocation!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(latLng)
                                .title("Me")
                                .snippet("here is my location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.player)))
                        mMap!!.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(latLng, 17f))

                        for (i in 0..pokemonList.size - 1) {
                            var newPokemon = pokemonList[i]

                            if (newPokemon.isCatch == false) {
                                var pokemonLocation = LatLng(newPokemon.location!!.latitude,
                                        newPokemon.location!!.longitude)

                                mMap!!.addMarker(MarkerOptions()
                                        .position(pokemonLocation)
                                        .title(newPokemon.name)
                                        .snippet(newPokemon.description)
                                        .icon(BitmapDescriptorFactory.fromResource(newPokemon.image!!)))
                                if (myLocation!!.distanceTo(newPokemon.location!!) < 10) {
                                    Log.d(tag, "catched")
                                    newPokemon.isCatch = true
                                    powerOfUser += newPokemon.power!!
                                    pokemonList[i] = newPokemon
                                    Toast.makeText(this@MapsActivity, "Now Your Power is " + powerOfUser, Toast.LENGTH_SHORT).show()
                                    //next
                                    currCatchIndex++
                                }
                            }
                        }
                    }

                    Thread.sleep(750)
                } catch (ex:Exception) {
                    Log.e("THREAD-RUN", ex.message)
                }
            }
        }
    }

    var currCatchIndex: Int = 0
    var speed: Double = 0.30;
    inner class AutoRunThread: Thread {
        constructor():super() {
            Log.d(tag, "constructorMyThread")
        }

        override fun run() {
            while (true) {
                try {
                    Log.d(tag, "curr "+pokemonList.size)
                    Log.d(tag, "size "+pokemonList.size)
                    if(currCatchIndex == pokemonList.size){
                        Log.d(tag, "stop")
                        break
                    }

                    var nearPokemon = pokemonList[currCatchIndex]
                    if(nearPokemon.isCatch == false){
                        Log.d(tag, "move")
                        myLocation!!.latitude = lerp(myLocation!!.latitude,nearPokemon.location!!.latitude,speed)
                        myLocation!!.longitude = lerp(myLocation!!.longitude,nearPokemon.location!!.longitude,speed)
                    }

                    Thread.sleep(500)
                } catch (ex:Exception) {
                    Log.e("THREAD-RUN", ex.message)
                }
            }
        }

        //https://en.wikipedia.org/wiki/Linear_interpolation
        //v0 = currlocation, v1 = destination, t = % value between 0-1
        fun lerp(v0: Double,v1: Double,t: Double): Double {
            return (1 - t) * v0 + t * v1
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
