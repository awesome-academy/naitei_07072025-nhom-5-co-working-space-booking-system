package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class InvalidCapacityRangeException extends ApiException {
    public InvalidCapacityRangeException() { super(ErrorCode.INVALID_CAPACITY_RANGE); }
}
