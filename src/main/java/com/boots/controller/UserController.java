package com.boots.controller;

import com.boots.model.User;
import com.boots.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            userService.register(user);
            return ResponseEntity.ok("Пожалуйста, подтвердите регистрацию через email.");
        } catch (Exception e) {
            logger.error("Ошибка при регистрации пользователя:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при регистрации пользователя.");
        }
    }

    @GetMapping("/confirm")
    public ResponseEntity<String> confirmRegistration(@RequestParam String code) {
        if (userService.confirmRegistration(code)) {
            return ResponseEntity.ok("Регистрация успешно подтверждена.");
        } else {
            return ResponseEntity.badRequest().body("Неверный код подтверждения.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> json) {
        String email = json.get("email");
        try {
            userService.createPasswordResetTokenForEmail(email);
            return ResponseEntity.ok("Письмо для сброса пароля отправлено на указанный email.");
        } catch (Exception e) {
            logger.error("Ошибка при сбросе пароля:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при сбросе пароля.");
        }
    }

    @PostMapping("/reset-password-confirm")
    public ResponseEntity<String> resetPasswordConfirm(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        if (userService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok("Пароль успешно изменен.");
        } else {
            return ResponseEntity.badRequest().body("Ошибка при смене пароля.");
        }
    }
}
