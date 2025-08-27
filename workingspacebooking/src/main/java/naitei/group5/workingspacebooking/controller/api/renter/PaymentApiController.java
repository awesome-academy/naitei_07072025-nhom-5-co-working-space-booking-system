package naitei.group5.workingspacebooking.controller.api.renter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import naitei.group5.workingspacebooking.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;
    @Value("${frontend.url}")
    private String frontendUrl;

    // Tạo link thanh toán
    @PostMapping("/{bookingId}")
    public Map<String, String> createPayment(@PathVariable Integer bookingId, HttpServletRequest request) throws Exception {
        String url = paymentService.createPaymentUrl(bookingId, request);
        return Map.of("paymentUrl", url);
    }

    // Xử lý khi VNPAY redirect về
    @GetMapping("/vnpay-return")
    public void vnpayReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws Exception {
        String resultMessage = paymentService.handleVnpReturn(params);

        // Redirect sang frontend React
        String redirectUrl = frontendUrl + "/payment/result?msg=" +
                URLEncoder.encode(resultMessage, StandardCharsets.UTF_8);


        response.sendRedirect(redirectUrl);
    }
}
