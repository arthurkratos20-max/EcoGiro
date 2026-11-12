package com.example.ecogiro.config;

import com.example.ecogiro.model.Role;
import com.example.ecogiro.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {

        http
            .csrf(csrf -> csrf
                // Mantem compatibilidade com formularios antigos; protege novas escritas setoriais.
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .requireCsrfProtectionMatcher(request ->
                    "POST".equalsIgnoreCase(request.getMethod())
                    && (request.getRequestURI().equals("/api/admin/redistribution-proposals")
                        || request.getRequestURI().startsWith("/api/admin/redistribution-proposals/")
                        || request.getRequestURI().startsWith("/api/admin/operations/"))
                )
            )

            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            )

            .authorizeHttpRequests(auth -> auth

                // O console H2 nunca deve ficar público, mesmo se alguém habilitar
                // acidentalmente SPRING_H2_CONSOLE_ENABLED no ambiente.
                .requestMatchers("/h2-console/**").denyAll()

                // Rotas públicas
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/esqueci-senha.html",
                    "/redefinir-senha.html",
                    "/forgot-password",
                    "/reset-password",
                    "/cadastro.html",
                    "/admin-login.html",
                    "/admin-cadastro.html",
                    "/quiz.html",
                    "/403.html",
                    "/assets/**",
                    "/register",
                    "/admin/register",
                    "/admin/login",
                    "/api/recommendations",
                    "/api/requests/lookup",
                    "/consulta-solicitacao.html",
                    "/error",
                    "/favicon.ico"
                ).permitAll()

                .requestMatchers("/operacao.html", "/api/admin/operations/**")
                .hasAnyAuthority("ROLE_ADMIN_GERAL", "ROLE_ADMIN_SETORIAL", "ROLE_ADMIN_LEGACY")

                // Setores: leitura do proprio territorio para ADMIN_SETORIAL.
                .requestMatchers("/api/admin/sectors")
                .hasAnyAuthority("ROLE_ADMIN_GERAL", "ROLE_ADMIN_SETORIAL")

                // Propostas e distribuicao: apenas administrador geral.
                .requestMatchers("/api/admin/redistribution-proposals/**",
                                 "/api/admin/redistribution-proposals",
                                 "/api/admin/sector-distribution/**")
                .hasAuthority("ROLE_ADMIN_GERAL")

                // Mapa administrativo dedicado, inclusive para contas legadas.
                .requestMatchers("/mapa-admin.html", "/estoque.html")
                .hasAnyAuthority("ROLE_ADMIN_GERAL", "ROLE_ADMIN_SETORIAL", "ROLE_ADMIN_LEGACY")

                // Painel setorial dedicado: nao oferece operacoes globais.
                .requestMatchers("/admin-setorial.html").hasAuthority("ROLE_ADMIN_SETORIAL")

                // Painel legado ainda mostra dados globais: bloquear ADMIN_SETORIAL.
                .requestMatchers("/admin.html", "/api/admin/**")
                .hasAnyAuthority("ROLE_ADMIN_GERAL", "ROLE_ADMIN_LEGACY")

                // Rotas exclusivas de USER
                .requestMatchers(
                    "/usuario.html",
                    "/planos-aluguel.html",
                    "/api/user/**"
                ).hasRole(Role.USER.name())

                // Rotas compartilhadas entre usuários autenticados
                .requestMatchers(
                    "/mapa.html",
                    "/api/session/profile",
                    "/api/map/**"
                ).authenticated()

                .anyRequest().permitAll()
            )

            // Login do usuário comum
            .formLogin(form -> form
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")

                .successHandler((request, response, authentication) -> {

                    boolean isUser = authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_USER")
                        );

                    // Impede ADMIN de entrar pelo login de usuário
                    if (!isUser) {

                        SecurityContextHolder.clearContext();

                        if (request.getSession(false) != null) {
                            request.getSession(false).invalidate();
                        }

                        response.sendRedirect(
                            "/login.html?error=Esta+conta+e+administrativa.+Use+o+acesso+de+administrador."
                        );

                        return;
                    }

                    response.sendRedirect("/usuario.html");
                })

                .failureUrl("/login.html?error=1")
                .permitAll()
            )

            // Página personalizada para acesso sem permissão
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        writeSecurityError(response, objectMapper, HttpStatus.UNAUTHORIZED,
                                "UNAUTHORIZED", "É necessário autenticar-se para acessar este recurso.",
                                request.getRequestURI());
                    } else {
                        response.sendRedirect("/login.html");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getRequestURI().startsWith("/api/")) {
                        writeSecurityError(response, objectMapper, HttpStatus.FORBIDDEN,
                                "FORBIDDEN", "Você não possui permissão para acessar este recurso.",
                                request.getRequestURI());
                    } else {
                        response.sendRedirect("/403.html");
                    }
                })
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.html?logout=1")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }

    private void writeSecurityError(HttpServletResponse response, ObjectMapper objectMapper,
                                    HttpStatus status, String error, String message, String path)
            throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(
                ApiErrorResponse.of(status.value(), error, message, path)));
    }
}