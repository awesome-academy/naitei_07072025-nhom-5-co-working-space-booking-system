package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import naitei.group5.workingspacebooking.entity.AccountRecovery;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.enums.AccountRecoveryStatus;
import naitei.group5.workingspacebooking.repository.AccountRecoveryRepository;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.AccountRecoveryService;
import naitei.group5.workingspacebooking.service.EmailService;
import naitei.group5.workingspacebooking.dto.request.PasswordResetRequest;
import naitei.group5.workingspacebooking.utils.TokenGenerator;

import static naitei.group5.workingspacebooking.constant.Endpoint.AUTH_RECOVERY_CONFIRM;
import static naitei.group5.workingspacebooking.constant.Endpoint.BASE;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AccountRecoveryServiceImpl implements AccountRecoveryService {

    private final UserRepository userRepository;
    private final AccountRecoveryRepository accountRecoveryRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public void initiateRecovery(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String token = TokenGenerator.generateToken(30);

        AccountRecovery recovery = AccountRecovery.builder()
                .user(user)
                .recoveryToken(token)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .status(AccountRecoveryStatus.pending)
                .build();

        accountRecoveryRepository.save(recovery);

        String resetLink = BASE + AUTH_RECOVERY_CONFIRM + token;

        emailService.sendSimpleMessage(
                user.getEmail(),
                "Password Recovery",
                "Click this link to reset your password: " + resetLink
        );
    }

    @Override
    @Transactional
    public void confirmRecovery(String token, PasswordResetRequest request) {
        AccountRecovery recovery = accountRecoveryRepository.findByRecoveryToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (recovery.getExpiryDate().isBefore(LocalDateTime.now())) {
            recovery.setStatus(AccountRecoveryStatus.expired);
            accountRecoveryRepository.save(recovery);
            throw new IllegalStateException("Token expired");
        }

        User user = recovery.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        recovery.setStatus(AccountRecoveryStatus.used);
        accountRecoveryRepository.save(recovery);
    }
}
