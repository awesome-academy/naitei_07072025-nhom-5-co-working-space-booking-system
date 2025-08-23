package naitei.group5.workingspacebooking.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import naitei.group5.workingspacebooking.entity.Booking;
import naitei.group5.workingspacebooking.entity.Payment;
import naitei.group5.workingspacebooking.entity.enums.PaymentStatus;
import naitei.group5.workingspacebooking.repository.BookingRepository;
import naitei.group5.workingspacebooking.repository.PaymentRepository;
import naitei.group5.workingspacebooking.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;

    @Value("${vnpay.tmn-code}")
    private String vnpTmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnpHashSecret;

    @Value("${vnpay.pay-url}")
    private String vnpPayUrl;

    @Value("${vnpay.return-url}")
    private String vnpReturnUrl;

    @Override
    public String createPaymentUrl(Integer bookingId, HttpServletRequest request) throws Exception {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        long amount = Math.round(booking.getTotalAmount() * 100); // VNPAY yêu cầu nhân 100

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_Amount", String.valueOf(amount));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(System.currentTimeMillis()));
        params.put("vnp_OrderInfo", "Thanh toan booking " + bookingId);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpReturnUrl);
        params.put("vnp_IpAddr", request.getRemoteAddr());
        params.put("vnp_CreateDate", new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        // sort params
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = params.get(name);
            if (value != null && !value.isEmpty()) {
                hashData.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                query.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                    query.append('&');
                }
            }
        }

        String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnpPayUrl + "?" + query;
    }

    @Override
    public String handleVnpReturn(Map<String, String> params) throws Exception {
        // verify checksum
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = params.get(name);
            if (value != null && !value.isEmpty()) {
                hashData.append(name).append('=').append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        String checkHash = hmacSHA512(vnpHashSecret, hashData.toString());
        if (!checkHash.equals(vnpSecureHash)) {
            throw new RuntimeException("Invalid checksum from VNPAY");
        }

        // check response code
        String respCode = params.get("vnp_ResponseCode");
        String orderInfo = params.get("vnp_OrderInfo"); // "Thanh toan booking {id}"
        Integer bookingId = Integer.parseInt(orderInfo.replace("Thanh toan booking ", ""));

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        PaymentStatus status = "00".equals(respCode) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(BigDecimal.valueOf(booking.getTotalAmount()))
                .method("VNPAY")
                .paidTime(LocalDateTime.now())
                .status(status)
                .build();

        paymentRepo.save(payment);

        if (status == PaymentStatus.SUCCESS) {
            booking.setStatus(naitei.group5.workingspacebooking.entity.enums.BookingStatus.completed);
            bookingRepo.save(booking);
        }

        return status == PaymentStatus.SUCCESS ?
                "Thanh toán thành công cho booking " + bookingId :
                "Thanh toán thất bại cho booking " + bookingId;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKeySpec);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }
}
