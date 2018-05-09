package messaging.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class MessageOutput @JsonCreator constructor(
    @JsonProperty("text") val text: String,
    @JsonProperty("from") val from: Int,
    @JsonProperty("to") val to: Int,
    @JsonProperty("date") val date: Instant
)
