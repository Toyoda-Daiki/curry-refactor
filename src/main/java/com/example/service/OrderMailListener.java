package com.example.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.event.OrderCompletedEvent;

/**
 * 注文完了イベントのリスナー。
 * リファクタリング課題#7 Observerパターン：注文完了時のメール送信処理を
 * OrderControlerから分離し、イベント経由で呼び出す。
 */
@Component
public class OrderMailListener {

  private final OrderService orderService;

  public OrderMailListener(OrderService orderService) {
    this.orderService = orderService;
  }

  @EventListener
  public void onOrderCompleted(OrderCompletedEvent event) {
    orderService.sendMail(event.getEmail(), event.getOrderId());
  }
}
