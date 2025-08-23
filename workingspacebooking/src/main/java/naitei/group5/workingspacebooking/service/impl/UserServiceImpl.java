package naitei.group5.workingspacebooking.service.impl;

import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.entity.enums.UserRole;
import naitei.group5.workingspacebooking.exception.ResourceNotFoundException;
import naitei.group5.workingspacebooking.utils.ConverterDto;
import naitei.group5.workingspacebooking.utils.PasswordUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import naitei.group5.workingspacebooking.config.JwtUserDetails;


import naitei.group5.workingspacebooking.dto.request.RegisterRequest;
import naitei.group5.workingspacebooking.dto.request.UpdateUserProfileRequestDto;
import naitei.group5.workingspacebooking.dto.response.RegisterResponse;
import naitei.group5.workingspacebooking.dto.response.UpdateUserProfileResponseDto;
import naitei.group5.workingspacebooking.dto.response.UserResponse;
import naitei.group5.workingspacebooking.entity.User;
import naitei.group5.workingspacebooking.repository.UserRepository;
import naitei.group5.workingspacebooking.service.EmailService;
import naitei.group5.workingspacebooking.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;
	private final MessageSource messageSource;

    @Override
    public RegisterResponse registerRenter(RegisterRequest request) {
        return registerWithRole(request, UserRole.renter);
    }

    @Override
    public RegisterResponse registerOwner(RegisterRequest request) {
        return registerWithRole(request, UserRole.pending_owner);
    }

    private RegisterResponse registerWithRole(RegisterRequest request, UserRole role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String rawPassword = PasswordUtil.generateRandomPassword(8);

        String encodedPassword = passwordEncoder.encode(rawPassword);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(encodedPassword)
                .role(role) // role được set từ service
                .build();

        // Lưu DB
        userRepository.save(user);

        emailService.sendSimpleMessage(
                user.getEmail(),
                "Your Account Password",
                "Hello " + user.getName() + ",\n\n" +
                        "Your account has been created successfully.\n" +
                        "Your password is: " + rawPassword + "\n\n" +
                        "Please change it after logging in."
        );

        return RegisterResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserResponse getUserProfile(JwtUserDetails userDetails) {
        Integer userId = userDetails.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getMessage("user.profile.notfound", userId)
                ));

        return ConverterDto.toUserResponse(user);
    }

	@Override
    @Transactional
    public UpdateUserProfileResponseDto updateUserProfile(JwtUserDetails userDetails,
                                                          UpdateUserProfileRequestDto requestDto) {
        Integer userId = userDetails.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        getMessage("user.profile.update.notfound", userId)
                ));

        // update trực tiếp entity
        user.setName(requestDto.getName());
        user.setPhone(requestDto.getPhone());

        userRepository.save(user);

        return ConverterDto.toUpdateUserProfileResponseDto(user);
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
