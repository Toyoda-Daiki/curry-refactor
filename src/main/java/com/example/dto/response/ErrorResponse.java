package com.example.dto.response;

/**
 * 共通エラーレスポンスDTO.
 * API仕様書の ErrorResponse スキーマに対応
 */
public class ErrorResponse {

    /** HTTPステータスコード */
    private Integer status;

    /** エラーメッセージ */
    private String message;

    public ErrorResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
