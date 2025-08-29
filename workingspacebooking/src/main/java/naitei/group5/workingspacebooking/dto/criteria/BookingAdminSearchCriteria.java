package naitei.group5.workingspacebooking.dto.criteria;

public record BookingAdminSearchCriteria(
    String userEmail,
    String status,
    Integer venueId
) {
    public boolean hasAnyFilter() {
        return (userEmail != null && !userEmail.isBlank()) ||
               (status != null && !status.isBlank()) ||
               venueId != null;
    }
}
