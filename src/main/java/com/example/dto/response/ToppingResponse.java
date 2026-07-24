package com.example.dto.response;

/**
 * トッピング情報のレスポンスDTO.
 * 商品詳細取得API（GET /api/items/{id}）のレスポンスに含まれる
 */
public class ToppingResponse {

    /** トッピングID */
    private Integer id;

    /** トッピング名 */
    private String name;

    /** Mサイズの価格（円） */
    private Integer priceM;

    /** Lサイズの価格（円） */
    private Integer priceL;

    public ToppingResponse() {
    }

    public ToppingResponse(Integer id, String name, Integer priceM, Integer priceL) {
        this.id = id;
        this.name = name;
        this.priceM = priceM;
        this.priceL = priceL;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPriceM() {
        return priceM;
    }

    public Integer getPriceL() {
        return priceL;
    }
}
