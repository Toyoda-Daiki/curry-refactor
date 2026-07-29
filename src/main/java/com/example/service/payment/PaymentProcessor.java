package com.example.service.payment;

import com.example.domain.OrderStatus;
import com.example.domain.PaymentMethod;

/**
 * 支払い方法ごとの処理を表すインターフェース。
 * リファクタリング課題#6 Strategyパターンで支払い方法を拡張可能にする
 */
public interface PaymentProcessor {

  /**
   * 自分がどの支払い方法(PaymentMethod)を担当するかを返す。
   */
  PaymentMethod getSupportedMethod();

  /**
   * この支払い方法の場合の注文ステータスを返す。
   */
  OrderStatus judge();
}
