package com.example.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.dto.response.ErrorResponse;

/**
 * REST API 共通例外ハンドラー.
 * API仕様書で定義したエラーレスポンス形式（status, message）に統一する.
 * 
 * @author watanabe
 */
@RestControllerAdvice(basePackages = "com.example.api")
public class ApiExceptionHandler {

    /** ログ出力用Logger */
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * 404 Not Found
     * 商品が存在しない、または論理削除済みの場合
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFound(ItemNotFoundException e) {
        log.warn("404 Not Found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(404, e.getMessage()));
    }

    /**
     * 500 Internal Server Error
     * 想定外例外、DB接続障害など
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("500 Internal Server Error", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(500, "サーバー内部エラーが発生しました"));
    }
}
