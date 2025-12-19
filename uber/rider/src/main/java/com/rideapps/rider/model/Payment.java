package com.rideapps.rider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rideapps.common.model.enums.PaymentMethod;
import com.rideapps.common.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private String paymentId;
    private String tripId;
    private double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime processedAt;
}