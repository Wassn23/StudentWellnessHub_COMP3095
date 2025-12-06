package ca.gbc.comp3095.gatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/api/events/**").hasAnyAuthority("ROLE_STUDENT", "ROLE_STAFF")
                        .pathMatchers("/api/wellness/**").hasAuthority("ROLE_STUDENT")
                        .pathMatchers("/api/goals/**").hasAuthority("ROLE_STAFF")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                );

        return http.build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            Collection<GrantedAuthority> authorities;

            if (realmAccess != null && realmAccess.containsKey("roles")) {
                authorities = ((List<String>) realmAccess.get("roles")).stream()
                        .map(role -> {
                            System.out.println("Extracted role: " + role);
                            return new SimpleGrantedAuthority(role);
                        })
                        .collect(Collectors.toList());
            } else {
                authorities = List.of();
            }

            System.out.println("Total authorities: " + authorities);
            return authorities;
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
