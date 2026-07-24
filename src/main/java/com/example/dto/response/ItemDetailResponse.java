package com.example.dto.response;

import java.util.List;

/**
 * 商品詳細取得APIのレスポンスDTO.
 * GET /api/items/{id}
 */
public class ItemDetailResponse {

    /** 商品ID */
    private Integer id;

    /** 商品名 */
    private String name;

    /** 商品説明 */
    private String description;

    /** Mサイズの価格（円） */
    private Integer priceM;

    /** Lサイズの価格（円） */
    private Integer priceL;

    /** 商品画像の相対パス */
    private String imagePath;

    /** トッピング一覧（全商品共通のトッピングマスタから取得） */
    private List<ToppingResponse> toppings;

    public ItemDetailResponse() {
    }

    public ItemDetailResponse(Integer id, String name, String description,
            Integer priceM, Integer priceL, String imagePath, List<ToppingResponse> toppings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceM = priceM;
        this.priceL = priceL;
        this.imagePath = imagePath;
        this.toppings = toppings;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public List<ToppingResponse> getToppings() {
        return toppings;
    }
}
