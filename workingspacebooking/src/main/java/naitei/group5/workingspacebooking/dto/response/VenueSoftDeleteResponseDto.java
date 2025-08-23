package naitei.group5.workingspacebooking.dto.response;

public record VenueSoftDeleteResponseDto(
        Integer id,
        String name,
        Boolean deleted,
        String message
) {}
