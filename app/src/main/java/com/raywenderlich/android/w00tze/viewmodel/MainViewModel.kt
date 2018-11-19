package com.raywenderlich.android.w00tze.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.net.Uri
import com.raywenderlich.android.w00tze.BuildConfig
import com.raywenderlich.android.w00tze.app.Injection
import com.raywenderlich.android.w00tze.model.AccessToken
import com.raywenderlich.android.w00tze.model.AuthenticationPrefs
import retrofit2.Call
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val authApi = Injection.provideAuthApi()

  val isAuthenticated = AuthenticationPrefs.isAuthenticated()

  fun getAccessToken(uri: Uri, callback: () -> Unit) {

    val accessCode = uri.getQueryParameter("code")

    authApi.getAccessToken(
        BuildConfig.CLIENT_ID,
        BuildConfig.CLIENT_SECRET,
        accessCode)
        .enqueue(object : retrofit2.Callback<AccessToken> {
          override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
            val token = response.body()?.accessToken
            val tokenType = response.body()?.tokenType
            if (token != null && tokenType != null) {
              AuthenticationPrefs.saveAuthToken(token)
              AuthenticationPrefs.saveTokenType(tokenType)
              callback
            }
          }

          override fun onFailure(call: Call<AccessToken>, t: Throwable) {
            println("Error retrieving accessToken")
          }
        })

  }

  fun logout() {
    AuthenticationPrefs.saveAuthToken("")
    AuthenticationPrefs.clearUsername()
  }
}

