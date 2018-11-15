package com.raywenderlich.android.w00tze.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.raywenderlich.android.w00tze.app.Injection
import com.raywenderlich.android.w00tze.app.isNullOrBlanckOrNullString
import com.raywenderlich.android.w00tze.model.Gist
import com.raywenderlich.android.w00tze.model.Repo
import com.raywenderlich.android.w00tze.model.User
import org.json.JSONArray
import org.json.JSONObject
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
        liveData.value = emptyList()
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
        liveData.value = emptyList()
      }

      override fun onFailure(call: Call<List<Gist>>, t: Throwable) {

      }
    })

    return liveData
  }

  override fun getUser(): LiveData<User> {
    val liveData = MutableLiveData<User>()

//    FetchAsyncTask("/users/$LOGIN", ::parseUser) { user ->
//      liveData.value = user
//    }.execute()

    api.getUser(LOGIN).enqueue(object: Callback<User> {
      override fun onResponse(call: Call<User>, response: Response<User>) {
//        liveData.value = Unit()
      }

      override fun onFailure(call: Call<User>, t: Throwable) {

      }
    })

    return liveData
  }

  fun parseRepo(jsonString: String): List<Repo> {
    val repos = mutableListOf<Repo>()

    val jsonArray = JSONArray(jsonString)
    for (i in 0 until jsonArray.length()) {
      val repoObject = jsonArray.getJSONObject(i)
      repos.add(Repo(repoObject.getString("name")))
    }

    return repos
  }

  private fun parseGist(jsonString: String): List<Gist> {
    val gists = mutableListOf<Gist>()

    val jsonArray = JSONArray(jsonString)
    for (i in 0 until jsonArray.length()) {
      val gistObject = jsonArray.getJSONObject(i)
      val createdAt = gistObject.getString("created_at")
      val des = gistObject.getString("description")
      gists.add(Gist(createdAt, des))
    }

    return gists
  }

  private fun parseUser(jsonString: String): User {

    val user = JSONObject(jsonString)

    val id = user.getLong("id")
    val name = if (user.getString("name").isNullOrBlanckOrNullString()) "" else user.getString("name")
    val login = user.getString("login")
    val company = if (user.getString("company").isNullOrBlanckOrNullString()) "" else user.getString("company")
    val avatarUrl = user.getString("avatar_url")

    return User(
        id, name, login, company, avatarUrl
    )

  }
}