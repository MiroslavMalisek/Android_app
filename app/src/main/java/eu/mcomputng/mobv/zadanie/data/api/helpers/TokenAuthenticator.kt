package eu.mcomputng.mobv.zadanie.data.api.helpers

import android.content.Context
import android.util.Log
import eu.mcomputng.mobv.zadanie.data.PreferenceData
import eu.mcomputng.mobv.zadanie.data.api.ApiService
import eu.mcomputng.mobv.zadanie.data.api.dtos.RefreshTokenRequest
import eu.mcomputng.mobv.zadanie.data.models.LocalUser
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Route

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: okhttp3.Response): Request? {

        if (response.request.url.toUrl().path.contains("/user/create.php", true)
            || response.request.url.toUrl().path.contains("/user/login.php", true)
            || response.request.url.toUrl().path.contains("/user/refresh.php", true)
            || response.request.url.toUrl().path.contains("/user/reset.php", true)
        ) {
            //here we do not need a authorization token
        } else {
            //if the authorization token was required, but it was rejected from REST API, it is probably outdated
            if (response.code == 401) {
                val userItem = PreferenceData.getInstance().getUser(context)
                Log.d("tokenAuth", userItem.toString())
                userItem?.let { user ->
                    val tokenResponse = ApiService.create(context).refreshTokenBlocking(
                        RefreshTokenRequest(user.refresh)
                    ).execute()

                    Log.d("tokenResponseCode", tokenResponse.code().toString())
                    Log.d("tokenResponseBody", tokenResponse.body().toString())

                    if (tokenResponse.isSuccessful) {
                        tokenResponse.body()?.let {
                            val newUser = LocalUser(
                                user.username,
                                user.email,
                                user.id,
                                it.access,
                                it.refresh,
                            )
                            PreferenceData.getInstance().putUser(context, newUser)
                            return response.request.newBuilder()
                                .header("Authorization", "Bearer ${newUser.access}")
                                .build()
                        }
                    }
                }
                //if there was no success of refresh token we logout user and clean any data
                PreferenceData.getInstance().clearData(context)
                return null
            }
        }
        return null
    }
}