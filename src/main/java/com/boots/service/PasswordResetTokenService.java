package com.boots.service;

import com.boots.model.PasswordResetToken;
import com.boots.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    public PasswordResetToken createPasswordResetTokenForEmail(String email) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setEmail(email);
        passwordResetToken.setToken(UUID.randomUUID().toString());
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Неверный токен восстановления пароля"));
    }
}
