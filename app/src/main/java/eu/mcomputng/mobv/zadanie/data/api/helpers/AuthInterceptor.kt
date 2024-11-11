package eu.mcomputng.mobv.zadanie.data.api.helpers

import android.content.Context
import android.util.Log
import eu.mcomputng.mobv.zadanie.BuildConfig
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("x-apikey", BuildConfig.MOBV_API_KEY)


        if (chain.request().url.toUrl().path.contains("/user/create.php", true)
            || chain.request().url.toUrl().path.contains("/user/login.php", true)
            || chain.request().url.toUrl().path.contains("/user/reset.php", true)
        ) {
            //here we do not need a authorization token
        } else if (chain.request().url.toUrl().path.contains("/user/refresh.php", true)) {
            //when refreshing token we need to add our user id
            PreferenceData.getInstance().getUser(context)?.id?.let {
                request.header(
                    "x-user",
                    it
                )
            }
        } else {
            //we add auth token
            val token = PreferenceData.getInstance().getUser(context)?.access
            Log.d("token", token.toString())
            request.addHeader(
                "Authorization",
                "Bearer $token"
            )
        }

        return chain.proceed(request.build())
    }
}