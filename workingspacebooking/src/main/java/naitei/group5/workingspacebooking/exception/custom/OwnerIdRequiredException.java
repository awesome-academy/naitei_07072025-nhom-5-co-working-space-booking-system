package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class OwnerIdRequiredException extends ApiException {
    public OwnerIdRequiredException() { super(ErrorCode.OWNER_ID_REQUIRED); }
}
