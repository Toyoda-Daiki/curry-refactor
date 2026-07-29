package com.example.service.payment;

import org.springframework.stereotype.Component;

import com.example.domain.OrderStatus;
import com.example.domain.PaymentMethod;

@Component
public class CreditCardPaymentProcessor implements PaymentProcessor {
  @Override
  public PaymentMethod getSupportedMethod() {
    return PaymentMethod.CREDIT_CARD;
  }

  @Override
  public OrderStatus judge(){
    return OrderStatus.PAID;
  }
}
