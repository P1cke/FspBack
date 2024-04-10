package com.boots.controller;

import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @PostMapping("/reset-password-confirm/{token}")
    public ResponseEntity<String> resetPasswordConfirm(@PathVariable String token, @RequestBody String newPassword) {
        if (userService.resetPassword(token, newPassword)) {
            return ResponseEntity.ok("Пароль успешно изменен.");
        } else {
            return ResponseEntity.badRequest().body("Ошибка при смене пароля.");
        }
    }
}
