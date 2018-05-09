package gateway

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class GatewayApplication {
    @Value("\${messaging.uri}")
    private lateinit var messagingUri: String

    @Value("\${integration.uri}")
    private lateinit var integrationUri: String

    @Value("\${auth.uri}")
    private lateinit var authUri: String

    @Bean
    fun routeLocator(builder: RouteLocatorBuilder, authGatewayFilter: AuthGatewayFilter): RouteLocator {
        val segmentPathFilter = { f: GatewayFilterSpec ->
            f.setPath("/{segment}")
        }
        val segmentPathFilterWithAuth = { f: GatewayFilterSpec ->
            f.filter(authGatewayFilter, 0)
            segmentPathFilter.invoke(f)
        }
        return builder.routes()
            .route { r -> r.path("/messaging/{segment}").filters(segmentPathFilterWithAuth).uri(messagingUri) }
            .route { r -> r.path("/integration/{segment}").filters(segmentPathFilterWithAuth).uri(integrationUri) }
            .route { r -> r.path("/auth/{segment}").filters(segmentPathFilter).uri(authUri) }
            .build()
    }

    @Bean
    fun restTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        factory.setConnectTimeout(200)
        factory.setReadTimeout(500)
        return RestTemplate(factory)
    }

    @Bean
    fun authGatewayFilter(restTemplate: RestTemplate): AuthGatewayFilter {
        return AuthGatewayFilter(restTemplate, authUri)
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>()
}
