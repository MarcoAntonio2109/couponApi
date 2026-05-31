package br.com.onebrain.couponapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // desabilita CSRF para facilitar testes
                .authorizeHttpRequests(auth -> auth
                        // libera acesso ao Swagger e API docs
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // libera também o console do H2
                        .requestMatchers("/h2-console/**").permitAll()
                        // libera todas as rotas da API de cupons
                        .requestMatchers("/api/coupons/**").permitAll()
                        // exige autenticação para o restante
                        .anyRequest().authenticated()
                )
                // necessário para o console H2 funcionar em frames
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
