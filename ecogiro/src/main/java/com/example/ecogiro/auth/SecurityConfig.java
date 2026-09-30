package com.example.ecogiro.auth;
import java.util.Locale;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean UserDetailsService users(UserRepository repository){return email -> {
  AppUser u=repository.findByEmail(email.strip().toLowerCase(Locale.ROOT))
   .orElseThrow(()->new UsernameNotFoundException("Credenciais inválidas"));
  return User.withUsername(u.getEmail()).password(u.getPasswordHash()).roles("USER").build();
 };}
 @Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
  http.authorizeHttpRequests(a->a.requestMatchers("/api/auth/csrf","/api/auth/register","/api/health").permitAll()
   .requestMatchers("/api/**").authenticated().anyRequest().permitAll())
   .formLogin(f->f.loginPage("/login.html").loginProcessingUrl("/api/auth/login")
    .successHandler((req,res,auth)->{res.setContentType("application/json");res.getWriter().write("{\"message\":\"Login realizado\"}");})
    .failureHandler((req,res,ex)->{res.setStatus(401);res.setContentType("application/json");res.getWriter().write("{\"message\":\"E-mail ou senha incorretos\"}");}).permitAll())
   .logout(l->l.logoutUrl("/api/auth/logout").deleteCookies("JSESSIONID")
    .logoutSuccessHandler((req,res,auth)->res.setStatus(204)))
   .requestCache(c->c.disable())
   .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->res.setStatus(401)));
  return http.build();
 }
}
