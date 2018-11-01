package com.github.exact7.xtra.model.stream

import com.google.gson.annotations.SerializedName

class StreamsResponse(
    @SerializedName("_total")
    val total: Int,
    val streams: List<Stream>)
