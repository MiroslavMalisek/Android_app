package eu.mcomputng.mobv.zadanie.data.api.helpers

import android.content.Context
import android.util.Log
import eu.mcomputng.mobv.zadanie.BuildConfig
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptorUpload(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("x-apikey", BuildConfig.MOBV_API_KEY)

        //we add auth token
        val token = PreferenceData.getInstance().getUser(context)?.access
        Log.d("token", token.toString())
        request.addHeader(
            "Authorization",
            "Bearer $token"
        )

        return chain.proceed(request.build())
    }
}