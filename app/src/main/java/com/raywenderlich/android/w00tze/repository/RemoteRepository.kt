package com.raywenderlich.android.w00tze.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.raywenderlich.android.w00tze.app.Injection
import com.raywenderlich.android.w00tze.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RemoteRepository : Repository {

  private const val TAG = "RemoteRepository"

  private val LOGIN = AuthenticationPrefs.getUsername()

  private val api = Injection.provideGitHubApi()

  override fun getRepos(): LiveData<Either<List<Repo>>> {
    val liveData = MutableLiveData<Either<List<Repo>>>()

    api.getRepos(LOGIN).enqueue(object : retrofit2.Callback<List<Repo>> {
      override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
        if (response != null && response.isSuccessful) {
          liveData.value = Either.success(response.body())
        } else {
          liveData.value = Either.error(ApiError.REPOS, null)
        }
      }

      override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
        liveData.value = Either.error(ApiError.REPOS, null)
      }
    })

    return liveData

  }

  override fun getGists(): LiveData<Either<List<Gist>>> {
    val liveData = MutableLiveData<Either<List<Gist>>>()

    api.getGists(LOGIN).enqueue(object : Callback<List<Gist>> {
      override fun onResponse(call: Call<List<Gist>>, response: Response<List<Gist>>) {
        if (response != null && response.isSuccessful) {
          liveData.value = Either.success(response.body())
        } else {
          liveData.value = Either.error(ApiError.GISTS, null)
        }
      }

      override fun onFailure(call: Call<List<Gist>>, t: Throwable) {
        liveData.value = Either.error(ApiError.GISTS, null)
      }
    })

    return liveData
  }

  override fun getUser(): LiveData<Either<User>> {
    val liveData = MutableLiveData<Either<User>>()

    api.getUser(LOGIN).enqueue(object : Callback<User> {
      override fun onResponse(call: Call<User>, response: Response<User>) {
        if (response != null && response.isSuccessful) {
          liveData.value = Either.success(response.body())
        } else {
          liveData.value = Either.error(ApiError.USER, null)
        }
      }

      override fun onFailure(call: Call<User>, t: Throwable) {
        liveData.value = Either.error(ApiError.USER, null)
      }
    })

    return liveData
  }

  override fun postGist(request: GistRequest): LiveData<Either<Gist>> {
    val liveData = MutableLiveData<Either<Gist>>()

    api.postGist(request).enqueue(object : Callback<Gist> {
      override fun onResponse(call: Call<Gist>, response: Response<Gist>?) {
        if (response != null && response.isSuccessful) {
          liveData.value = Either.success(response.body())
        } else {
          liveData.value = Either.error(ApiError.POST_GIST, null)
        }
      }

      override fun onFailure(call: Call<Gist>, t: Throwable) {
        liveData.value = Either.error(ApiError.POST_GIST, null)
      }
    })

    return liveData
  }
}