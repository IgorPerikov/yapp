package gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GatewayApplication {
    @Bean
    fun routeLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route { r ->
                r.path("/messaging/{segment}")
                    .filters { f -> f.setPath("/{segment}") }
                    .uri("http://localhost:8081")
            }
            .route { r ->
                r.path("/integration/{segment}")
                    .filters { f -> f.setPath("/{segment}") }
                    .uri("http://localhost:8082")
            }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayApplication>()
}
