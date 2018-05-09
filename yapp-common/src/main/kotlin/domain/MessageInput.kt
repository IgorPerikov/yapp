package domain

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageInput(
    @JsonProperty("text") val text: String,
    @JsonProperty("to") val to: Int
)
