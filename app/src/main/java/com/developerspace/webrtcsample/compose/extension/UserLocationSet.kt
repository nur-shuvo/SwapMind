package com.developerspace.webrtcsample.compose.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
const val REQUEST_GPS = 102
const val USER_LOCATION_PATH = "userLocation"
var fusedLocationClient: FusedLocationProviderClient? = null

fun AppCompatActivity.tryToSetUserLocation() {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    if (checkPermissions()) {
        if (!checkIfGpsEnabled(this)) {
            showDialogToEnableLocation()
        } else {
            requestLocationUpdates()
        }
    } else {
        requestPermissions()
    }
}

fun AppCompatActivity.showDialogToEnableLocation() {
    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setTitle("Enable Location")
    alertDialogBuilder.setMessage("To get the most of the features please enable location")
    alertDialogBuilder.setPositiveButton("OK") { _, _ ->
        val settingintent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(settingintent, REQUEST_GPS)
    }
    alertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
        Timber.i("ancel button is clicked on location enable popup")
    }
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}

fun checkIfGpsEnabled(context: Context): Boolean {
    return isLocationEnabled(context)
}

private fun isLocationEnabled(context: Context): Boolean {
    return getLocationMode(context) != Settings.Secure.LOCATION_MODE_OFF
}

private fun getLocationMode(context: Context): Int {
    return Settings.Secure.getInt(
        context.contentResolver,
        Settings.Secure.LOCATION_MODE,
        Settings.Secure.LOCATION_MODE_OFF
    )
}

fun AppCompatActivity.checkPermissions(): Boolean {
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

fun AppCompatActivity.requestPermissions() {
    ActivityCompat.requestPermissions(
        this,
        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_PERMISSION
    )
}