package eu.mcomputng.mobv.zadanie.data.models

import com.google.gson.Gson
import java.io.IOException

data class LocalUser(
    val username: String,
    val email: String,
    val id: String,
    val access: String,
    val refresh: String
    ){
    fun toJson(): String? {
        return try {
            Gson().toJson(this)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    companion object {
        fun fromJson(string: String): LocalUser? {
            return try {
                Gson().fromJson(string, LocalUser::class.java)
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }
        }
    }
}
