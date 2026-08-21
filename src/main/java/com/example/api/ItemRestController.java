package com.example.api;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.Item;
import com.example.domain.Topping;
import com.example.exception.ItemNotFoundException;
import com.example.dto.response.ItemDetailResponse;
import com.example.dto.response.ItemSummaryResponse;
import com.example.dto.response.ToppingResponse;
import com.example.service.ItemService;

import jakarta.validation.constraints.Min;

/**
 * 商品情報REST API コントローラー.
 * 外部システム連携用の参照専用API（GETのみ）.
 * 
 * @author watanabe
 */
@RestController
@RequestMapping("/api")
@Validated
public class ItemRestController {

    /** ログ出力用Logger */
    private static final Logger log = LoggerFactory.getLogger(ItemRestController.class);

    @Autowired
    private ItemService itemService;

    /**
     * 商品一覧取得・検索
     * GET /api/items
     *
     * @param name 商品名（部分一致検索、任意）。未指定または空文字列の場合は全件返却
     * @return 商品概要のリスト（該当なしの場合は空配列）
     */
    @GetMapping("/items")
    public ResponseEntity<List<ItemSummaryResponse>> getItems(
            @RequestParam(required = false) String name) {

        log.info("商品一覧取得API: name={}", name);

        // nameが未指定または空文字列の場合、ItemService#findByName()内部で全件取得に切り替わる
        List<Item> itemList = itemService.findByName(name);

        // ドメインオブジェクト → レスポンス用DTOに変換
        List<ItemSummaryResponse> responseList = itemList.stream()
                .map(item -> new ItemSummaryResponse(
                        item.getId(),
                        item.getName(),
                        item.getPriceM(),
                        item.getPriceL(),
                        item.getImagePath()))
                .toList();

        log.info("商品一覧取得API完了: 件数={}", responseList.size());
        return ResponseEntity.ok(responseList);
    }

    /**
     * 商品詳細取得
     * GET /api/items/{id}
     *
     * @param id 取得する商品のID（1以上の整数）
     * @return 商品詳細情報（トッピング一覧含む）。存在しない場合は404
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<ItemDetailResponse> getItemById(
            @PathVariable @Min(1) int id) {

        log.info("商品詳細取得API: id={}", id);

        // 商品詳細を取得（論理削除済みはnullが返る想定）
        // Item item = itemService.showItemDetail(id);

        // 商品詳細を取得（存在しない場合はOptional.empty()）
        // リファクタリング課題#2（対応漏れ修正）Optional<Item>で受け取る
        Optional<Item> itemOpt = itemService.showItemDetail(id);

        // if (item == null) {
        Item item = itemOpt.orElseThrow(() -> {
            log.warn("商品詳細取得API: 商品が存在しない id={}", id);
            // 404はExceptionHandlerで処理するため例外をスロー
            // throw new ItemNotFoundException("指定した商品が存在しません");
            return new ItemNotFoundException("指定した商品が存在しません");
        });

        // 全商品共通のトッピングマスタを取得（ItemServiceのfindAllTopping()で一元管理）
        List<Topping> toppingList = itemService.findAllTopping();

        // トッピング → レスポンス用DTOに変換
        List<ToppingResponse> toppingResponseList = toppingList.stream()
                .map(topping -> new ToppingResponse(
                        topping.getId(),
                        topping.getName(),
                        topping.getPriceM(),
                        topping.getPriceL()))
                .toList();

        // 商品詳細 → レスポンス用DTOに変換
        ItemDetailResponse response = new ItemDetailResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPriceM(),
                item.getPriceL(),
                item.getImagePath(),
                toppingResponseList);

        log.info("商品詳細取得API完了: id={}", id);
        return ResponseEntity.ok(response);
    }
}
