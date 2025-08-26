package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class VenueNotFoundException extends ApiException {
    
    private final Integer venueId;
    
    public VenueNotFoundException() {
        super(ErrorCode.VENUE_NOT_FOUND);
        this.venueId = null;
    }
    
    public VenueNotFoundException(Integer venueId) {
        super(ErrorCode.VENUE_NOT_FOUND);
        this.venueId = venueId;
    }
    
    public Integer getVenueId() {
        return venueId;
    }
}
