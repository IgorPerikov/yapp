@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package auth

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.Level

private const val JWT_AUTH = "jwt"
private const val BASIC_AUTH = "basic"

const val YAPP_ID_CLAIM_NAME = "yapp-user-id"

fun Application.main() {
    DbUtils.init()
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        jackson {

        }
    }
    install(Authentication) {
        basic(BASIC_AUTH) {
            validate { BasicAuthUtils.authenticateByPassword(it) }
        }
        jwt(JWT_AUTH) {
            verifier(JwtUtils.jwtVerifier)
            validate { credential -> JWTPrincipal(credential.payload) }
        }
    }
    routing {
        post("/registration") {
            val user = call.receive<UserPasswordCredential>()
            val name = user.name.toLowerCase()
            val password = user.password
            if (SecLists.isWeak(password)) {
                call.respond(HttpStatusCode.BadRequest, Error("Weak password from top1000 was used"))
            } else if (BasicAuthUtils.isUsernameAlreadyTaken(name)) {
                call.respond(HttpStatusCode.Conflict)
            } else {
                transaction {
                    UserEntity.new {
                        username = name
                        this@new.password = BasicAuthUtils.hashAndEncodePassword(password)
                    }
                }
                call.respond(HttpStatusCode.Created)
            }
        }
        authenticate(BASIC_AUTH) {
            post("/authentication") {
                val userIdPrincipal = call.authentication.principal as UserIdPrincipal
                call.respond(JwtToken(JwtUtils.createToken(userIdPrincipal.name)))
            }
        }
        authenticate(JWT_AUTH) {
            post("/jwt-token-verifier") {
                val jwtPrincipal = call.authentication.principal as JWTPrincipal
                val userId = jwtPrincipal.payload.getClaim(YAPP_ID_CLAIM_NAME).asInt().toString()
                call.respond(UserInfo(userId))
            }
        }
    }
}

data class JwtToken(val token: String)
data class UserInfo(val id: String)
data class Error(val message: String)
