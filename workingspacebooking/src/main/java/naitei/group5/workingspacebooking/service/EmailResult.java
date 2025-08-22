package naitei.group5.workingspacebooking.service;

public record EmailResult(
    String email,
    String name,
    String role,
    boolean success,
    String status
) {}
