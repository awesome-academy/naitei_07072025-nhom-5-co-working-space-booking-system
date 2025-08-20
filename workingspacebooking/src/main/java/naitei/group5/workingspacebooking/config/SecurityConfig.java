package naitei.group5.workingspacebooking.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static naitei.group5.workingspacebooking.constant.Endpoint.*;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/admin/login",
                                "/css/**", "/js/**", "/images/**", "/static/**",
                                // API endpoints
                                AUTH_LOGIN,
                                AUTH_REFRESH,
                                AUTH_LOGOUT,
                                AUTH_RECOVERY,
                                AUTH_REGISTER_RENTER,
                                AUTH_REGISTER_OWNER,
                                OWNER_VENUES
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("admin")
                        .requestMatchers(RENTER_VENUES, RENTER_VENUES_SUB).hasRole("renter") 
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
