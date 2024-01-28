package com.example.runaway_aos.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.example.runaway_aos.LocationUpdateListener
import com.example.runaway_aos.LocationUpdateListenerHolder
import com.example.runaway_aos.util.Constants.ACTION_START_SERVICE
import com.example.runaway_aos.util.Constants.ACTION_STOP_SERVICE
import com.example.runaway_aos.util.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runaway_aos.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runaway_aos.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runaway_aos.util.Constants.NOTIFICATION_ID
import com.example.runaway_aos.util.hasLocationPermissions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class RunningService: LifecycleService() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var locationRequest: LocationRequest

    private val timeInterval = 1000L

    private val isTracking = MutableStateFlow(false)
    private var locationUpdateListener: LocationUpdateListener? = null

    override fun onCreate() {
        super.onCreate()

        locationRequest = createLocationRequest()
        lifecycleScope.launch {
            isTracking.collectLatest {
                updateLocationTracking(it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand ${intent?.action}")

        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    Timber.d("Start Service")
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                    Timber.d("Stopped Service")
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    fun setLocationUpdateListener(listener: LocationUpdateListener) {
        this.locationUpdateListener = listener
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            Timber.e("updateLocationTracking tracking")
            if (hasLocationPermissions(this)) {
                Timber.e("updateLocationTracking hasLocationPermissions")

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack)
        }
    }

    private fun startForegroundService() {
        isTracking.value = true
        this.locationUpdateListener = LocationUpdateListenerHolder.listener
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        createNotificationChannel(notificationManager)
        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

    }

    private fun killService() {
        isTracking.value = false
        stopForeground(true)
        stopSelf()
    }

    private val locationCallBack = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value) {
                for (location in result.locations) {
                    Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    locationUpdateListener?.onLocationUpdate(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun createLocationRequest() = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
        // setMinUpdateDistanceMeters(minimalDistance)
        setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        setIntervalMillis(LOCATION_UPDATE_INTERVAL)
        setWaitForAccurateLocation(true)
    }.build()

}