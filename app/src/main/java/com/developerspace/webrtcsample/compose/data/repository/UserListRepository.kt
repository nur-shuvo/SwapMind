package com.developerspace.webrtcsample.compose.data.repository

import android.location.Location
import androidx.core.util.Consumer
import com.developerspace.webrtcsample.compose.data.db.dao.UserDao
import com.developerspace.webrtcsample.compose.data.db.entity.UserData
import com.developerspace.webrtcsample.compose.data.model.User
import com.developerspace.webrtcsample.compose.ui.util.Topic
import com.developerspace.webrtcsample.compose.extension.USER_LOCATION_PATH
import com.developerspace.webrtcsample.legacy.ChatMainActivity
import com.developerspace.webrtcsample.legacy.MainActivity
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.firebase.geofire.LocationCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserListRepository @Inject constructor(private val userDao: UserDao) {
    private var db: FirebaseDatabase = Firebase.database
    private val workerScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "UserListRepository"
    }

    fun prePopulateAllUsersInDb() {
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD).get()
            .addOnSuccessListener { snapShot ->
                snapShot.getValue<MutableMap<String, User>>()?.let {
                    val resultList: MutableList<User> = mutableListOf()
                    it.forEach { entry ->
                        val user = entry.value
                        resultList.add(user)
                        saveUserInDb(user)
                    }
                    Timber.i("total users - ${resultList.size}")
                }
            }
    }

    fun getUserListFromDbFlow(): Flow<List<User>> {
        return userDao.getAllUser().map { userDataList ->
            userDataList.map { userData ->
                User(userData.userId, userData.userName, userData.profileUrl, userData.onlineStatus)
            }
        }
    }

    fun getUserByUserID(userID: String): Flow<UserData> {
        return userDao.getUserProfileData(userID)
    }

    fun fetchAllUsersRemote() {
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue<MutableMap<String, User>>()?.let {
                            val resultList: MutableList<User> = mutableListOf()
                            it.forEach { entry ->
                                val user = entry.value
                                resultList.add(user)
                                saveUserInDb(user)
                            }
                            Timber.i("total users - ${resultList.size}")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO("Not yet implemented")
                    }
                }
            )
    }

    fun fetchAllActiveUsersByTopic(topic: Topic, consumer: Consumer<List<User>>) {
        // TODO db path will change based on topic
        db.reference.child(ChatMainActivity.ROOT).child(MainActivity.ONLINE_USER_LIST_CHILD)
            .addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.getValue<MutableMap<String, User>>()?.let {
                            val resultList: MutableList<User> = mutableListOf()
                            it.forEach { entry ->
                                val user = entry.value
                                resultList.add(user)
                                saveUserInDb(user)
                            }
                            Timber.i("total users - ${resultList.size}")
                            consumer.accept(resultList)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // TODO("Not yet implemented")
                    }
                }
            )
    }

    private fun saveUserInDb(user: User) {
        workerScope.launch {
            userDao.insertUser(
                UserData(
                    user.userID!!,
                    user.userName,
                    user.photoUrl,
                    user.onlineStatus
                )
            )
        }
    }

    fun fetchAllNearByUsers(distanceInKm: Long, consumer: Consumer<List<Pair<User, Long>>>) {
        val workerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val geoFire = GeoFire(
                Firebase.database.reference.child(ChatMainActivity.ROOT).child(USER_LOCATION_PATH)
            )
            geoFire.getLocation(Firebase.auth.uid, object : LocationCallback {
                override fun onLocationResult(key: String?, location: GeoLocation?) {
                    if (location != null) {
                        val currentNearByUsers: MutableSet<Pair<User, Long>> = mutableSetOf()
                        // now query with the distanceInKm
                        val geoQuery = geoFire.queryAtLocation(location, distanceInKm.toDouble())
                        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
                            override fun onKeyEntered(key: String?, location1: GeoLocation?) {
                                if (location1 != null) {
                                    workerScope.launch {
                                        if (key != null) {
                                            getUserByUserID(key).collect { userData ->
                                                currentNearByUsers.add(
                                                    User(
                                                        userData.userId,
                                                        userData.userName,
                                                        userData.profileUrl,
                                                        userData.onlineStatus
                                                    ) to
                                                            distanceInKiloMeter(
                                                                location.latitude,
                                                                location.longitude,
                                                                location1.latitude,
                                                                location1.longitude
                                                            )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onKeyExited(key: String?) {

                            }

                            override fun onKeyMoved(key: String?, location: GeoLocation?) {}

                            override fun onGeoQueryReady() {
                                workerScope.launch {
                                    delay(5000)
                                    consumer.accept(currentNearByUsers.toMutableList())
                                    workerScope.cancel()
                                }
                            }

                            override fun onGeoQueryError(error: DatabaseError?) {}

                        })
                    } else {
                        println(String.format("There is no location for key %s in GeoFire", key))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Timber.e("There was an error getting the GeoFire location: $databaseError")
                }
            })
        } catch (_: Exception) {}
    }

    private fun distanceInKiloMeter(
        startLat: Double,
        startLon: Double,
        endLat: Double,
        endLon: Double
    ): Long {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLon, endLat, endLon, results)
        return (results[0] / 1000).toLong()
    }
}