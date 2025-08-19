package naitei.group5.workingspacebooking.service;

import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.entity.enums.UserRole;

import java.util.List;

public interface AdminService {
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(UserRole role);

    UserResponse getUserById(Integer id);
    UserResponse updateUser(Integer id, UserResponse updatedUser);
    UserResponse getUserByEmail(String email);
}
