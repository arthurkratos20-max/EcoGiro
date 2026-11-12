package com.example.ecogiro.service;

import com.example.ecogiro.model.AppUser;
import com.example.ecogiro.model.PasswordResetToken;
import com.example.ecogiro.model.Role;
import com.example.ecogiro.repository.AppUserRepository;
import com.example.ecogiro.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;

@Service
public class PasswordResetService {
    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_VALIDITY_MINUTES = 30;

    private final AppUserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.public-url:http://localhost:8080}")
    private String publicUrl;

    @Value("${app.password-reset.from:${spring.mail.username:}}")
    private String fromAddress;

    public PasswordResetService(
            AppUserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderProvider = mailSenderProvider;
    }

    @Transactional
    public void requestReset(String email) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        if (normalizedEmail.isBlank()) return;

        AppUser user = userRepository.findByEmailIgnoreCase(normalizedEmail).orElse(null);
        // A resposta HTTP é sempre genérica para não revelar se um e-mail está cadastrado.
        if (user == null || !user.isActive() || user.getRole() != Role.USER) return;

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException("O serviço de envio de e-mail não está configurado.");
        }

        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String rawToken = HexFormat.of().formatHex(bytes);
        String tokenHash = sha256(rawToken);

        tokenRepository.deleteAllByUser(user);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_VALIDITY_MINUTES));
        tokenRepository.save(resetToken);

        String base = publicUrl == null ? "http://localhost:8080" : publicUrl.replaceAll("/+$", "");
        String link = base + "/redefinir-senha.html?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(user.getEmail());
        message.setSubject("Recuperação de senha — EcoGiro");
        message.setText(
                "Olá, " + user.getFullName() + "!\n\n"
                + "Recebemos uma solicitação para redefinir a senha da sua conta EcoGiro.\n"
                + "Acesse o link abaixo para criar uma nova senha (válido por 30 minutos e utilizável uma única vez):\n\n"
                + link + "\n\n"
                + "Se você não solicitou esta alteração, ignore este e-mail. Sua senha atual continuará a mesma.\n\n"
                + "Por segurança, não compartilhe este link.\n\nEquipe EcoGiro"
        );
        mailSender.send(message);
    }

    @Transactional
    public void resetPassword(String rawToken, String newPassword, String confirmation) {
        if (rawToken == null || !rawToken.matches("[a-fA-F0-9]{64}")) {
            throw new IllegalArgumentException("O link de recuperação é inválido ou expirou.");
        }
        if (newPassword == null || newPassword.length() < 8) {
            throw new IllegalArgumentException("A nova senha deve ter no mínimo 8 caracteres.");
        }
        if (!newPassword.equals(confirmation)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }

        PasswordResetToken token = tokenRepository.findByTokenHash(sha256(rawToken))
                .orElseThrow(() -> new IllegalArgumentException("O link de recuperação é inválido ou expirou."));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())
                || !token.getUser().isActive()
                || token.getUser().getRole() != Role.USER) {
            tokenRepository.delete(token);
            throw new IllegalArgumentException("O link de recuperação é inválido ou expirou.");
        }

        AppUser user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Excluir o token garante uso único.
        tokenRepository.delete(token);
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Não foi possível processar o token de recuperação.", ex);
        }
    }
}
