package com.developerspace.webrtcsample.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HttpHeaderInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        chain.request().apply {
            val newRequest = newBuilder().apply {
                //TODO add headers here
            }.build()
            return chain.proceed(newRequest)
        }
    }
}
