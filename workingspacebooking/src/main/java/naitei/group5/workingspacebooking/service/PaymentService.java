package naitei.group5.workingspacebooking.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    String createPaymentUrl(Integer bookingId, HttpServletRequest request) throws Exception;
    String handleVnpReturn(Map<String, String> params) throws Exception;
}
