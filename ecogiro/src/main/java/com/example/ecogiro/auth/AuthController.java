package com.example.ecogiro.auth;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
@RestController
public class AuthController {
 private final UserRepository users; private final PasswordEncoder encoder;
 public AuthController(UserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
 public record Registration(@NotBlank @Size(max=100) String name,
  @NotBlank @Email @Size(max=254) String email, @NotBlank @Size(min=8,max=72) String password){}
 @GetMapping("/api/health") public Map<String,String> health(){return Map.of("status","ok");}
 @GetMapping("/api/auth/csrf") public Map<String,String> csrf(CsrfToken token){
  return Map.of("token",token.getToken(),"headerName",token.getHeaderName());
 }
 @PostMapping("/api/auth/register") public ResponseEntity<?> register(@Valid @RequestBody Registration r){
  if(r.name().isBlank() || r.password().getBytes(StandardCharsets.UTF_8).length>72)
   return ResponseEntity.badRequest().body(Map.of("message","Nome obrigatório; senha entre 8 caracteres e 72 bytes."));
  try {users.saveAndFlush(new AppUser(r.name().strip(),r.email().strip().toLowerCase(Locale.ROOT),encoder.encode(r.password())));}
  catch(DataIntegrityViolationException ex){return ResponseEntity.status(409).body(Map.of("message","Não foi possível cadastrar esse e-mail."));}
  return ResponseEntity.status(201).body(Map.of("message","Cadastro realizado. Faça login."));
 }
 @GetMapping("/api/auth/me") public Map<String,Object> me(Principal principal){
  AppUser u=users.findByEmail(principal.getName()).orElseThrow();
  return Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail());
 }
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> invalid(){
  return ResponseEntity.badRequest().body(Map.of("message","Confira nome, e-mail e senha (mínimo de 8 caracteres)."));
 }
}
