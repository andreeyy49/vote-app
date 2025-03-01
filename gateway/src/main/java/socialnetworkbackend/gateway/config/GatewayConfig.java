package socialnetworkbackend.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/v1/auth/**").uri("lb://auth-service"))
                .route("users-service", r -> r.path("/api/v1/user/**").uri("lb://users-service"))
                .route("community-service", r -> r.path("/api/v1/community/**").uri("lb://community-service"))
                .route("membership-service", r -> r.path("/api/v1/membership/**").uri("lb://membership-service"))
                .route("voting-service", r -> r.path("/api/v1/voting/**").uri("lb://voting-service"))
                .build();
    }
}
