package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.CartTopping;

/**
 * CartToppingRepositoryのテストクラス.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CartToppingRepositoryTest {

    @Autowired
    private CartToppingRepository cartToppingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE cart_toppings RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE cart_items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE carts RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE toppings RESTART IDENTITY CASCADE");

        // 外部参照用データの準備
        jdbcTemplate.update("INSERT INTO items (id, name, description, price_m, price_l, image_path, deleted) VALUES (?, ?, ?, ?, ?, ?, ?)",
                1, "テストカレー", "テスト説明", 1000, 1500, "test.png", false);
        jdbcTemplate.update("INSERT INTO toppings (id, name, price_m, price_l) VALUES (?, ?, ?, ?)",
                1, "テストトッピング", 200, 300);
        jdbcTemplate.update("INSERT INTO carts (id, user_id, session_id) VALUES (?, ?, ?)",
                1, 1, "session-abc");
        jdbcTemplate.update("INSERT INTO cart_items (id, cart_id, item_id, quantity, size) VALUES (?, ?, ?, ?, ?)",
                1, 1, 1, 1, "M");
    }

    @Test
    @DisplayName("正常系: カートトッピングを保存できる")
    void save_Success() {
        CartTopping cartTopping = new CartTopping();
        cartTopping.setCartItemId(1);
        cartTopping.setToppingId(1);
        
        cartToppingRepository.save(cartTopping);
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cart_toppings WHERE cart_item_id = ? AND topping_id = ?",
                Integer.class, 1, 1);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("正常系: カートアイテムIDによるトッピング検索ができる")
    void findByCartItemId_Success() {
        jdbcTemplate.update("INSERT INTO cart_toppings (cart_item_id, topping_id) VALUES (?, ?)", 1, 1);
        
        List<CartTopping> result = cartToppingRepository.findByCartItemId(1);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCartItemId());
        assertEquals(1, result.get(0).getToppingId());
        assertNotNull(result.get(0).getTopping());
        assertEquals("テストトッピング", result.get(0).getTopping().getName());
    }

    @Test
    @DisplayName("正常系: 存在しないカートアイテムIDの場合は空リストを返す")
    void findByCartItemId_Empty() {
        List<CartTopping> result = cartToppingRepository.findByCartItemId(999);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("正常系: カートアイテムIDによる全削除ができる")
    void deleteByCartItemId_Success() {
        jdbcTemplate.update("INSERT INTO cart_toppings (cart_item_id, topping_id) VALUES (?, ?)", 1, 1);
        
        cartToppingRepository.deleteByCartItemId(1);
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cart_toppings WHERE cart_item_id = ?",
                Integer.class, 1);
        assertEquals(0, count);
    }
}
