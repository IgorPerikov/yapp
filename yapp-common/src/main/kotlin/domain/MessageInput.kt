package domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class MessageInput @JsonCreator constructor(
    val text: String,
    val from: Int,
    val to: Int,
    @JsonProperty("from_logical_id") val fromLogicalId: Int
)
