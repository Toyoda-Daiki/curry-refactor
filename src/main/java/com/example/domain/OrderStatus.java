package com.example.domain;

public enum OrderStatus {
    IN_CART(0),
    PAID(1),
    CASH_ON_DELIVERY(2);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * 支払い状況の表示ラベルを返す。
     * リファクタリング課題#1 追加対応：
     * View側でstatusの意味（数値の対応）を判断させると、
     * 意味の取り違え（未入金/入金済の逆転）が起きやすいため、
     * enum自身に振る舞いを持たせて一元管理する。
     */
    public String getPaymentStatusLabel() {
        return switch (this) {
            case PAID -> "入金済";
            case CASH_ON_DELIVERY -> "未入金（代引き）";
            case IN_CART -> "-";
        };
    }
}
