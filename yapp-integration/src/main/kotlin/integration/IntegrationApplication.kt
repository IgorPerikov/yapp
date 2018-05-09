@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package integration

import domain.MessageInput
import domain.USER_ID_HEADER_NAME
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.header
import io.ktor.response.respond
import io.ktor.routing.*
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.withContext
import org.apache.http.entity.ContentType
import org.slf4j.event.Level

val messagingUrl = System.getenv("YAPP_MESSAGING_URL") ?: "localhost:8081"
val client = HttpClient(Apache) {
    install(JsonFeature)
}
val pool = newFixedThreadPoolContext(5, "dad-jokes-pool")

fun Application.main() {
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        jackson {

        }
    }
    routing {
        route("/dad-jokes-sender") {
            method(HttpMethod.Post) {
                param("to") {
                    sendDadJoke()
                }
            }
        }
    }
}

fun Route.sendDadJoke() {
    handle {
        withContext(pool) {
            val joke = client.get<String> {
                url("https://icanhazdadjoke.com/")
                header("User-Agent", "https://github.com/IgorPerikov/yapp")
                header("Accept", ContentType.TEXT_PLAIN.mimeType)
            }
            val to = call.request.queryParameters["to"] ?: throw IllegalArgumentException()
            val from = call.request.header(USER_ID_HEADER_NAME) ?: throw IllegalArgumentException()
            client.post<Any> {
                url("http://$messagingUrl/messages")
                header("Content-Type", ContentType.APPLICATION_JSON.mimeType)
                header(USER_ID_HEADER_NAME, from)
                body = MessageInput(
                    joke,
                    to.toInt()
                )
            }
            call.respond(HttpStatusCode.Created)
        }
    }
}
