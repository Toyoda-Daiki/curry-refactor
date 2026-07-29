package com.example.domain;

public enum PaymentMethod {
  CASH_ON_DELIVERY(1),
  CREDIT_CARD(2);

  private final int value;

  PaymentMethod(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public static PaymentMethod fromValue(Integer value) {
    for (PaymentMethod method : PaymentMethod.values()) {
      if (method.getValue() == value) {
        return method;
      }
    }
    throw new IllegalArgumentException("不正な支払い方法です: " + value);
  }
}
