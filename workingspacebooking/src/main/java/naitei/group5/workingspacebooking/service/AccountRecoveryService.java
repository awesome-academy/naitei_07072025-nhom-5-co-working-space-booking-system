package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.request.PasswordResetRequest;

public interface AccountRecoveryService {
    void initiateRecovery(String email);
    void confirmRecovery(String token, PasswordResetRequest request);
}
