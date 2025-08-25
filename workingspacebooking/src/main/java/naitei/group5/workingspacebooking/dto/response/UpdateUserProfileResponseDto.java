package naitei.group5.workingspacebooking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserProfileResponseDto {
    private Integer id;
    private String name;
    private String email; 
    private String phone;
}
