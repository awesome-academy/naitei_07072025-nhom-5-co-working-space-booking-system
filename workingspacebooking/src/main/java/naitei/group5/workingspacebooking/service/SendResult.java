package naitei.group5.workingspacebooking.service;

import java.util.List;

public record SendResult(
    int totalRecipients,
    int successCount,
    int failedCount,
    List<EmailResult> emailResults
) {}
