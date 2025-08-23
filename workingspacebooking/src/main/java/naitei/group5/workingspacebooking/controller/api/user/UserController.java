package naitei.group5.workingspacebooking.controller.api.user;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal JwtUserDetails userDetails) {
        UserResponse response = userService.getUserProfile(userDetails);
        return ResponseEntity.ok(response);
    }
}
