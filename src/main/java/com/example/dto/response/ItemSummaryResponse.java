package com.example.dto.response;

/**
 * 商品一覧取得APIのレスポンスDTO
 * GET /api/items
 */
public class ItemSummaryResponse {

    /** 商品ID */
    private Integer id;

    /** 商品名 */
    private String name;

    /** Mサイズの価格（円） */
    private Integer priceM;

    /** Lサイズの価格（円） */
    private Integer priceL;

    /** 商品画像の相対パス */
    private String imagePath;

    public ItemSummaryResponse(Integer id, String name, Integer priceM, Integer priceL, String imagePath) {
        this.id = id;
        this.name = name;
        this.priceM = priceM;
        this.priceL = priceL;
        this.imagePath = imagePath;
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

    public String getImagePath() {
        return imagePath;
    }
}
