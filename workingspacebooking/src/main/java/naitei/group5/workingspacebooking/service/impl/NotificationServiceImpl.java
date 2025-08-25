package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import naitei.group5.workingspacebooking.dto.request.CreateSystemNotificationRequest;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.enums.NotificationType;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.EmailResult;
import naitei.group5.workingspacebooking.service.EmailService;
import naitei.group5.workingspacebooking.service.NotificationService;
import naitei.group5.workingspacebooking.service.SendResult;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MessageSource messageSource;

    @Override
    public SendResult sendSystemNotification(CreateSystemNotificationRequest req) {
        List<User> users = userRepository.findAllByRoleIn(req.targetRoles());
        
        if (users.isEmpty()) {
            return new SendResult(0, 0, 0, List.of());
        }
        
        Map<String, User> emailUserMap = users.stream()
            .collect(Collectors.toMap(User::getEmail, user -> user));

        String subject = buildSubject(req.type());
        String body = buildBody(req.title(), req.content());

        // Sử dụng CompletableFuture để gửi email song song
        List<CompletableFuture<EmailSendResult>> futures = emailUserMap.keySet().stream()
            .map(email -> sendEmailAsync(email, subject, body)
                .exceptionally(ex -> new EmailSendResult(email, false, ex.getMessage())))
            .toList();

        // Chờ tất cả email được gửi xong và thu thập kết quả
        List<EmailResult> emailResults = new ArrayList<>();

        for (CompletableFuture<EmailSendResult> future : futures) {
            EmailSendResult result = future.join();
            User user = emailUserMap.get(result.email());
            
            EmailResult emailResult = new EmailResult(
                result.email(),
                user != null && user.getName() != null ? user.getName() : "N/A",
                user != null && user.getRole() != null ? user.getRole().name() : "UNKNOWN",
                result.success(),
                result.success() 
                    ? getMessage("notification.result.status.success")
                    : getMessage("notification.result.status.failed", result.message())
            );
            emailResults.add(emailResult);
        }

        // Sort email mặc định thành công trước rồi thất bại sau
        emailResults.sort((a, b) -> {
            if (a.success() != b.success()) {
                return Boolean.compare(b.success(), a.success());
            }
            return a.email().compareTo(b.email());
        });

        long successCount = emailResults.stream().filter(EmailResult::success).count();
        long failedCount = emailResults.size() - successCount;

        return new SendResult(emailResults.size(), (int) successCount, (int) failedCount, emailResults);
    }

    @Async("emailTaskExecutor")
    public CompletableFuture<EmailSendResult> sendEmailAsync(String email, String subject, String body) {
        try {
            emailService.sendSimpleMessage(email, subject, body);
            
            return CompletableFuture.completedFuture(new EmailSendResult(email, true, "Success"));
            
        } catch (Exception e) {
            return CompletableFuture.completedFuture(new EmailSendResult(email, false, e.getMessage()));
        }
    }

    public record EmailSendResult(String email, boolean success, String message) {}

    private String buildSubject(NotificationType type) {
        return switch (type) {
            case EMERGENCY -> getMessage("notification.email.subject.emergency");
            case MAINTENANCE -> getMessage("notification.email.subject.maintenance");
            case POLICY_UPDATE -> getMessage("notification.email.subject.policy.update");
        };
    }

    private String buildBody(String title, String content) {
        return getMessage("notification.email.body.template", title, content);
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
