package com.boots.service;

import com.boots.model.PasswordResetToken;
import com.boots.model.User;
import com.boots.repository.PasswordResetTokenRepository;
import com.boots.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Такого пользователя нет"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, new ArrayList<>());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Пользователь с таким email уже зарегистрирован");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setConfirmationToken(UUID.randomUUID().toString());
        user.setEnabled(false);
        User savedUser = userRepository.save(user);

        sendConfirmationEmail(savedUser);

        return savedUser;
    }

    public void sendConfirmationEmail(User user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Подтверждение регистрации");
        mailMessage.setText("Для подтверждения регистрации перейдите по ссылке: "
                + "http://localhost:8080/api/users/confirm?code=" + user.getConfirmationToken());

        try {
            javaMailSender.send(mailMessage);
            logger.info("Письмо для подтверждения регистрации отправлено на адрес: " + user.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка отправки письма для подтверждения регистрации:", e);
            throw new RuntimeException("Ошибка отправки письма для подтверждения регистрации", e);
        }
    }

    public boolean confirmRegistration(String token) {
        Optional<User> optionalUser = userRepository.findByConfirmationToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setConfirmationToken(null);
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void createPasswordResetTokenForEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            passwordResetTokenRepository.save(myToken);

            sendPasswordResetEmail(user, token);
        } else {
            throw new RuntimeException("Пользователь с указанным email не найден");
        }
    }

    public void sendPasswordResetEmail(User user, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Сброс пароля");
        mailMessage.setText("Для сброса пароля перейдите по ссылке: "
                + "http://localhost:8080/api/users/reset-password-confirm?token=" + token);

        try {
            javaMailSender.send(mailMessage);
            logger.info("Письмо для сброса пароля отправлено на адрес: " + user.getEmail());
        } catch (Exception e) {
            logger.error("Ошибка отправки письма для сброса пароля:", e);
            throw new RuntimeException("Ошибка отправки письма для сброса пароля", e);
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> optToken = passwordResetTokenRepository.findByToken(token);
        if (optToken.isPresent()) {
            PasswordResetToken passwordResetToken = optToken.get();
            if (passwordResetToken.isExpired()) {
                return false;
            }
            User user = passwordResetToken.getUser();
            user.setPassword(bCryptPasswordEncoder.encode(newPassword));
            userRepository.save(user);
            passwordResetTokenRepository.delete(passwordResetToken);
            return true;
        }
        return false;
    }
}
