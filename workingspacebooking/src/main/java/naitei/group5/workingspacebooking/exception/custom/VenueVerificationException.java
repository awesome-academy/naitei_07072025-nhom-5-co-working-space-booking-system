package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class VenueVerificationException extends ApiException {
    
    private final String operation;
    private final Integer venueId;
    
    public VenueVerificationException(String operation, Integer venueId, String message) {
        super(ErrorCode.VENUE_VERIFICATION_FAILED);
        this.operation = operation;
        this.venueId = venueId;
    }
    
    public VenueVerificationException(String operation, Integer venueId, String message, Throwable cause) {
        super(ErrorCode.VENUE_VERIFICATION_FAILED);
        this.operation = operation;
        this.venueId = venueId;
        this.initCause(cause);
    }
    
    public String getOperation() {
        return operation;
    }
    
    public Integer getVenueId() {
        return venueId;
    }
}
