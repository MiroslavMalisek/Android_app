package eu.mcomputng.mobv.zadanie.data.api.helpers

import android.util.Log
import eu.mcomputng.mobv.zadanie.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class Logger() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("url", request.url.toUrl().toString())
        Log.d("headers", request.headers.toString())
        Log.d("api", BuildConfig.MOBV_API_KEY)
        val requestBody = request.body
        if (requestBody != null) {
            val buffer = okio.Buffer()
            requestBody.writeTo(buffer)
            val bodyString = buffer.readUtf8() // This contains the JSON content
            Log.d("body", bodyString)
        } else {
            Log.d("body", "No body")
        }
        //Log.d("body", request.body.toString())
        val response = chain.proceed(request)
        return response
    }
}