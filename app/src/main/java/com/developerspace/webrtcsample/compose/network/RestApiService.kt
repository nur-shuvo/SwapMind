package com.developerspace.webrtcsample.compose.network

import com.developerspace.webrtcsample.compose.network.annotation.Format
import com.developerspace.webrtcsample.compose.network.annotation.RequestFormat
import com.developerspace.webrtcsample.compose.network.annotation.ResponseFormat
import com.developerspace.webrtcsample.compose.network.requestVO.FcmMessageRequestBody
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Url

interface RestApiService {
    @ResponseFormat(Format.JSON)
    @RequestFormat(Format.JSON)
    @POST
    suspend fun sendFcmMessageByDeviceToken(
        @Url fullUrl: String,
        @HeaderMap headerMap: Map<String, String>,
        @Body body: FcmMessageRequestBody
    )
}