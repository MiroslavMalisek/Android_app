package eu.mcomputng.mobv.zadanie.data

import android.content.Context
import android.content.SharedPreferences
import eu.mcomputng.mobv.zadanie.data.models.LocalUser

class PreferenceData private constructor() {

    private fun getSharedPreferences(context: Context?): SharedPreferences? {
        return context?.getSharedPreferences(
            shpKey, Context.MODE_PRIVATE
        )
    }

    fun clearData(context: Context?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun putUser(context: Context?, user: LocalUser?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        user?.toJson()?.let {
            editor.putString(userKey, it)
        } ?: editor.remove(userKey)

        editor.apply()
    }

    fun getUser(context: Context?): LocalUser? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val json = sharedPref.getString(userKey, null) ?: return null

        return LocalUser.fromJson(json)
    }

    fun putSharing(context: Context?, sharing: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(sharingKey, sharing)
        editor.apply()
    }

    fun getSharing(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val sharing = sharedPref.getBoolean(sharingKey, false)

        return sharing
    }

    fun putLocationAcquired(context: Context?, locationAcquired: Boolean) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putBoolean(locationAcquiredKey, locationAcquired)
        editor.apply()
    }

    fun getLocationAcquired(context: Context?): Boolean {
        val sharedPref = getSharedPreferences(context) ?: return false
        val locationAcquired = sharedPref.getBoolean(locationAcquiredKey, false)

        return locationAcquired
    }

    fun putResetPasswordUserEmail(context: Context?, email: String?) {
        val sharedPref = getSharedPreferences(context) ?: return
        val editor = sharedPref.edit()
        editor.putString(resetPasswordUserEmail, email)
        editor.apply()
    }

    fun getResetPasswordUserEmail(context: Context?): String? {
        val sharedPref = getSharedPreferences(context) ?: return null
        val email = sharedPref.getString(resetPasswordUserEmail, null) ?: return null

        return email
    }


    companion object {
        @Volatile
        private var INSTANCE: PreferenceData? = null

        private val lock = Any()

        fun getInstance(): PreferenceData =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: PreferenceData().also { INSTANCE = it }
            }

        private const val shpKey = "eu.mcomputing.mobv.zadanie"
        private const val userKey = "userKey"
        private const val sharingKey = "sharingKey"
        private const val locationAcquiredKey = "locationAcquiredKey"
        private const val resetPasswordUserEmail = "resetUserEmail"

    }

}