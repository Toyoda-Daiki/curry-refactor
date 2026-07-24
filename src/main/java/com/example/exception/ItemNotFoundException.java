package com.example.exception;

/**
 * 指定した商品が存在しない場合にスローする例外.
 */
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
