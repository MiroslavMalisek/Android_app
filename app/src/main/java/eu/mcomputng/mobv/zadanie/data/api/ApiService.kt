package eu.mcomputng.mobv.zadanie.data.api

import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationRequest
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserRegistrationResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("x-apikey: c95332ee022df8c953ce470261efc695ecf3e784")
    @POST("user/create.php")
    suspend fun registerUser(@Body userInfo: UserRegistrationRequest): Response<UserRegistrationResponse>

    companion object{
        private const val BASE_URL = "https://zadanie.mpage.sk/"
        fun create(): ApiService {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}