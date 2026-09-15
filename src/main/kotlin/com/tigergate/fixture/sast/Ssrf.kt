package com.tigergate.fixture.sast

import java.net.HttpURLConnection
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request

object Ssrf {
    // CWE-918: server fetches an arbitrary user-supplied URL.
    fun fetch(url: String): String = URL(url).openStream().bufferedReader().readText()

    fun head(url: String): Int =
        (URL(url).openConnection() as HttpURLConnection).apply { requestMethod = "HEAD" }.responseCode

    fun fetchWithOkHttp(url: String): String? =
        OkHttpClient().newCall(Request.Builder().url(url).build()).execute().body()?.string()

    // CWE-918: cloud metadata endpoint reachable through a user-controlled path.
    fun metadata(path: String): String = fetch("http://169.254.169.254/latest/meta-data/" + path)
}
