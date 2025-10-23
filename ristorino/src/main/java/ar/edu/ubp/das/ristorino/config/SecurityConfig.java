/*package ar.edu.ubp.das.ristorino.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ Solo estos dos endpoints son públicos:
                        .requestMatchers("/api/v1/ristorino/registrarCliente", "/api/v1/ristorino/login").permitAll()
                        // 🚫 Todo lo demás requiere JWT
                        .anyRequest().authenticated()
                )
                // 🧩 Recurso protegido con JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.decoder(jwtDecoder()))
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        var key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
*/
package ar.edu.ubp.das.ristorino.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 🔸 Desactiva protección CSRF
                .csrf(csrf -> csrf.disable())

                // 🔸 Permite TODO (ninguna ruta requiere autenticación)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 🔸 Desactiva el soporte de OAuth2 / JWT completamente
                .oauth2ResourceServer(oauth2 -> oauth2.disable());

        return http.build();
    }
}
