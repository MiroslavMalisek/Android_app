package eu.mcomputng.mobv.zadanie.workers

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.DataRepository
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedUpdateWorker(context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val inputData = inputData
        val feedNotificationChannel = inputData.getString("channel_id")
        val dataRepository = DataRepository.getInstance(context)
        val user = PreferenceData.getInstance().getUser(context)
        val sharing = PreferenceData.getInstance().getSharing(context)

        if (user == null){
            Log.d("FeedUpdateWorker", "no user logged in = no action")
            return Result.success()
        }
        if (sharing){
            //notify about the feed change
            try {
                // Step 1: Get users from cache
                val cachedUsers = withContext(Dispatchers.IO) {
                    dataRepository.getUsersList()
                }
                //Log.d("FeedUpdateWorker", "Cached Users: $cachedUsers")
                /*cachedUsers.forEach {
                    Log.d("cached", "User name: ${it.name}")
                }*/

                // Step 2: Call the API to update geofence users
                withContext(Dispatchers.IO) {
                    dataRepository.apiGetGeofenceUsers(context)
                }
                val updatedUsers = withContext(Dispatchers.IO) {
                    dataRepository.getUsersList()
                }
                //Log.d("FeedUpdateWorker", "API Result: $updatedUsers")
                /*updatedUsers.forEach {
                    Log.d("updated", "User name: ${it.name}")
                }*/

                // Option 1: get users that were cached and are present in new api call
                val commonUsers = cachedUsers.filter { cachedUser ->
                    updatedUsers.any { updatedUser -> updatedUser.id == cachedUser.id }
                }
                commonUsers.forEach {
                    Log.d("commonUser", "User name: ${it.name}, User ID: ${it.id}")
                }

                //Option 2: get users that are new (were not cached)
                val onlyNewUsers = updatedUsers.filter { updatedUser ->
                    cachedUsers.none { cachedUser -> cachedUser.id == updatedUser.id }
                }
                if (onlyNewUsers.isEmpty()){
                    Log.d("newUsers", "empty")
                }else{
                    onlyNewUsers.forEach {
                        Log.d("newUser", "User name: ${it.name}, User ID: ${it.id}")
                    }
                }

                //Option 3: get users that left
                val removedUsers = cachedUsers.filter { cachedUser ->
                    updatedUsers.none { updatedUser -> updatedUser.id == cachedUser.id }
                }

                //Option 4: count number of updated Users and size change
                val updatedUsersSize = updatedUsers.size
                val sizeChange = updatedUsers.size - cachedUsers.size

                if (feedNotificationChannel != null) {
                    this.createNotificationFeedChange(context, feedNotificationChannel, updatedUsersSize.toInt(), sizeChange.toInt())
                }

                return Result.success()
            } catch (ex: Exception) {
                Log.e("FeedUpdateWorker", "Error: ${ex.message}")
                return Result.retry()
            }
        }else{
            //notify about switch on the sharing
            Log.d("FeedUpdateWorker", "no sharing")
            if (feedNotificationChannel != null) {
                this.createNotificationTurnOnSharing(context, feedNotificationChannel)
            }
            return Result.success()
        }

    }

    fun createNotificationFeedChange(context: Context, feedNotificationChannel: String, newUsersSize: Int, sizeChange: Int){
        val channelId = feedNotificationChannel
        val builder =
            NotificationCompat.Builder(context, channelId).apply {
                setContentTitle("Nový používatelia vo vašom okolí")
                if (newUsersSize == 0){
                    setContentText("Vo vašom okolí sa nikto nenachádza")
                }else if (sizeChange < 0){
                    setContentText("Počet používateľov: ${newUsersSize}, ubudli ${-sizeChange} používatelia")
                }else{
                    setContentText("Počet používateľov: ${newUsersSize}, pribudli ${sizeChange} používatelia")
                }

                setSmallIcon(R.drawable.ic_launcher_foreground)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

        if (!hasNotificationsPermissions(context)) {
            Log.d("Notifikacia","Chyba povolenie na notifikaciu");
            return
        }

        NotificationManagerCompat.from(context).notify(1, builder.build())
    }

    fun createNotificationTurnOnSharing(context: Context, feedNotificationChannel: String,){
        val channelId = feedNotificationChannel
        val builder =
            NotificationCompat.Builder(context, channelId).apply {
                setContentTitle("Zapnite si zdieľanie polohy aby ste videli ostatných používateľov!")
                setSmallIcon(R.drawable.ic_launcher_foreground)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }

        if (!hasNotificationsPermissions(context)) {
            Log.d("Notifikacia","Chyba povolenie na notifikaciu");
            return
        }

        NotificationManagerCompat.from(context).notify(1, builder.build())
    }

    fun PERMISSIONS_REQUIRED_NOFIFICATIONS(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else ""
    }

    fun hasNotificationsPermissions(context: Context): Boolean {
        val permission = PERMISSIONS_REQUIRED_NOFIFICATIONS()
        return if (permission.isNotEmpty()) {
            // If permission is not empty, check if it's granted
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        } else {
            // If no permission is required (empty string), return true
            true
        }
    }
}