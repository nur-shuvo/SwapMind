package com.developerspace.webrtcsample.network.requestVO

import com.google.gson.annotations.SerializedName

data class ExampleRequest(
    @SerializedName("value")
    val value: String
)