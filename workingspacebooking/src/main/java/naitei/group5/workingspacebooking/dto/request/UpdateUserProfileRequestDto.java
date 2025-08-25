package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileRequestDto {

    @NotBlank(message = "{user.profile.update.name.required}")
    @Size(min = 2, max = 100, message = "{user.profile.update.name.size}")
    private String name;

    @NotBlank(message = "{user.profile.update.phone.required}")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "{user.profile.update.phone.pattern}")
    private String phone;
}
