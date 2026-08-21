package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Item;

/**
 * ItemRepositoryの単体テストクラス.
 * 商品情報を操作するRepositoryクラスの各メソッドについて、データベースを使用した動作確認を行うテストクラスです。
 * {@code @SpringBootTest} によりSpringのコンテナを起動し、
 * {@code @Transactional} によりテスト終了後にトランザクションをロールバックすることでデータベースの状態を元に戻します。
 *
 * @author Sho Toda
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class ItemRepositoryTest {

    /**
     * ItemRepositoryのインスタンス.
     * SpringのDIコンテナにより自動的に注入され、商品情報のデータベース操作を行うために使用します。
     */
    @Autowired
    private ItemRepository itemRepository;

    /**
     * insertメソッドとfindAllメソッドのテスト.
     * 商品データを新規登録し、findAll()で取得した一覧に登録した商品が含まれていることを確認します。
     */
    @Test
    void testInsertAndFindAll() {
        Item item = new Item();
        item.setName("テストカレー");
        item.setDescription("テスト用の美味しいカレーです");
        item.setPriceM(1000);
        item.setPriceL(2000);
        item.setImagePath("test.jpg");
        item.setDeleted(false);

        itemRepository.insert(item);

        List<Item> itemList = itemRepository.findAll();
        assertNotNull(itemList, "商品リストがnullであってはなりません");
        assertTrue(itemList.size() > 0, "商品リストが空であってはなりません");

        boolean found = false;
        for (Item i : itemList) {
            if ("テストカレー".equals(i.getName())) {
                found = true;
                assertEquals("テスト用の美味しいカレーです", i.getDescription());
                assertEquals(1000, i.getPriceM());
                assertEquals(2000, i.getPriceL());
                break;
            }
        }
        assertTrue(found, "挿入した商品がfindAllの結果に含まれている必要があります");
    }

    /**
     * findByNameメソッドのテスト（部分一致検索）.
     * 商品名に指定した文字列を含む商品が正しく取得できることを確認します。
     */
    @Test
    void testFindByName() {
        List<Item> itemList = itemRepository.findByName("カツ");
        assertNotNull(itemList, "カツを含む商品が見つかるはずです");
        assertTrue(itemList.size() >= 1, "少なくとも1つの商品が見つかるはずです");

        boolean foundKatsu = false;
        for (Item item : itemList) {
            if (item.getName().contains("カツ")) {
                foundKatsu = true;
                break;
            }
        }
        assertTrue(foundKatsu, "検索結果に「カツ」を含む商品が含まれている必要があります");
    }

    /**
     * findByNameメソッドのテスト（検索結果なし）.
     * 存在しない商品名で検索した場合に、nullが返却されることを確認します。
     */
    @Test
    void testFindByNameNotFound() {
        List<Item> itemList = itemRepository.findByName("存在しない商品名XXXXX");
        // リファクタリング課題#2（対応漏れ修正）findByName()は0件時にnullではなく空リストを返すようになったため修正
        // assertNull(itemList, "見つからない場合はnullが返る必要があります");
        assertNotNull(itemList, "見つからない場合でもnullではなく空リストが返る必要があります");
        assertTrue(itemList.isEmpty(), "見つからない場合は空リストが返る必要があります");
    }

    /**
     * showItemDetailメソッドのテスト.
     * 商品データを登録した後、そのIDを使用して詳細情報が正しく取得できることを確認します。
     */
    @Test
    void testShowItemDetail() {
        Item item = new Item();
        item.setName("詳細テストカレー");
        item.setDescription("詳細表示テスト用");
        item.setPriceM(1200);
        item.setPriceL(2200);
        item.setImagePath("detail.jpg");
        item.setDeleted(false);
        itemRepository.insert(item);

        List<Item> itemList = itemRepository.findByName("詳細テストカレー");
        assertNotNull(itemList);
        Integer id = itemList.get(0).getId();

        // リファクタリング課題#2（対応漏れ修正）showItemDetail()はOptional<Item>を返すようになったため修正
        // Item foundItem = itemRepository.showItemDetail(id);
        // assertNotNull(foundItem, "IDによる詳細検索で商品が見つかる必要があります");
        Optional<Item> foundItemOpt = itemRepository.showItemDetail(id);
        assertTrue(foundItemOpt.isPresent(), "IDによる詳細検索で商品が見つかる必要があります");
        Item foundItem = foundItemOpt.get();
        assertEquals("詳細テストカレー", foundItem.getName());
        assertEquals("詳細表示テスト用", foundItem.getDescription());
        assertEquals(1200, foundItem.getPriceM());
        assertEquals(2200, foundItem.getPriceL());
        assertEquals("detail.jpg", foundItem.getImagePath());
    }

    /**
     * findAllメソッドのテスト.
     * 削除済み商品が一覧取得結果に含まれないことを確認します。
     */
    @Test
    void testFindAll_ExcludeDeleted() {
        Item item = new Item();
        item.setName("削除済みカレー");
        item.setDescription("削除済み");
        item.setPriceM(999);
        item.setPriceL(1999);
        item.setImagePath("deleted.jpg");
        item.setDeleted(true);
        itemRepository.insert(item);

        List<Item> itemList = itemRepository.findAll();
        boolean found = itemList.stream().anyMatch(i -> "削除済みカレー".equals(i.getName()));
        assertFalse(found, "削除済み商品はfindAllの結果に含まれてはなりません");
    }

    /**
     * findByNameメソッドのテスト.
     * 削除済み商品が名前検索結果に含まれないことを確認します。
     */
    @Test
    void testFindByName_ExcludeDeleted() {
        Item item = new Item();
        item.setName("検索除外カレー");
        item.setDescription("削除済み");
        item.setPriceM(888);
        item.setPriceL(1888);
        item.setImagePath("exclude.jpg");
        item.setDeleted(true);
        itemRepository.insert(item);

        List<Item> itemList = itemRepository.findByName("検索除外");
        if (itemList != null) {
            boolean found = itemList.stream().anyMatch(i -> "検索除外カレー".equals(i.getName()));
            assertFalse(found, "削除済み商品はfindByNameの結果に含まれてはなりません");
        }
    }

    /**
     * findByNameメソッドのテスト.
     * 同じ検索文字列に一致する商品が複数件取得できることを確認します。
     */
    @Test
    void testFindByName_MultipleResults() {
        for (int i = 0; i < 3; i++) {
            Item item = new Item();
            item.setName("共通名グループ" + i);
            item.setDescription("複数件テスト" + i);
            item.setPriceM(100 + i);
            item.setPriceL(200 + i);
            item.setImagePath("multi" + i + ".jpg");
            item.setDeleted(false);
            itemRepository.insert(item);
        }

        List<Item> itemList = itemRepository.findByName("共通名グループ");
        assertNotNull(itemList);
        assertTrue(itemList.size() >= 3);
    }

    /**
     * 削除済み商品は名前検索にヒットしないことを確認します。
     * ※ showItemDetail の挙動確認ではなく、findByName の削除済み除外確認です。
     */
    // リファクタリング課題#2（対応漏れ修正）findByName()は0件時にnullではなく空リストを返すようになったためメソッド名・内容を修正
    // @Test
    // void testFindByName_DeletedItemReturnsNull() {
    @Test
    void testFindByName_DeletedItemReturnsEmptyList() {
        Item item = new Item();
        item.setName("詳細用削除済み");
        item.setDescription("削除済み");
        item.setPriceM(500);
        item.setPriceL(700);
        item.setImagePath("deleted-detail.jpg");
        item.setDeleted(true);
        itemRepository.insert(item);

        List<Item> itemList = itemRepository.findByName("詳細用削除済み");
        // assertNull(itemList, "削除済みは名前検索にヒットしないはず");
        assertNotNull(itemList, "削除済みは名前検索にヒットしないはず（nullではなく空リスト）");
        assertTrue(itemList.isEmpty(), "削除済みは名前検索にヒットしないはず（空リスト）");
    }

    /**
     * getAllNamesメソッドのテスト.
     * 削除済み商品の名前が一覧に含まれないことを確認します。
     */
    @Test
    void testGetAllNames_ExcludeDeleted() {
        Item item = new Item();
        item.setName("名前除外カレー");
        item.setDescription("削除済み");
        item.setPriceM(777);
        item.setPriceL(1777);
        item.setImagePath("name-exclude.jpg");
        item.setDeleted(true);
        itemRepository.insert(item);

        List<String> names = itemRepository.getAllNames();
        assertFalse(names.contains("名前除外カレー"), "削除済み商品の名前はgetAllNamesに含まれてはなりません");
    }

    /**
     * insertメソッドのテスト.
     * 全フィールドを設定して登録し、正常に検索できることを確認します。
     */
    @Test
    void testInsert_WithFullFields() {
        Item item = new Item();
        item.setName("フルフィールドカレー");
        item.setDescription("全部入り");
        item.setPriceM(1500);
        item.setPriceL(2500);
        item.setImagePath("full.png");
        item.setDeleted(false);

        itemRepository.insert(item);

        List<Item> itemList = itemRepository.findByName("フルフィールド");
        assertNotNull(itemList);
        assertEquals(1, itemList.size());
        assertEquals("フルフィールドカレー", itemList.get(0).getName());
        assertEquals("全部入り", itemList.get(0).getDescription());
        assertEquals(1500, itemList.get(0).getPriceM());
        assertEquals(2500, itemList.get(0).getPriceL());
        assertEquals("full.png", itemList.get(0).getImagePath());
    }

    /**
     * findByNameメソッドのテスト.
     * 部分一致検索ができることを確認します。
     */
    @Test
    void testFindByName_PartialMatch() {
        Item item = new Item();
        item.setName("あいうえおカレー");
        item.setDescription("部分一致用");
        item.setPriceM(600);
        item.setPriceL(800);
        item.setImagePath("partial.jpg");
        item.setDeleted(false);
        itemRepository.insert(item);

        List<Item> result = itemRepository.findByName("うえお");
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(i -> "あいうえおカレー".equals(i.getName())));
    }

    /**
     * findAllメソッドのテスト.
     * price_mの昇順で並んでいることを確認します。
     */
    @Test
    void testFindAll_OrderByPriceMAsc() {
        Item item1 = new Item();
        item1.setName("並び順確認商品A");
        item1.setDescription("高い");
        item1.setPriceM(900);
        item1.setPriceL(1100);
        item1.setImagePath("a.jpg");
        item1.setDeleted(false);
        itemRepository.insert(item1);

        Item item2 = new Item();
        item2.setName("並び順確認商品B");
        item2.setDescription("安い");
        item2.setPriceM(300);
        item2.setPriceL(500);
        item2.setImagePath("b.jpg");
        item2.setDeleted(false);
        itemRepository.insert(item2);

        Item item3 = new Item();
        item3.setName("並び順確認商品C");
        item3.setDescription("中間");
        item3.setPriceM(600);
        item3.setPriceL(800);
        item3.setImagePath("c.jpg");
        item3.setDeleted(false);
        itemRepository.insert(item3);

        List<Item> result = itemRepository.findAll();

        List<Item> targets = result.stream()
                .filter(i -> i.getName().startsWith("並び順確認商品"))
                .collect(Collectors.toList());

        assertEquals(3, targets.size());
        assertEquals("並び順確認商品B", targets.get(0).getName());
        assertEquals("並び順確認商品C", targets.get(1).getName());
        assertEquals("並び順確認商品A", targets.get(2).getName());
    }

    /**
     * findByNameメソッドのテスト.
     * price_mの昇順で並んでいることを確認します。
     */
    @Test
    void testFindByName_OrderByPriceMAsc() {
        Item item1 = new Item();
        item1.setName("検索並び商品");
        item1.setDescription("高い");
        item1.setPriceM(1000);
        item1.setPriceL(1200);
        item1.setImagePath("x1.jpg");
        item1.setDeleted(false);
        itemRepository.insert(item1);

        Item item2 = new Item();
        item2.setName("検索並び商品");
        item2.setDescription("安い");
        item2.setPriceM(400);
        item2.setPriceL(600);
        item2.setImagePath("x2.jpg");
        item2.setDeleted(false);
        itemRepository.insert(item2);

        Item item3 = new Item();
        item3.setName("検索並び商品");
        item3.setDescription("中間");
        item3.setPriceM(700);
        item3.setPriceL(900);
        item3.setImagePath("x3.jpg");
        item3.setDeleted(false);
        itemRepository.insert(item3);

        List<Item> result = itemRepository.findByName("検索並び商品");

        assertNotNull(result);
        assertTrue(result.size() >= 3);

        List<Item> targets = result.stream()
                .filter(i -> "検索並び商品".equals(i.getName()))
                .collect(Collectors.toList());

        assertEquals(3, targets.size());
        assertEquals(400, targets.get(0).getPriceM());
        assertEquals(700, targets.get(1).getPriceM());
        assertEquals(1000, targets.get(2).getPriceM());
    }

    /**
     * getAllNamesメソッドの正常系テスト.
     * 登録済みの商品名が取得結果に含まれることを確認します。
     */
    @Test
    void testGetAllNames() {
        Item item = new Item();
        item.setName("名前一覧確認カレー");
        item.setDescription("名前一覧テスト");
        item.setPriceM(850);
        item.setPriceL(1050);
        item.setImagePath("names.jpg");
        item.setDeleted(false);
        itemRepository.insert(item);

        List<String> names = itemRepository.getAllNames();

        assertNotNull(names, "商品名一覧がnullであってはなりません");
        assertFalse(names.isEmpty(), "商品名一覧が空であってはなりません");
        assertTrue(names.contains("名前一覧確認カレー"), "登録した商品名が一覧に含まれている必要があります");
    }

    /**
     * showItemDetailメソッドの異常系テスト.
     * 存在しないIDを指定した場合にOptional.empty()が返ることを確認します。
     * リファクタリング課題#39/#2（対応漏れ修正）：
     * queryForObject()（EmptyResultDataAccessExceptionを投げる実装）から
     * query()+Optionalに変更されたため、期待する結果を合わせて修正。
     */
    @Test
    void testShowItemDetail_NotFound() {
        // assertThrows(EmptyResultDataAccessException.class,
        //         () -> itemRepository.showItemDetail(999999));
        Optional<Item> result = itemRepository.showItemDetail(999999);
        assertTrue(result.isEmpty(), "存在しないIDの場合はOptional.empty()が返る必要があります");
    }
}
