package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.time.Duration
import java.time.Instant
import java.util.*

object JwtUtils {
    private const val ISSUER = "yapp-auth-application"

    private val hmacSecret = System.getenv("HMAC_SECRET") ?: "this_is_default_hmac_secret"
    private val algorithm = Algorithm.HMAC256(hmacSecret)

    val jwtVerifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .build()

    fun createToken(name: String): String {
        return JWT
            .create()
            .withIssuer(ISSUER)
            .withExpiresAt(Date.from(Instant.now().plus(Duration.ofHours(4))))
            .withClaim(YAPP_ID_CLAIM_NAME, name.toInt())
            .sign(algorithm)
    }
}
