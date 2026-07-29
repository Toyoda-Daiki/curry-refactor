package com.example.service.payment;

import org.springframework.stereotype.Component;

import com.example.domain.OrderStatus;
import com.example.domain.PaymentMethod;

@Component
public class CashOnDeliveryPaymentProcessor implements PaymentProcessor {
  @Override
  public PaymentMethod getSupportedMethod() {
    return PaymentMethod.CASH_ON_DELIVERY ;
  }

  @Override
  public OrderStatus judge(){
    return OrderStatus.CASH_ON_DELIVERY;
  }
}
