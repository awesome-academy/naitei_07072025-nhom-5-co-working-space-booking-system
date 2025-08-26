package naitei.group5.workingspacebooking.exception.custom;

import naitei.group5.workingspacebooking.exception.ApiException;
import naitei.group5.workingspacebooking.exception.ErrorCode;

public class PriceRuleNotFoundException extends ApiException {
    public PriceRuleNotFoundException() {
        super(ErrorCode.PRICE_RULE_NOT_FOUND);
    }
}
