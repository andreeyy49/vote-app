package voteapp.communityservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import voteapp.communityservice.client.AuthClient;
import voteapp.communityservice.dto.ErrorResponse;
import voteapp.communityservice.exception.InvalidTokenException;
import voteapp.communityservice.util.UserContext;

import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {

    private final AuthClient authClient;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        log.info("token {}", token);
        try {
            boolean isValid = authClient.validateToken(token);
            if (!isValid) {
                throw new InvalidTokenException("Invalid token");
            }
            String userId = getUserId(token);
            UserContext.setUserId(UUID.fromString(userId));
            Authentication authentication = getAuthentication(userId);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        } finally {
            UserContext.clear();
        }
    }

    private Authentication getAuthentication(String userId) {
        return new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
    }

    private String getUserId(String token) {
        try {
            token = token.substring(7);
            return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new InvalidTokenException("Token dont hase payload");
        }
    }
}
