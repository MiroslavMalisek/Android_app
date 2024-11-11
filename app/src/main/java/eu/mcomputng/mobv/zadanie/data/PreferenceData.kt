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

    }

}