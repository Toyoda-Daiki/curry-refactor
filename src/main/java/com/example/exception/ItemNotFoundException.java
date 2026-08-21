package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 指定した商品が存在しない場合にスローする例外.
 *
 * リファクタリング課題#2/#39（対応漏れ修正）：
 * REST API（com.example.api配下）はApiExceptionHandlerが処理するが、
 * MVC側（com.example.controller配下）には対応するControllerAdviceがないため、
 * @ResponseStatusでデフォルトの404マッピングを持たせ、
 * templates/error/4xx.htmlに解決されるようにする。
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
