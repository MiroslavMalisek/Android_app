package eu.mcomputng.mobv.zadanie.data

import android.content.Context
import android.util.Log
import eu.mcomputng.mobv.zadanie.R
import eu.mcomputng.mobv.zadanie.data.api.ApiService
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationResponse
import eu.mcomputng.mobv.zadanie.data.models.RegistrationResultPair
import eu.mcomputng.mobv.zadanie.data.models.User
import retrofit2.Response
import java.io.IOException

class DataRepository private constructor(
    private val service: ApiService
) {
    companion object {
        const val TAG = "DataRepository"

        @Volatile
        private var INSTANCE: DataRepository? = null
        private val lock = Any()

        fun getInstance(): DataRepository =
            INSTANCE ?: synchronized(lock) {
                INSTANCE
                    ?: DataRepository(ApiService.create()).also { INSTANCE = it }
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
        try {
            val response: Response<UserRegistrationResponse> = service.registerUser(UserRegistrationRequest(username, email, password))
            if (response.isSuccessful) {
                response.body()?.let { jsonResponse ->
                    if(jsonResponse.uid == "-1"){
                        return RegistrationResultPair(context.getString(R.string.registerErrorUsernameExists), null)
                    }
                    if (jsonResponse.uid == "-2"){
                        return RegistrationResultPair(context.getString(R.string.registerErrorEmailExists), null)
                    }
                    return RegistrationResultPair(context.getString(R.string.registerSuccess), User(username, email, jsonResponse.uid, jsonResponse.access, jsonResponse.refresh))
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

}