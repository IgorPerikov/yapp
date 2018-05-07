package gateway

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
    private val messagingUri = "http://localhost:8081"
    private val integrationUri = "http://localhost:8082"
    private val authUri = "http://localhost:8083"

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
