package domain

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageInput(
    val text: String,
    val to: Int,
    @JsonProperty("from_logical_id") val fromLogicalId: Int
)
