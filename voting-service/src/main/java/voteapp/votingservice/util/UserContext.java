package voteapp.votingservice.util;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.UUID;

public class UserContext {

    public static final String USER_ID_KEY = "user-id";

    // Добавляем userId в контекст
    public static Context withUserId(UUID userId) {
        return Context.of(USER_ID_KEY, userId);
    }

    // Получаем userId из контекста
    public static Mono<UUID> getUserId() {
        return Mono.deferContextual(ctx ->
                ctx.getOrEmpty(USER_ID_KEY)
                        .map(id -> Mono.just((UUID) id))
                        .orElseGet(() -> Mono.error(new IllegalStateException("User ID not found in context")))
        );
    }
}