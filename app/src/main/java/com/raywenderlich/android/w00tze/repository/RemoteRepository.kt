package com.raywenderlich.android.w00tze.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.raywenderlich.android.w00tze.app.Injection
import com.raywenderlich.android.w00tze.model.Gist
import com.raywenderlich.android.w00tze.model.Repo
import com.raywenderlich.android.w00tze.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RemoteRepository : Repository {

  private const val TAG = "RemoteRepository"

  private const val LOGIN = "w00tze"

  private val api = Injection.provideGitHubApi()

  override fun getRepos(): LiveData<List<Repo>> {
    val liveData = MutableLiveData<List<Repo>>()

    api.getRepos(LOGIN).enqueue(object: retrofit2.Callback<List<Repo>> {
      override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
        liveData.value = response.body()
      }

      override fun onFailure(call: Call<List<Repo>>, t: Throwable) {

      }
    })

    return liveData

  }

  override fun getGists(): LiveData<List<Gist>> {
    val liveData = MutableLiveData<List<Gist>>()

    api.getGists(LOGIN).enqueue(object: Callback<List<Gist>>{
      override fun onResponse(call: Call<List<Gist>>, response: Response<List<Gist>>) {
        if (response != null) {
          liveData.value = response.body()
        }
      }

      override fun onFailure(call: Call<List<Gist>>, t: Throwable) {

      }
    })

    return liveData
  }

  override fun getUser(): LiveData<User> {
    val liveData = MutableLiveData<User>()

    api.getUser(LOGIN).enqueue(object: Callback<User> {
      override fun onResponse(call: Call<User>, response: Response<User>) {
        liveData.value = response.body()
      }

      override fun onFailure(call: Call<User>, t: Throwable) {

      }
    })

    return liveData
  }

}