package com.raywenderlich.android.w00tze.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.raywenderlich.android.w00tze.app.Constants.fullUrlString
import com.raywenderlich.android.w00tze.app.Injection
import com.raywenderlich.android.w00tze.app.isNullOrBlanckOrNullString
import com.raywenderlich.android.w00tze.model.Gist
import com.raywenderlich.android.w00tze.model.Repo
import com.raywenderlich.android.w00tze.model.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

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

    FetchAsyncTask("/users/$LOGIN/gists", ::parseGist) { gists ->
      liveData.value = gists
    }.execute()

    return liveData
  }

  override fun getUser(): LiveData<User> {
    val liveData = MutableLiveData<User>()

    FetchAsyncTask("/users/$LOGIN", ::parseUser) { user ->
      liveData.value = user
    }.execute()

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

  fun <T> fetch(uriPath: String, parser: (String) -> T): T? {

    try {

      val url = Uri.parse(fullUrlString(uriPath)).toString()
      val jsonString = getUrlAsString(url)
      return parser(jsonString)

    } catch (e: IOException) {
      Log.e(TAG, "Error trieving path: $uriPath ::: ${e.localizedMessage}")
    } catch (e: JSONException) {
      Log.e(TAG, "Error trieving path: $uriPath ::: ${e.localizedMessage}")
    }
    return null
  }


  private class FetchAsyncTask<T>(val path: String,
                                  val parser: (String) -> T,
                                  val callback: (T) -> Unit)
    : AsyncTask<(T) -> Unit, Void, T>() {
    override fun doInBackground(vararg params: ((T) -> Unit)?): T? {
      return fetch(path, parser)
    }

    override fun onPostExecute(result: T?) {
      if (result != null) {
        callback(result)
      }
    }

  }

}