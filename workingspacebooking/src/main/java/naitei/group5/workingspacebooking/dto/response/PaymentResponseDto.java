package naitei.group5.workingspacebooking.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import naitei.group5.workingspacebooking.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDto(
        BigDecimal amount,
        String method,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime paidTime,
        PaymentStatus status
) {}
