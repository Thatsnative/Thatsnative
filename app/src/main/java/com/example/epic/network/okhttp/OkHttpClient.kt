package com.example.epic.network.okhttp

import android.annotation.SuppressLint
import com.example.epic.BuildConfig
import com.example.epic.common.inject
import com.example.epic.network.interseptors.HeaderInterceptor
import com.example.epic.network.interseptors.TokenAuthenticator
import com.example.epic.network.interseptors.TokenInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

private const val CONNECT_TIMEOUT_IN_MILLIS = 30000L
private const val READ_TIMEOUT_IN_MILLIS = 30000L
private const val WRITE_TIMEOUT_IN_MILLIS = 30000L
private const val HTTP = "http://"

fun createOkHttpClient(): OkHttpClient {
    val tokenInterceptor by inject<TokenInterceptor>()
    val tokenAuthenticator by inject<TokenAuthenticator>()
    return OkHttpClient.Builder().run {
        addInterceptor(tokenInterceptor)
        addInterceptor(HeaderInterceptor())
        authenticator(tokenAuthenticator)
        if (BuildConfig.DEBUG) {
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        disableSSLIfNeed()
        connectTimeout(CONNECT_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS)
        readTimeout(READ_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS)
        writeTimeout(WRITE_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS)
        build()
    }
}


fun OkHttpClient.Builder.disableSSLIfNeed(): OkHttpClient.Builder {
    if(!BuildConfig.BASE_URL.startsWith(HTTP)) return this

    val trustAllCerts = arrayOf<TrustManager>(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    val sslContext = SSLContext.getInstance("SSL")
    sslContext.init(null, trustAllCerts, SecureRandom())
    val sslSocketFactory = sslContext.socketFactory
    return OkHttpClient.Builder()
        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        .hostnameVerifier { _, _ -> true }
}