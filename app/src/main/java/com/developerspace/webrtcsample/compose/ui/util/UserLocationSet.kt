package com.developerspace.webrtcsample.compose.ui.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber

const val REQUEST_LOCATION_PERMISSION = 101
const val USER_LOCATION_PATH = "userLocation"
var fusedLocationClient: FusedLocationProviderClient? = null

fun Activity.tryToSetUserLocation() {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    if (checkPermissions()) {
        requestLocationUpdates()
    } else {
        requestPermissions()
    }
}

fun Activity.checkPermissions(): Boolean {
    return (ActivityCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED)
}

@SuppressLint("MissingPermission")
fun requestLocationUpdates() {
    fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
        // Got last known location. In some rare situations, this can be null.
        if (location != null) {
            val latitude = location.latitude
            val longitude = location.longitude

            Timber.i("Latitude: $latitude, Longitude: $longitude")
            val geoFire = GeoFire(
                Firebase.database.reference.child(ChatMainActivity.ROOT).child(USER_LOCATION_PATH)
            )
            geoFire.setLocation(
                Firebase.auth.uid.toString(),
                GeoLocation(location.latitude, location.longitude)
            )
        }
    }
}

fun Activity.requestPermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_PERMISSION
    )
}