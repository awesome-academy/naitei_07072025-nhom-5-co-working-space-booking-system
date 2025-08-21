package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class InvalidTimeRangeException extends ApiException {
    public InvalidTimeRangeException() {
        super(ErrorCode.INVALID_TIME_RANGE);
    }
}
