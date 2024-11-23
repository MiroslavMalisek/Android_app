package eu.mcomputng.mobv.zadanie.data

import android.content.Context
import android.util.Log
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.Utils.hashPassword
import eu.mcomputng.mobv.zadanie.data.api.ApiService
import eu.mcomputng.mobv.zadanie.data.api.dtos.ChangePasswordRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.ChangePasswordResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.GeofenceResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.ResetPasswordRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.ResetPasswordResponse
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
import eu.mcomputng.mobv.zadanie.data.models.ChangePasswordResultPair
import eu.mcomputng.mobv.zadanie.data.models.LoginResultPair
import eu.mcomputng.mobv.zadanie.data.models.RegistrationResultPair
import eu.mcomputng.mobv.zadanie.data.models.LocalUser
import eu.mcomputng.mobv.zadanie.data.models.ResetPasswordResultPair
import eu.mcomputng.mobv.zadanie.data.models.UpdateLocationPair
import eu.mcomputng.mobv.zadanie.data.models.User
import eu.mcomputng.mobv.zadanie.data.models.UserGetPair
import okhttp3.Call
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


    suspend fun apiLoginUser(doHashPassword: Boolean = true, context: Context, username: String, password: String): LoginResultPair{
        if (username.isEmpty()){
            return LoginResultPair(context.getString(R.string.loginErrorEmptyUsername), null)
        }
        if (password.isEmpty()){
            return LoginResultPair(context.getString(R.string.loginErrorEmptyPassword), null)
        }
        val processedPassword: String = if (doHashPassword){
            hashPassword(password)
        }else{
            password
        }

        Log.d("login_credits", username)
        Log.d("login_credits", processedPassword)
        try {
            val response: Response<UserLoginResponse> = service.loginUser(UserLoginRequest(
                username, processedPassword
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

    suspend fun apiResetPassword(context: Context, email: String): ResetPasswordResultPair{
        if (email.isEmpty()){
            return ResetPasswordResultPair(context.getString(R.string.resetPasswordEmailEmpty), false)
        }
        try {
            val response: Response<ResetPasswordResponse> = service.resetPassword(
                ResetPasswordRequest(email)
            )
            if (response.isSuccessful) {
                return ResetPasswordResultPair(context.getString(R.string.resetPasswordSuccess), true)
            }else{
                response.body()?.let { jsonResponse ->
                    if (jsonResponse.message.isNotEmpty()){
                        return ResetPasswordResultPair(jsonResponse.message, false)
                    }else{
                        return ResetPasswordResultPair(context.getString(R.string.resetPasswordFailed), false)
                    }
                }
            }
        }catch (ex: IOException) {
            ex.printStackTrace()
            return ResetPasswordResultPair(context.getString(R.string.resetPasswordErrorNetwork), false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ResetPasswordResultPair(context.getString(R.string.resetPasswordErrorUnexpected), false)
    }

    suspend fun apiChangePassword(doHashPassword: Boolean = true, context: Context, actualPassword: String, newPassword: String): ChangePasswordResultPair{
        if (actualPassword.isEmpty()){
            return ChangePasswordResultPair(context.getString(R.string.changePasswordActualPasswordEmpty), false)
        }
        if (newPassword.isEmpty()){
            return ChangePasswordResultPair(context.getString(R.string.changePasswordNewPasswordEmpty), false)
        }

        val processedActualPassword: String = if (doHashPassword){
            hashPassword(actualPassword)
        }else{
            actualPassword
        }

        try {
            val response: Response<ChangePasswordResponse> = service.changePassword(
                ChangePasswordRequest(old_password = processedActualPassword, new_password = hashPassword(newPassword))
            )
            Log.d("response code", response.code().toString())
            Log.d("response body", response.body().toString())
            if (response.isSuccessful) {
                return ChangePasswordResultPair(context.getString(R.string.changePasswordSuccess), true)
            }
            return ChangePasswordResultPair(context.getString(R.string.changePasswordFailed), false)
        }catch (ex: IOException) {
            ex.printStackTrace()
            return ChangePasswordResultPair(context.getString(R.string.changePasswordErrorNetwork), false)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ChangePasswordResultPair(context.getString(R.string.changePasswordErrorUnexpected), false)
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
            Log.d("getGeofence code", response.code().toString())
            Log.d("getGeofence body", response.body().toString())
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    val users = jsonResponse.list
                        //.filter { it.uid != jsonResponse.me.uid }
                        .map {
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

    suspend fun deleteUsers() = cache.deleteUserItems()

}