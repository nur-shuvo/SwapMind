package com.developerspace.webrtcsample.compose.network

import com.developerspace.webrtcsample.compose.network.annotation.Format
import com.developerspace.webrtcsample.compose.network.annotation.RequestFormat
import com.developerspace.webrtcsample.compose.network.annotation.ResponseFormat
import com.developerspace.webrtcsample.compose.network.requestVO.FcmMessageRequestBody
import com.developerspace.webrtcsample.compose.network.responseVO.DogResponse
import com.squareup.okhttp.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Url

interface RestApiService {
    // Just for an example for demo.
    // val result = restApiService.getRandomDogImageUrl("https://dog.ceo/api/breeds/image/random")
    @ResponseFormat(Format.JSON)
    @GET
    suspend fun getRandomDogImageUrl(@Url fullUrl: String): DogResponse

    @ResponseFormat(Format.JSON)
    @RequestFormat(Format.JSON)
    @POST
    suspend fun sendFcmMessageByDeviceToken(
        @Url fullUrl: String,
        @HeaderMap headerMap: Map<String, String>,
        @Body body: FcmMessageRequestBody
    )
}