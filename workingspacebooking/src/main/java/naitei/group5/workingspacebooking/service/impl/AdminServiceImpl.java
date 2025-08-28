package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.service.EmailService;
import org.springframework.stereotype.Service;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.AdminService;
import naitei.group5.workingspacebooking.repository.VenueRepository;
import naitei.group5.workingspacebooking.repository.BookingRepository;
import naitei.group5.workingspacebooking.repository.NotificationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));
        return mapToResponse(user);
    }

    @Override
    public UserResponse approveOwner(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id " + userId));

        if (user.getRole() != UserRole.pending_owner) {
            throw new RuntimeException("User is not pending_owner");
        }

        // Cập nhật role
        user.setRole(UserRole.owner);
        User saved = userRepository.save(user);

        // Gửi mail thông báo
        emailService.sendSimpleMessage(
                user.getEmail(),
                "Phê duyệt trở thành chủ sở hữu",
                "Xin chào " + user.getName() + ",\n\n" +
                        "Yêu cầu trở thành chủ sở hữu của bạn đã được phê duyệt. Giờ bạn đã có quyền owner.\n\n" +
                        "Trân trọng,\nĐội ngũ hỗ trợ."
        );

        return mapToResponse(saved);
    }

    @Override
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public long getTotalPendingOwners() {
        return userRepository.countByRole(UserRole.pending_owner);
    }

    @Override
    public long getTotalVenues() {
        return venueRepository.count();
    }

    @Override
    public long getTotalVerifiedVenues() {
        return venueRepository.countByVerifiedTrue();
    }

    @Override
    public long getTotalDeletedVenues() {
        return venueRepository.countByDeletedTrue();
    }

    @Override
    public long getTotalBookings() {
        return bookingRepository.count();
    }

    @Override
    public long getTotalNotifications() {
        return notificationRepository.countByUser_Role(UserRole.admin);
    }
}
