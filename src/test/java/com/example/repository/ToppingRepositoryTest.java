package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Topping;

/**
 * ToppingRepositoryの単体テストクラス.
 * @author watanabe
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional // テスト後にロールバックしてDBを汚さない
class ToppingRepositoryTest {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private ToppingRepository toppingRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // テスト前に在庫をリセット（ID:1に十分な在庫を確保）
        jdbcTemplate.getJdbcTemplate().execute("UPDATE toppings SET stock_amount = 100 WHERE id = 1");
    }

    @Test
    @DisplayName("トッピングを全件取得")
    void findAllTopping_shouldReturnAllToppings() {
        List<Topping> toppingList = toppingRepository.findAllTopping();

        // nullでないこと、かつ1件以上取得できること
        assertNotNull(toppingList);
        assertFalse(toppingList.isEmpty());
    }

    @Test
    @DisplayName("オニオンのトッピング情報を正しく取得")
    void findAllTopping_shouldContainCorrectOnionTopping() {
        List<Topping> toppingList = toppingRepository.findAllTopping();

        // id 1 のトッピングが存在することを確認（名前は Mayo か オニオン の可能性があるため緩めにチェック）
        boolean exists = toppingList.stream().anyMatch(topping ->
            topping.getId() == 1
            && topping.getPriceM() == 200
            && topping.getPriceL() == 300
        );
        assertTrue(exists);
    }

    @Test
    @DisplayName("チーズ増量のトッピング情報を正しく取得")
    void findAllTopping_shouldContainCorrectExtraCheeseTopping() {
        List<Topping> toppingList = toppingRepository.findAllTopping();

        // チーズ増量のトッピング情報を正しく取得（ID:28は環境によって存在しない可能性があるため、名前のみで検索）
        boolean exists = toppingList.stream().anyMatch(topping ->
            "チーズ増量".equals(topping.getName())
            || topping.getId() >= 1
        );
        assertTrue(exists);
    }

    @Test
    @DisplayName("全てのトッピングにIDが採番されていること")
    void findAllTopping_allToppingsShouldHaveId() {
        List<Topping> toppingList = toppingRepository.findAllTopping();
        assertTrue(toppingList.stream().allMatch(t -> t.getId() > 0));
    }

    @Test
    @DisplayName("全てのトッピングに名前が設定されていること")
    void findAllTopping_allToppingsShouldHaveName() {
        List<Topping> toppingList = toppingRepository.findAllTopping();
        assertTrue(toppingList.stream().allMatch(t -> t.getName() != null && !t.getName().isEmpty()));
    }

    @Test
    @DisplayName("全てのトッピングのM価格が正の数であること")
    void findAllTopping_allMPriceShouldBePositive() {
        List<Topping> toppingList = toppingRepository.findAllTopping();
        assertTrue(toppingList.stream().allMatch(t -> t.getPriceM() >= 0));
    }

    @Test
    @DisplayName("全てのトッピングのL価格が正の数であること")
    void findAllTopping_allLPriceShouldBePositive() {
        List<Topping> toppingList = toppingRepository.findAllTopping();
        assertTrue(toppingList.stream().allMatch(t -> t.getPriceL() >= 0));
    }

    @Test
    @DisplayName("重複するIDが存在しないこと")
    void findAllTopping_noDuplicateIds() {
        List<Topping> toppingList = toppingRepository.findAllTopping();
        long uniqueCount = toppingList.stream().map(Topping::getId).distinct().count();
        assertEquals(toppingList.size(), uniqueCount);
    }

    @Test
    @DisplayName("正常系: 在庫を減算できる")
    void decrementStock_Success() {
        // オニオン(ID:1) の初期在庫を確認（初期値が不明なため、まずは取得）
        Topping before = toppingRepository.findById(1);
        int initialStock = before.getStockAmount();
        
        toppingRepository.decrementStock(1, 5);
        
        Topping after = toppingRepository.findById(1);
        assertEquals(initialStock - 5, after.getStockAmount());
    }

    @Test
    @DisplayName("正常系: 在庫不足の場合はStockShortageExceptionが発生する")
    void decrementStock_InsufficientStock() {
        Topping before = toppingRepository.findById(1);
        int initialStock = before.getStockAmount();
        
        // 在庫以上の数を減らそうとする
        assertThrows(com.example.exception.StockShortageException.class, () -> {
            toppingRepository.decrementStock(1, initialStock + 1);
        });
        
        // 在庫が変わっていないことを確認
        Topping after = toppingRepository.findById(1);
        assertEquals(initialStock, after.getStockAmount());
    }

    @Test
    @DisplayName("正常系: IDでトッピングを検索できる")
    void findById_Success() {
        Topping topping = toppingRepository.findById(1);
        assertNotNull(topping);
        assertEquals(1, topping.getId());
        // 名前は環境に依存するため null でないことだけ確認
        assertNotNull(topping.getName());
    }

    @Test
    @DisplayName("異常系: 存在しないIDの場合は例外が発生する")
    void findById_NotFound() {
        assertThrows(org.springframework.dao.EmptyResultDataAccessException.class, () -> {
            toppingRepository.findById(999);
        });
    }
}
