package voteapp.membershipservice.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import voteapp.membershipservice.client.AuthClient;
import voteapp.membershipservice.dto.ErrorResponse;
import voteapp.membershipservice.exception.InvalidTokenException;
import voteapp.membershipservice.util.UserContext;

import java.text.ParseException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenFilter implements WebFilter {

    private final AuthClient authClient;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        log.info("Received token: {}", token);  // Логирование входящего токена

        if (token == null || token.isEmpty()) {
            log.info("Token is missing or empty");
            return chain.filter(exchange);
        }

        return validateToken(token)
                .flatMap(valid -> {
                    log.info("Token validation result: {}", valid);  // Логируем результат валидации
                    if (!valid) {
                        return unauthorized(exchange, "Invalid token");
                    }

                    String userId;
                    try {
                        userId = getUserId(token);
                        log.info("Extracted user ID: {}", userId);  // Логируем извлеченный userId
                    } catch (InvalidTokenException e) {
                        log.error("Token parsing failed", e);
                        return unauthorized(exchange, e.getMessage());
                    }

                    Authentication authentication = getAuthentication(userId);
                    SecurityContext context = new SecurityContextImpl(authentication);

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
                            .contextWrite(UserContext.withUserId(UUID.fromString(userId)));
                });
    }


    private Mono<Boolean> validateToken(String token) {
        return Mono.fromCallable(() -> authClient.validateToken(token))
                .subscribeOn(Schedulers.boundedElastic());
    }


    private Authentication getAuthentication(String userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private String getUserId(String token) {
        try {
            token = token.substring(7);
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new InvalidTokenException("Token does not have a valid payload");
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        try {
            return exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(
                            objectMapper.writeValueAsBytes(new ErrorResponse(message))
                    ))
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
