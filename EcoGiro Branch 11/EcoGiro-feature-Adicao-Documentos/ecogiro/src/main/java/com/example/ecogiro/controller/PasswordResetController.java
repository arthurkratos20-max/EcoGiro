package com.example.ecogiro.controller;

import com.example.ecogiro.service.PasswordResetService;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public void requestReset(@RequestParam String email, HttpServletResponse response) throws IOException {
        try {
            passwordResetService.requestReset(email);
            response.sendRedirect("/esqueci-senha.html?sent=1");
        } catch (IllegalStateException | MailException ex) {
            response.sendRedirect("/esqueci-senha.html?error=mail");
        }
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestParam String token,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              HttpServletResponse response) throws IOException {
        try {
            passwordResetService.resetPassword(token, password, confirmPassword);
            response.sendRedirect("/login.html?reset=1");
        } catch (IllegalArgumentException ex) {
            String message = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("/redefinir-senha.html?error=" + message);
        }
    }
}
