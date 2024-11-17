package eu.mcomputng.mobv.zadanie.data

import android.content.Context
import android.util.Log
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils.hashPassword
import eu.mcomputng.mobv.zadanie.data.api.ApiService
import eu.mcomputng.mobv.zadanie.data.api.dtos.GeofenceResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UpdateLocationRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UpdateLocationResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserLoginRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserLoginResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserResponse
import eu.mcomputng.mobv.zadanie.data.db.AppRoomDatabase
import eu.mcomputng.mobv.zadanie.data.db.LocalCache
import eu.mcomputng.mobv.zadanie.data.db.entities.UserEntity
import eu.mcomputng.mobv.zadanie.data.models.LoginResultPair
import eu.mcomputng.mobv.zadanie.data.models.RegistrationResultPair
import eu.mcomputng.mobv.zadanie.data.models.LocalUser
import eu.mcomputng.mobv.zadanie.data.models.UpdateLocationPair
import eu.mcomputng.mobv.zadanie.data.models.User
import eu.mcomputng.mobv.zadanie.data.models.UserGetPair
import retrofit2.Response
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService,
    private val cache: LocalCache
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(context: Context): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(ApiService.create(context),
                        LocalCache(AppRoomDatabase.getInstance(context).appDao())
                    ).also { INSTANCE = it }
            }
    }

    suspend fun apiRegisterUser(context: Context, username: String, email: String, password: String) : RegistrationResultPair{
        if (email.isEmpty()){
            return RegistrationResultPair(context.getString(R.string.registerErrorEmptyEmail), null)
        }
        if (username.isEmpty()){
            return RegistrationResultPair(context.getString(R.string.registerErrorEmptyUsername), null)
        }
        if (password.isEmpty()){
            return RegistrationResultPair(context.getString(R.string.registerErrorEmptyPassword), null)
        }
        val hashedPass = hashPassword(password)
        Log.d("register_credits", hashedPass)
        try {
            val response: Response<UserRegistrationResponse> = service.registerUser(UserRegistrationRequest(
                username, email, hashedPass))
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    if(jsonResponse.uid == "-1"){
                        return RegistrationResultPair(context.getString(R.string.registerErrorUsernameExists), null)
                    }
                    if (jsonResponse.uid == "-2"){
                        return RegistrationResultPair(context.getString(R.string.registerErrorEmailExists), null)
                    }
                    return RegistrationResultPair(context.getString(R.string.registerSuccess), LocalUser(username, email, jsonResponse.uid, jsonResponse.access, jsonResponse.refresh))
                }
            }
            return RegistrationResultPair(context.getString(R.string.registerErrorUserFailed), null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return RegistrationResultPair(context.getString(R.string.registerErrorNetwork), null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return RegistrationResultPair(context.getString(R.string.registerErrorUnexpected), null)
    }


    suspend fun apiLoginUser(context: Context, username: String, password: String): LoginResultPair{
        if (username.isEmpty()){
            return LoginResultPair(context.getString(R.string.loginErrorEmptyUsername), null)
        }
        if (password.isEmpty()){
            return LoginResultPair(context.getString(R.string.loginErrorEmptyPassword), null)
        }
        val hashedPass = hashPassword(password)
        Log.d("login_credits", username)
        Log.d("login_credits", hashedPass)
        try {
            val response: Response<UserLoginResponse> = service.loginUser(UserLoginRequest(
                username, hashedPass
            ))
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    if(jsonResponse.uid == "-1"){
                        return LoginResultPair(context.getString(R.string.loginErrorLoginFailed), null)
                    }
                    return LoginResultPair(context.getString(R.string.loginSuccess), LocalUser(username, "", jsonResponse.uid, jsonResponse.access, jsonResponse.refresh))
                }
            }
            return LoginResultPair(context.getString(R.string.loginErrorUserFailed), null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return LoginResultPair(context.getString(R.string.loginErrorNetwork), null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return LoginResultPair(context.getString(R.string.loginErrorUnexpected), null)
    }

    suspend fun apiGetUser(context: Context, id: String): UserGetPair{
        try {
            val response: Response<UserResponse> = service.getUser(id)
            Log.d("getUser code", response.code().toString())
            Log.d("getUser body", response.body().toString())
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    return UserGetPair("",
                        User(jsonResponse.id, jsonResponse.name,jsonResponse.photo))
                }
            }
            return UserGetPair(context.getString(R.string.userErrorGetUserFailed), null)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return UserGetPair(context.getString(R.string.userErrorNetwork), null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return UserGetPair(context.getString(R.string.userErrorUnexpected), null)
    }

    suspend fun apiGetGeofenceUsers(context: Context): String{
        try {
            val response: Response<GeofenceResponse> = service.getGeofenceList()
            Log.d("getUser code", response.code().toString())
            Log.d("getUser body", response.body().toString())
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    val users = jsonResponse.list.map {
                        UserEntity(
                            it.uid, it.name, it.updated,
                            jsonResponse.me.lat, jsonResponse.me.lon, it.radius,
                            it.photo
                        )
                    }
                    cache.insertUserItems(users)
                    return ""
                }
            }
            return context.getString(R.string.userErrorGetUserFailed)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return context.getString(R.string.userErrorNetwork)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return context.getString(R.string.userErrorUnexpected)
    }

    fun getUsers() = cache.getUsers()

    suspend fun apiUpdateLocation(context: Context, lat: Double, lon: Double, radius: Double): UpdateLocationPair{
        try {
            val response: Response<UpdateLocationResponse> = service.updateLocation(
                UpdateLocationRequest(lat, lon, radius)
            )
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    if (jsonResponse.success == "true"){
                        return UpdateLocationPair("", true)
                    }else{
                        return UpdateLocationPair(context.getString(R.string.locationErrorUpdateFailed), false)
                    }
                }
            }
            return UpdateLocationPair(context.getString(R.string.locationErrorUpdateFailed), false)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return UpdateLocationPair(context.getString(R.string.locationErrorUpdateNetwork), false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return UpdateLocationPair(context.getString(R.string.locationErrorUpdateUnexpected), false)
    }

    suspend fun apiDeleteLocation(context: Context): UpdateLocationPair{
        try {
            val response: Response<UpdateLocationResponse> = service.deleteLocation()
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    if (jsonResponse.success == "true"){
                        return UpdateLocationPair("", true)
                    }else{
                        return UpdateLocationPair(context.getString(R.string.locationErrorDeleteFailed), false)
                    }
                }
            }
            return UpdateLocationPair(context.getString(R.string.locationErrorDeleteFailed), false)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return UpdateLocationPair(context.getString(R.string.locationErrorDeleteNetwork), false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return UpdateLocationPair(context.getString(R.string.locationErrorDeleteUnexpected), false)
    }

}