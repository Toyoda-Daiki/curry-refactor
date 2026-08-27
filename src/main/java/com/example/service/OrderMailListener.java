package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(OrderMailListener.class);
  private final OrderService orderService;

  public OrderMailListener(OrderService orderService) {
    this.orderService = orderService;
  }

  @EventListener
  public void onOrderCompleted(OrderCompletedEvent event) {
    // orderService.sendMail(event.getEmail(), event.getOrderId());

    // リファクタリング課題#7　メール送信失敗が注文確定処理に影響しないよう、例外はここでcatchしログにのみ記録する
    try {
      orderService.sendMail(event.getEmail(), event.getOrderId());
    } catch (Exception e) {
      log.error("注文完了メールの送信に失敗しました: orderId={}, email={}",
          event.getOrderId(), event.getEmail(), e);
    }
  }
}
