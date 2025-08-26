package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class InvalidDurationException extends ApiException {
    public InvalidDurationException() {
        super(ErrorCode.INVALID_DURATION);
    }
}
