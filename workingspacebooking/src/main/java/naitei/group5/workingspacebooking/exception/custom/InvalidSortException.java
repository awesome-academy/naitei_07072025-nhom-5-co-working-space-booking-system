package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class InvalidSortException extends ApiException {
    public InvalidSortException() { super(ErrorCode.INVALID_SORT); }
}
