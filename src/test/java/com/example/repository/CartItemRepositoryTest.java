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

import com.example.domain.CartItem;

/**
 * CartItemRepositoryのテストクラス.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE cart_toppings RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE cart_items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE carts RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE items RESTART IDENTITY CASCADE");

        // 外部参照用データの準備
        jdbcTemplate.update("INSERT INTO items (id, name, description, price_m, price_l, image_path, deleted) VALUES (?, ?, ?, ?, ?, ?, ?)",
                1, "テストカレー", "テスト説明", 1000, 1500, "test.png", false);
        jdbcTemplate.update("INSERT INTO carts (id, user_id, session_id) VALUES (?, ?, ?)",
                1, 1, "session-abc");
    }

    @Test
    @DisplayName("正常系: カートIDによる検索ができる")
    void findByCartId_Success() {
        jdbcTemplate.update("INSERT INTO cart_items (cart_id, item_id, quantity, size) VALUES (?, ?, ?, ?)",
                1, 1, 2, "M");
        
        List<CartItem> result = cartItemRepository.findByCartId(1);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getCartId());
        assertEquals(1, result.get(0).getItemId());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals("M", result.get(0).getSize());
        assertEquals("テストカレー", result.get(0).getName());
        assertEquals(1000, result.get(0).getItemPrice());
    }

    @Test
    @DisplayName("正常系: 存在しないカートIDの場合は空リストを返す")
    void findByCartId_Empty() {
        List<CartItem> result = cartItemRepository.findByCartId(999);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("正常系: カートアイテムを新規保存できる")
    void save_Insert_Success() {
        CartItem cartItem = new CartItem();
        cartItem.setCartId(1);
        cartItem.setItemId(1);
        cartItem.setQuantity(3);
        cartItem.setSize("L");
        
        CartItem savedItem = cartItemRepository.save(cartItem);
        
        assertNotNull(savedItem.getId());
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cart_items WHERE id = ?", Integer.class, savedItem.getId());
        assertEquals(1, count);
    }

    @Test
    @DisplayName("正常系: カートアイテムを更新保存できる")
    void save_Update_Success() {
        jdbcTemplate.update("INSERT INTO cart_items (cart_id, item_id, quantity, size) VALUES (?, ?, ?, ?)",
                1, 1, 1, "M");
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM cart_items WHERE quantity = 1", Integer.class);
        
        CartItem cartItem = new CartItem();
        cartItem.setId(id);
        cartItem.setQuantity(5);
        cartItem.setSize("L");
        
        cartItemRepository.save(cartItem);
        
        Integer quantity = jdbcTemplate.queryForObject("SELECT quantity FROM cart_items WHERE id = ?", Integer.class, id);
        assertEquals(5, quantity);
        String size = jdbcTemplate.queryForObject("SELECT size FROM cart_items WHERE id = ?", String.class, id);
        assertEquals("L", size);
    }

    @Test
    @DisplayName("正常系: IDによる削除ができる")
    void deleteById_Success() {
        jdbcTemplate.update("INSERT INTO cart_items (cart_id, item_id, quantity, size) VALUES (?, ?, ?, ?)",
                1, 1, 1, "M");
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM cart_items WHERE cart_id = 1", Integer.class);
        
        cartItemRepository.deleteById(id);
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cart_items WHERE id = ?", Integer.class, id);
        assertEquals(0, count);
    }
}
