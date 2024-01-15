package com.developerspace.webrtcsample.network.responseVO

import com.google.gson.annotations.SerializedName

data class DogResponse (
    @SerializedName("message")
    var message : String? = null,

    @SerializedName("status")
    var status  : String? = null
)