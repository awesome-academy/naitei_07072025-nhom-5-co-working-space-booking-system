package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.request.RegisterRequest;
import naitei.group5.workingspacebooking.dto.response.RegisterResponse;
import naitei.group5.workingspacebooking.dto.response.UserResponse;

public interface UserService {
    RegisterResponse registerRenter(RegisterRequest request);
    RegisterResponse registerOwner(RegisterRequest request);
    UserResponse getUserProfile(JwtUserDetails userDetails);
}
