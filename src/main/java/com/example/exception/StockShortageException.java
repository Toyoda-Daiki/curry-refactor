package com.example.exception;

/**
 * 在庫不足が発生した際に投げられる例外クラス。
 */
public class StockShortageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StockShortageException(String message) {
        super(message);
    }
}
