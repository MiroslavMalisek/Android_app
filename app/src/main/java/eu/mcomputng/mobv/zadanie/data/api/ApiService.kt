package eu.mcomputng.mobv.zadanie.data.api

import android.content.Context
import eu.mcomputng.mobv.zadanie.data.api.dtos.ChangeUserPasswordRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.ChangeUserPasswordResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.GeofenceResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.RefreshTokenRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.RefreshTokenResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UpdateLocationRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UpdateLocationResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserLoginRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserLoginResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserResetRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserResetResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserResponse
import eu.mcomputng.mobv.zadanie.data.api.helpers.AuthInterceptor
import eu.mcomputng.mobv.zadanie.data.api.helpers.Logger
import eu.mcomputng.mobv.zadanie.data.api.helpers.TokenAuthenticator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<UserRegistrationResponse>

    @POST("user/login.php")
    suspend fun loginUser(@Body userInfo: UserLoginRequest): Response<UserLoginResponse>

    @GET("user/get.php")
    suspend fun getUser(
        @Query("id") id: String
    ): Response<UserResponse>

    @GET("geofence/list.php")
    suspend fun getGeofenceList(): Response<GeofenceResponse>

    @POST("geofence/update.php")
    suspend fun updateLocation(@Body location: UpdateLocationRequest): Response<UpdateLocationResponse>

    @DELETE("geofence/update.php")
    suspend fun deleteLocation(): Response<UpdateLocationResponse>

    @POST("user/reset.php")
    suspend fun resetUser(@Body userInfo: UserResetRequest): Response<UserResetResponse>

    @POST("user/password.php")
    suspend fun changeUserPassword(@Body userInfo: ChangeUserPasswordRequest): Response<ChangeUserPasswordResponse>

    @POST("user/refresh.php")
    suspend fun refreshToken(
        @Body refreshInfo: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @POST("user/refresh.php")
    fun refreshTokenBlocking(
        @Body refreshInfo: RefreshTokenRequest
    ): Call<RefreshTokenResponse>



    companion object{
        private const val BASE_URL = "https://zadanie.mpage.sk/"
        fun create(context: Context): ApiService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .authenticator(TokenAuthenticator(context))
                .addInterceptor(Logger())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}