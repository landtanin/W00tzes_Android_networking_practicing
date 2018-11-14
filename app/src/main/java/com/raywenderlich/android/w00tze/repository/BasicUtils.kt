package com.raywenderlich.android.w00tze.repository

import com.raywenderlich.android.w00tze.model.Gist
import com.raywenderlich.android.w00tze.model.Repo
import com.raywenderlich.android.w00tze.model.User
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@Throws(IOException::class)
internal fun getUrlAsString(urlAddress: String): String {
  val url = URL(urlAddress)

  val conn = url.openConnection() as HttpsURLConnection

  // set connection properties
  conn.requestMethod = "GET"
  conn.setRequestProperty("Accept", "application/json")

  return try {
    val inputStream = conn.inputStream

    if (conn.responseCode != HttpsURLConnection.HTTP_OK) {
      throw IOException("${conn.responseMessage} for $urlAddress")
    }

    if (inputStream != null) {
      convertStreamToString(inputStream)
    } else {
      "Error retrieving $urlAddress"
    }

  } finally {
    conn.disconnect()
  }
}

@Throws(IOException::class)
private fun convertStreamToString(inputStream: InputStream): String {
  val reader = BufferedReader(InputStreamReader(inputStream))
  val sb = StringBuilder()
  var line: String? = reader.readLine()
  while (line != null) {
    sb.append(line).append("\n")
    line = reader.readLine()
  }
  reader.close()
  return sb.toString()
}