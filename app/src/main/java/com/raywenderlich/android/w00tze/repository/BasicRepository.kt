package com.raywenderlich.android.w00tze.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.raywenderlich.android.w00tze.app.Constants.fullUrlString
import com.raywenderlich.android.w00tze.model.Gist
import com.raywenderlich.android.w00tze.model.Repo
import com.raywenderlich.android.w00tze.model.User
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

object BasicRepository : Repository {

  private const val TAG = "BasicRepository"

  private const val LOGIN = "w00tze"

  override fun getRepos(): LiveData<List<Repo>> {
    val result = MutableLiveData<List<Repo>>()

    FetchReposAsyncTask { repos ->
      result.value = repos
    }.execute()

    return result

  }

  override fun getGists(): LiveData<List<Gist>> {
    val liveData = MutableLiveData<List<Gist>>()

    FetchGistsAsyncTask({gists ->
      liveData.value = gists
    }).execute()

    return liveData
  }

  override fun getUser(): LiveData<User> {
    val liveData = MutableLiveData<User>()

    val user = User(
        1234L,
        "w00tze",
        "w00tze",
        "W00tzeWootze",
        "https://avatars0.githubusercontent.com/u/36771440?v=4")

    liveData.value = user

    return liveData
  }

  fun fetchRepo(): List<Repo>? {
    try {

      val url = Uri.parse(fullUrlString("/users/$LOGIN/repos")).toString()
      val jsonString = getUrlAsString(url)

      return parseRepo(jsonString)

    } catch (e: IOException) {
      Log.e(TAG, "Error trieving repo ${e.localizedMessage}")
    } catch (e: JSONException) {
      Log.e(TAG, "Error parsing json ${e.localizedMessage}")
    }
    return null
  }

  private fun parseRepo(jsonString: String): List<Repo> {
    val repos = mutableListOf<Repo>()

    val jsonArray = JSONArray(jsonString)
    for (i in 0 until jsonArray.length()) {
      val repoObject = jsonArray.getJSONObject(i)
      repos.add(Repo(repoObject.getString("name")))
    }

    return repos
  }

  private class FetchReposAsyncTask(val callback: ReposCallback) : AsyncTask<ReposCallback, Void, List<Repo>>() {
    override fun doInBackground(vararg params: ReposCallback?): List<Repo>? = fetchRepo()

    override fun onPostExecute(result: List<Repo>?) {
      if (result != null) {
        callback(result)
      }
    }
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

  fun fetchGist(): List<Gist>? {
    try {

      val url = Uri.parse(fullUrlString("/users/$LOGIN/gists")).toString()
      val jsonString = getUrlAsString(url)
      Log.d(TAG, "gists data: $jsonString")
      return parseGist(jsonString)

    } catch (e: IOException) {
      Log.e(TAG, "Error retrieving gists ${e.localizedMessage}")
    } catch (e: JSONException) {
      Log.e(TAG, "Error parsing json ${e.localizedMessage}")
    }
    return null
  }

  private class FetchGistsAsyncTask(val callback: GistsCallback) : AsyncTask<GistsCallback, Void, List<Gist>>() {
    override fun doInBackground(vararg params: GistsCallback?): List<Gist>? = fetchGist()

    override fun onPostExecute(result: List<Gist>?) {
      if (result != null) {
        callback(result)
      }
    }
  }

}