package naitei.group5.workingspacebooking.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import naitei.group5.workingspacebooking.repository.UserSessionRepository;
import naitei.group5.workingspacebooking.service.TokenBlacklist;
import naitei.group5.workingspacebooking.utils.JwtUtils;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwt;
    private final TokenBlacklist blacklist;
    private final UserSessionRepository sessionRepo;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;

        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring(7);
        } else {
            // Fallback: đọc token từ cookie ACCESS_TOKEN
            token = resolveFromCookie(req, "ACCESS_TOKEN");
        }

        if (token != null) {
            try {
                var claims = jwt.parse(token).getBody();
                Integer sid = (Integer) claims.get("sid");
                Integer userId = claims.get("userId", Integer.class);
                String email = claims.getSubject();
                String role  = (String) claims.get("role");

                boolean expired = claims.getExpiration().getTime() < System.currentTimeMillis();
                boolean revoked = blacklist.isRevoked(sid);
                boolean sessionActive = sessionRepo.findByIdAndLogoutTimeIsNull(sid).isPresent();

                if (!expired && !revoked && sessionActive) {

                    JwtUserDetails userDetails = new JwtUserDetails(userId, email, role);
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {}
        }

        chain.doFilter(req, res);
    }

    private String resolveFromCookie(HttpServletRequest request, String cookieName) {
        var cookies = request.getCookies();
        if (cookies == null) return null;
        for (var cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
