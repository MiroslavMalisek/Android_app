package eu.mcomputng.mobv.zadanie.fragments.profile

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("GeofenceBroadcastReceiver", "new intent")
        if (intent == null) {
            // no geofence exit
            Log.e("GeofenceBroadcastReceiver", "error 1")
            return
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {
            // error
            Log.e("GeofenceBroadcastReceiver", "error 2")
            return
        }

        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            //send error message to user using notification
            Log.e("GeofenceBroadcastReceiver", "error 3")
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringLocation = geofencingEvent.triggeringLocation
            if (context == null || triggeringLocation == null) {
                // error
                Log.e("GeofenceBroadcastReceiver", "error 4")
                return
            }
            Log.d("Geofence", "exit")
            Toast.makeText(
                context,
                "opustili ste geofence",
                Toast.LENGTH_LONG
            ).show()
            setupGeofence(triggeringLocation, context)
        }
    }

    fun setupGeofence(location: Location, context: Context) {

        val geofencingClient = LocationServices.getGeofencingClient(context.applicationContext)

        val geofence = Geofence.Builder()
            .setRequestId("my-geofence")
            .setCircularRegion(location.latitude, location.longitude, 2f) // 100m polomer
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_EXIT
            )
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        val geofencePendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // permission issue, geofence not created
            Log.e("GeofenceBroadcastReceiver", "error 5")
            return
        }
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                // Geofences boli úspešne pridané
                Log.d("GeofenceBroadcastReceiver", "novy geofence vytvoreny")
                Toast.makeText(
                    context,
                    "novy geofence vytvoreny",
                    Toast.LENGTH_LONG
                ).show()
            }
            addOnFailureListener {
                // Chyba pri pridaní geofences
                Log.e("GeofenceBroadcastReceiver", "error 6")
            }
        }

    }

    fun removeGeofences(context: Context) {
        val geofenceIdsToRemove = listOf("my-geofence")
        val geofencingClient = LocationServices.getGeofencingClient(context.applicationContext)

        geofencingClient.removeGeofences(geofenceIdsToRemove).run {
            addOnSuccessListener {
                // Geofences successfully removed
                Log.d("GeofenceBroadcastReceiver", "Geofences removed: $geofenceIdsToRemove")
            }
            addOnFailureListener {
                // Error while removing geofences
                Log.e("GeofenceBroadcastReceiver", "Error removing geofences: ${it.message}")
            }
        }
    }
}