package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Cart;

/**
 * CartRepositoryのテストクラス.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE cart_toppings RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE cart_items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE carts RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("正常系: ユーザーIDでカートを検索できる")
    void findByUserId_Success() {
        jdbcTemplate.update("INSERT INTO carts (user_id, session_id) VALUES (?, ?)", 1, "session-123");
        
        Cart cart = cartRepository.findByUserId(1);
        
        assertNotNull(cart);
        assertEquals(1, cart.getUserId());
        assertEquals("session-123", cart.getSessionId());
    }

    @Test
    @DisplayName("正常系: ユーザーIDでカートが見つからない場合はnullを返す")
    void findByUserId_NotFound() {
        Cart cart = cartRepository.findByUserId(999);
        assertNull(cart);
    }

    @Test
    @DisplayName("正常系: セッションIDでカートを検索できる")
    void findBySessionId_Success() {
        jdbcTemplate.update("INSERT INTO carts (user_id, session_id) VALUES (?, ?)", null, "session-456");
        
        Cart cart = cartRepository.findBySessionId("session-456");
        
        assertNotNull(cart);
        assertNull(cart.getUserId());
        assertEquals("session-456", cart.getSessionId());
    }

    @Test
    @DisplayName("正常系: セッションIDでカートが見つからない場合はnullを返す")
    void findBySessionId_NotFound() {
        Cart cart = cartRepository.findBySessionId("non-existent");
        assertNull(cart);
    }

    @Test
    @DisplayName("正常系: カートを保存できる")
    void save_Success() {
        Cart cart = new Cart();
        cart.setUserId(2);
        cart.setSessionId("session-789");
        
        Cart savedCart = cartRepository.save(cart);
        
        assertNotNull(savedCart.getId());
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM carts WHERE id = ?", Integer.class, savedCart.getId());
        assertEquals(1, count);
    }

    @Test
    @DisplayName("正常系: 既存のカートを更新できる")
    void save_Update_Success() {
        // 1. 最初の一件を登録
        Cart cart = new Cart();
        cart.setUserId(10);
        cart.setSessionId("temp-session");
        Cart savedCart = cartRepository.save(cart);
        Integer id = savedCart.getId();
        
        // 2. フィールドを更新してsaveを呼ぶ（IDがセットされているのでUPDATE走るはず）
        savedCart.setUserId(20);
        savedCart.setSessionId("updated-session");
        cartRepository.save(savedCart);
        
        // 3. DBの値を検証
        Cart updatedCart = cartRepository.findByUserId(20);
        assertNotNull(updatedCart);
        assertEquals(id, updatedCart.getId());
        assertEquals("updated-session", updatedCart.getSessionId());
    }

    @Test
    @DisplayName("正常系: カートを削除できる")
    void deleteById_Success() {
        jdbcTemplate.update("INSERT INTO carts (user_id, session_id) VALUES (?, ?)", 3, "session-abc");
        Integer id = jdbcTemplate.queryForObject("SELECT id FROM carts WHERE session_id = 'session-abc'", Integer.class);
        
        cartRepository.deleteById(id);
        
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM carts WHERE id = ?", Integer.class, id);
        assertEquals(0, count);
    }
}
