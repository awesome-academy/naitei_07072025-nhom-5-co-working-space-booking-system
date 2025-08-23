package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.config.JwtUserDetails;
import naitei.group5.workingspacebooking.dto.request.RegisterRequest;
import naitei.group5.workingspacebooking.dto.response.RegisterResponse;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.dto.request.UpdateUserProfileRequestDto;
import naitei.group5.workingspacebooking.dto.response.UpdateUserProfileResponseDto;

public interface UserService {
    RegisterResponse registerRenter(RegisterRequest request);
    RegisterResponse registerOwner(RegisterRequest request);
    UserResponse getUserProfile(JwtUserDetails userDetails);
    UpdateUserProfileResponseDto updateUserProfile(JwtUserDetails userDetails, UpdateUserProfileRequestDto requestDto);
}
