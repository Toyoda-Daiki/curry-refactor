package com.example.event;

import org.springframework.context.ApplicationEvent;

/**
 * 注文完了イベント。
 * リファクタリング課題#7 Observerパターン：注文完了時の後続処理（メール送信等）を
 * OrderServiceから分離するため、イベントとして発行する情報をまとめたクラス。
 */
public class OrderCompletedEvent extends ApplicationEvent {

    private final String email;
    private final Integer orderId;

    public OrderCompletedEvent(Object source, String email, Integer orderId) {
        super(source);
        this.email = email;
        this.orderId = orderId;
    }

    public String getEmail() {
        return email;
    }

    public Integer getOrderId() {
        return orderId;
    }
}
