package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import com.example.domain.Item;
import com.example.domain.Topping;
import com.example.repository.ItemRepository;
import com.example.repository.ToppingRepository;

/**
 * ItemServiceの単体テストクラス。
 * {@link ItemService} の各メソッドについて、モックオブジェクトを使用してサービスロジックの動作確認を行う。
 * {@link MockitoExtension} を利用してRepositoryをモック化し、データベースにアクセスせずにService層の振る舞いを検証する。
 * 
 * @author Sho Toda
 */
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    /**
     * ItemRepositoryのモックオブジェクト。
     * Serviceから呼び出されるRepository処理をモック化する。
     */
    @Mock
    private ItemRepository itemRepository;

    /**
     * ToppingRepositoryのモックオブジェクト。
     */
    @Mock
    private ToppingRepository toppingRepository;

    /**
     * テスト対象のServiceクラス。
     * モックRepositoryが自動的に注入される。
     */
    @InjectMocks
    private ItemService itemService;

    /** テスト用商品データ1 */
    private Item item1;

    /** テスト用商品データ2 */
    private Item item2;

    /** テスト用トッピングデータ */
    private Topping topping1;

    /**
     * 各テスト実行前にテスト用データを初期化する。
     */
    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setId(1);
        item1.setName("カツカレー");
        
        item2 = new Item();
        item2.setId(2);
        item2.setName("ビーフカレー");

        topping1 = new Topping();
        topping1.setId(1);
        topping1.setName("チーズ");
    }

    /**
     * findAllメソッドのテスト。
     * 全商品を取得する処理が正しく動作するか確認する。
     */
    @Test
    @DisplayName("全商品検索のテスト")
    void testFindAll() {
        List<Item> itemList = Arrays.asList(item1, item2);
        when(itemRepository.findAll()).thenReturn(itemList);

        List<Item> result = itemService.findAll();

        assertEquals(2, result.size());
        assertEquals("カツカレー", result.get(0).getName());
        verify(itemRepository, times(1)).findAll();
    }

    /**
     * 商品名検索（名前指定あり）のテスト。
     * 指定した文字列を含む商品が取得できるか確認する。
     */
    @Test
    @DisplayName("商品名検索のテスト（名前指定あり）")
    void testFindByNameWithName() {
        List<Item> itemList = Arrays.asList(item1);
        when(itemRepository.findByName("カツ")).thenReturn(itemList);

        List<Item> result = itemService.findByName("カツ");

        assertEquals(1, result.size());
        assertEquals("カツカレー", result.get(0).getName());
        verify(itemRepository, times(1)).findByName("カツ");
    }

    /**
     * 商品名検索（空文字）のテスト。
     * 検索文字列が空の場合、全商品検索が実行されることを確認する。
     */
    @Test
    @DisplayName("商品名検索のテスト（名前が空文字の場合は全件検索される）")
    void testFindByNameWithEmptyName() {
        List<Item> itemList = Arrays.asList(item1, item2);
        when(itemRepository.findAll()).thenReturn(itemList);

        List<Item> result = itemService.findByName("");

        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAll();
        verify(itemRepository, never()).findByName(anyString());
    }

    /**
     * 商品名検索（null）のテスト。
     * 検索文字列がnullの場合、全商品検索が実行されることを確認する。
     */
    @Test
    @DisplayName("商品名検索のテスト（名前がnullの場合は全件検索される）")
    void testFindByNameWithNullName() {
        List<Item> itemList = Arrays.asList(item1, item2);
        when(itemRepository.findAll()).thenReturn(itemList);

        List<Item> result = itemService.findByName(null);

        assertEquals(2, result.size());
        verify(itemRepository, times(1)).findAll();
        verify(itemRepository, never()).findByName(anyString());
    }

    /**
     * 商品詳細取得のテスト。
     * 指定したIDの商品詳細が取得できるか確認する。
     */
    @Test
    @DisplayName("商品詳細取得のテスト")
    void testShowItemDetail() {
        // リファクタリング課題#2（対応漏れ修正）showItemDetail()はOptional<Item>を返すようになったため修正
        // when(itemRepository.showItemDetail(1)).thenReturn(item1);
        // Item result = itemService.showItemDetail(1);
        // assertNotNull(result);
        // assertEquals("カツカレー", result.getName());
        when(itemRepository.showItemDetail(1)).thenReturn(Optional.of(item1));

        Optional<Item> result = itemService.showItemDetail(1);

        assertTrue(result.isPresent());
        assertEquals("カツカレー", result.get().getName());
        verify(itemRepository, times(1)).showItemDetail(1);
    }

    /**
     * 全トッピング取得のテスト。
     * トッピング一覧が正しく取得できるか確認する。
     */
    @Test
    @DisplayName("全トッピング検索のテスト")
    void testFindAllTopping() {
        List<Topping> toppingList = Arrays.asList(topping1);
        when(toppingRepository.findAllTopping()).thenReturn(toppingList);

        List<Topping> result = itemService.findAllTopping();

        assertEquals(1, result.size());
        assertEquals("チーズ", result.get(0).getName());
        verify(toppingRepository, times(1)).findAllTopping();
    }

    /**
     * 全商品名取得のテスト。
     * 商品名一覧が取得できるか確認する。
     */
    @Test
    @DisplayName("全商品名取得のテスト")
    void testGetAllNames() {
        List<String> names = Arrays.asList("カツカレー", "ビーフカレー");
        when(itemRepository.getAllNames()).thenReturn(names);

        List<String> result = itemService.getAllNames();

        assertEquals(2, result.size());
        assertTrue(result.contains("カツカレー"));
        verify(itemRepository, times(1)).getAllNames();
    }

    /**
     * ページング処理のテスト。
     * 指定したページ番号と表示件数に応じて正しくデータが分割されるか確認する。
     */
    @Test
    @DisplayName("ページング処理のテスト")
    void testShowListPaging() {
        List<Item> itemList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setId(i);
            itemList.add(item);
        }

        Page<Item> resultPage = itemService.showListPaging(1, 3, itemList);

        assertEquals(3, resultPage.getContent().size());
        assertEquals(10, resultPage.getTotalElements());
        assertEquals(0, resultPage.getNumber());
        assertEquals(1, resultPage.getContent().get(0).getId());

        resultPage = itemService.showListPaging(4, 3, itemList);
        assertEquals(1, resultPage.getContent().size());
        assertEquals(10, resultPage.getContent().get(0).getId());
    }

    /**
     * ページング処理（範囲外ページ）のテスト。
     * 存在しないページ番号が指定された場合、空の結果が返却されることを確認する。
     */
    @Test
    @DisplayName("ページング処理のテスト（1ページぴったり）")
    void testShowListPaging_JustOnePage() {
        List<Item> itemList = Arrays.asList(item1, item2, new Item());
        Page<Item> resultPage = itemService.showListPaging(1, 3, itemList);
        assertEquals(3, resultPage.getContent().size());
        assertFalse(resultPage.hasNext());
    }

    @Test
    @DisplayName("ページング処理のテスト（最終ページが端数）")
    void testShowListPaging_LastPageHasFewerItems() {
        List<Item> itemList = Arrays.asList(item1, item2, new Item(), new Item());
        Page<Item> resultPage = itemService.showListPaging(2, 3, itemList);
        assertEquals(1, resultPage.getContent().size());
    }

    @Test
    @DisplayName("全トッピング検索のテスト（0件の場合）")
    void testFindAllTopping_Empty() {
        when(toppingRepository.findAllTopping()).thenReturn(new ArrayList<>());
        List<Topping> result = itemService.findAllTopping();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("全商品名取得のテスト（0件の場合）")
    void testGetAllNames_Empty() {
        when(itemRepository.getAllNames()).thenReturn(new ArrayList<>());
        List<String> result = itemService.getAllNames();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("商品詳細取得のテスト（存在しないID）")
    void testShowItemDetail_NotFound() {
        // リファクタリング課題#2（対応漏れ修正）showItemDetail()はOptional<Item>を返すようになったため修正
        // when(itemRepository.showItemDetail(999)).thenReturn(null);
        // Item result = itemService.showItemDetail(999);
        // assertNull(result);
        when(itemRepository.showItemDetail(999)).thenReturn(Optional.empty());
        Optional<Item> result = itemService.showItemDetail(999);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("商品名検索のテスト（結果なし）")
    void testFindByName_NoResult() {
        when(itemRepository.findByName("存在しない")).thenReturn(null);
        List<Item> result = itemService.findByName("存在しない");
        assertNull(result);
    }

    @Test
    @DisplayName("商品名検索のテスト（スペースのみの場合、RepositoryのfindByNameが呼ばれること）")
    void testFindByName_SpaceOnly() {
        when(itemRepository.findByName(" ")).thenReturn(new ArrayList<>());
        List<Item> result = itemService.findByName(" ");
        assertTrue(result.isEmpty());
        verify(itemRepository).findByName(" ");
    }

    @Test
    @DisplayName("ページング処理のテスト（開始位置がリストサイズを超える場合）")
    void testShowListPaging_OutOfBounds() {
        List<Item> itemList = Arrays.asList(item1, item2);
        // 1ページ3件の場合、2ページ目はインデックス3から。要素数2なので範囲外。
        Page<Item> resultPage = itemService.showListPaging(2, 3, itemList);
        
        assertTrue(resultPage.getContent().isEmpty());
        assertEquals(2, resultPage.getTotalElements());
    }
}
