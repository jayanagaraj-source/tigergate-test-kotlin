package com.tigergate.fixture.sast

import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.OkHttpClient

object InsecureTls {
    // CWE-295: trust manager that accepts every certificate.
    val trustAll: X509TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    }

    // CWE-297: hostname verification disabled.
    val allowAllHosts = HostnameVerifier { _, _ -> true }

    fun disableGlobally() {
        val ctx = SSLContext.getInstance("SSL") // CWE-326 legacy protocol string
        ctx.init(null, arrayOf<TrustManager>(trustAll), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier(allowAllHosts)
    }

    fun insecureClient(): OkHttpClient {
        val ctx = SSLContext.getInstance("TLS").apply { init(null, arrayOf<TrustManager>(trustAll), null) }
        return OkHttpClient.Builder()
            .sslSocketFactory(ctx.socketFactory, trustAll)
            .hostnameVerifier(allowAllHosts)
            .build()
    }
}
