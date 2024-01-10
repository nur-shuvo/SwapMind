package com.developerspace.webrtcsample.network

import com.developerspace.webrtcsample.network.annotation.Format
import com.developerspace.webrtcsample.network.annotation.ResponseFormat
import com.developerspace.webrtcsample.network.responseVO.DogResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface RestApiService {
    // Just for an example for demo.
    // val result = restApiService.getRandomDogImageUrl("https://dog.ceo/api/breeds/image/random")
    @ResponseFormat(Format.JSON)
    @GET
    suspend fun getRandomDogImageUrl(@Url fullUrl: String): DogResponse
}