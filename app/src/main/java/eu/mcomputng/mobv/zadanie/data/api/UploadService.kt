package eu.mcomputng.mobv.zadanie.data.api

import android.content.Context
import eu.mcomputng.mobv.zadanie.data.api.dtos.UpdateLocationResponse
import eu.mcomputng.mobv.zadanie.data.api.dtos.UserResponse
import eu.mcomputng.mobv.zadanie.data.api.helpers.AuthInterceptor
import eu.mcomputng.mobv.zadanie.data.api.helpers.AuthInterceptorUpload
import eu.mcomputng.mobv.zadanie.data.api.helpers.Logger
import eu.mcomputng.mobv.zadanie.data.api.helpers.TokenAuthenticator
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadService {

    @Multipart
    @POST("photo.php")
    suspend fun uploadProfilePhoto(
        @Part image: MultipartBody.Part
    ): Response<UserResponse>

    @DELETE("photo.php")
    suspend fun deleteProfilePhoto(): Response<UserResponse>

    companion object{
        private const val BASE_URL = "https://upload.mcomputing.eu/"
        fun create(context: Context): UploadService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptorUpload(context))
                .authenticator(TokenAuthenticator(context))
                .addInterceptor(Logger())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(UploadService::class.java)
        }
    }
}