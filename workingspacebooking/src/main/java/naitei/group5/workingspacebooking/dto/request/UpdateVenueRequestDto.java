package naitei.group5.workingspacebooking.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVenueRequestDto {

    @NotBlank(message = "{venue.update.name.required}")
    private String name;

    private String description;

    @NotNull(message = "{venue.update.capacity.required}")
    @Min(value = 1, message = "{venue.update.capacity.min}")
    private Integer capacity;

    @NotBlank(message = "{venue.update.location.required}")
    private String location;

    @NotNull(message = "{venue.update.venueStyle.required}")
    private Integer venueStyleId;

    private String image;
}
