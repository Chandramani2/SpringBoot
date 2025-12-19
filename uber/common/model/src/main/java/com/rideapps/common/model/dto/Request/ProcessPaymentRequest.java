package com.rideapps.common.model.dto.Request;

import com.rideapps.common.model.enums.PaymentMethod;
import lombok.Data;

@Data
public class ProcessPaymentRequest {
    private String tripId;
    private double amount;
    private PaymentMethod paymentMethod;
}
