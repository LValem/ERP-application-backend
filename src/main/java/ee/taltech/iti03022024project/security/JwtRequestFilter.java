package ee.taltech.iti03022024project.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final SecretKey key;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Optional<String> token = getToken(request);
        if (token.isPresent()) {
            Claims tokenBody = parseToken(token.get());
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(buildAuthToken(tokenBody));
        }

        chain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7)); // "Bearer " is 7 chars
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Authentication buildAuthToken(Claims tokenBody) {
        String username = tokenBody.get("name", String.class);
        Integer permissionId = tokenBody.get("permissionId", Integer.class);

        String role = mapPermissionIdToRole(permissionId);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(username, null, List.of(new SimpleGrantedAuthority(role)));
    }

    private String mapPermissionIdToRole(Integer permissionId) {
        return switch (permissionId) {
            case 1 -> "ADMIN";
            case 2 -> "USER";
            case 3 -> "DRIVER";
            default -> "USER";
        };
    }


}
