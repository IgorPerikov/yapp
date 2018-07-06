package gateway

import domain.USER_ID_HEADER_NAME
import domain.UserInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class AuthGatewayFilter(
    private val restTemplate: RestTemplate,
    private val authUri: String
) : GatewayFilter {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val authHeader = exchange.request.headers["Authorization"] ?: return rejectRequest(exchange)
        val httpEntity = HttpHeaders()
            .also { it["Authorization"] = authHeader }
            .let { HttpEntity<Nothing>(it) }
        try {
            val userInfo =
                restTemplate.exchange<UserInfo>("$authUri/jwt-token-verifier", HttpMethod.POST, httpEntity).body
            if (userInfo == null) {
                log.error("Empty payload returns from auth service")
                return rejectRequest(exchange)
            }
            val newRequest = exchange.request.mutate().headers { it.set(USER_ID_HEADER_NAME, userInfo.id) }.build()
            val newExchange = exchange.mutate().request(newRequest).build()
            return chain.filter(newExchange)
        } catch (e: RestClientException) {
            log.error("Jwt token verification failed")
            return rejectRequest(exchange)
        }
    }

    private fun rejectRequest(exchange: ServerWebExchange) =
        Mono.empty<Void>().apply { exchange.response.statusCode = HttpStatus.UNAUTHORIZED }
}
