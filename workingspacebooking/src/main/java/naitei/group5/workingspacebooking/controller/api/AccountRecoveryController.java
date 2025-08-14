package naitei.group5.workingspacebooking.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import naitei.group5.workingspacebooking.dto.request.PasswordResetRequest;
import naitei.group5.workingspacebooking.dto.response.ApiResponse;
import naitei.group5.workingspacebooking.service.AccountRecoveryService;

@RestController
@RequestMapping("/api/auth/recover")
@RequiredArgsConstructor
public class AccountRecoveryController {

    private final AccountRecoveryService accountRecoveryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> initiateRecovery(@RequestParam String email) {
        accountRecoveryService.initiateRecovery(email);
        return ResponseEntity.ok(ApiResponse.success("Recovery email sent", null));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmRecovery(@RequestParam String token,
                                                             @RequestBody PasswordResetRequest request) {
        accountRecoveryService.confirmRecovery(token, request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }
}
