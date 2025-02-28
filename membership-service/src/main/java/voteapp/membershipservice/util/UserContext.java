package voteapp.membershipservice.util;

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
        return Mono.deferContextual(ctx -> {
            if (ctx.hasKey(USER_ID_KEY)) {
                return Mono.just(ctx.get(USER_ID_KEY));
            } else {
                return Mono.error(new IllegalStateException("User ID not found in context"));
            }
        });
    }
}