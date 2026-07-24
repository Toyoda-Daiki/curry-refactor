package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.OrderItem;

/**
 * OrderItemRepositoryをテストするクラス.
 * 
 * @author watanabe
 */
@SpringBootTest
@Transactional // DBに保存されないようにつける
@ActiveProfiles("test")
public class OrderItemRepositoryTest {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        jdbcTemplate.execute("TRUNCATE TABLE order_toppings RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE order_items RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE orders RESTART IDENTITY CASCADE");

        // order_items の外部キー制約を満たすため、orders に親データを入れておく
        jdbcTemplate.update(
            "INSERT INTO orders ("
                + "id, user_id, status, total_price, order_date, "
                + "destination_name, destination_email, destination_zipcode, "
                + "destination_address, destination_tel, delivery_time, payment_method"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            1,
            1,
            1,
            1000,
            Date.valueOf(LocalDate.now()),
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    @Test
    @DisplayName("正常系: 異なるサイズ（M）で注文商品を登録できる")
    void order_sizeM() {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(501);
        orderItem.setOrderId(1);
        orderItem.setQuantity(1);
        orderItem.setSize("M");

        Integer resultId = orderItemRepository.order(orderItem);

        assertNotNull(resultId);
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM order_items WHERE id = ? AND size = 'M'", Integer.class, resultId);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("正常系: 異なるサイズ（S）で注文商品を登録できる")
    void order_sizeS() {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(502);
        orderItem.setOrderId(1);
        orderItem.setQuantity(1);
        orderItem.setSize("S");

        Integer resultId = orderItemRepository.order(orderItem);

        assertNotNull(resultId);
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM order_items WHERE id = ? AND size = 'S'", Integer.class, resultId);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("正常系: 大量（100個）の注文商品を登録できる")
    void order_largeQuantity() {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(503);
        orderItem.setOrderId(1);
        orderItem.setQuantity(100);
        orderItem.setSize("L");

        Integer resultId = orderItemRepository.order(orderItem);

        assertNotNull(resultId);
        Integer quantity = jdbcTemplate.queryForObject(
            "SELECT quantity FROM order_items WHERE id = ?", Integer.class, resultId);
        assertEquals(100, quantity);
    }

    @Test
    @DisplayName("正常系: 同じ注文IDで複数の注文商品を登録できる")
    void order_multipleItemsForSameOrder() {
        OrderItem item1 = new OrderItem();
        item1.setItemId(504);
        item1.setOrderId(1);
        item1.setQuantity(1);
        item1.setSize("M");
        orderItemRepository.order(item1);

        OrderItem item2 = new OrderItem();
        item2.setItemId(505);
        item2.setOrderId(1);
        item2.setQuantity(2);
        item2.setSize("L");
        orderItemRepository.order(item2);

        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM order_items WHERE order_id = 1", Integer.class);
        assertEquals(3, count); // setUpで1つ入れているので合計3
    }

    @Test
    @DisplayName("正常系: 数量が0の注文商品を登録できる（ドメインルールに依存するが、DB上は許可される想定）")
    void order_zeroQuantity() {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(506);
        orderItem.setOrderId(1);
        orderItem.setQuantity(0);
        orderItem.setSize("L");

        Integer resultId = orderItemRepository.order(orderItem);

        assertNotNull(resultId);
        Integer quantity = jdbcTemplate.queryForObject(
            "SELECT quantity FROM order_items WHERE id = ?", Integer.class, resultId);
        assertEquals(0, quantity);
    }

    @Test
    @DisplayName("正常系: 非常に長いサイズ文字列を登録できる（DB上限に依存するがテストとして追加）")
    void order_longSizeString() {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(507);
        orderItem.setOrderId(1);
        orderItem.setQuantity(1);
        orderItem.setSize("EXTRA-LARGE-SIZE");

        Integer resultId = orderItemRepository.order(orderItem);

        assertNotNull(resultId);
        String size = jdbcTemplate.queryForObject(
            "SELECT size FROM order_items WHERE id = ?", String.class, resultId);
        assertEquals("EXTRA-LARGE-SIZE", size);
    }

    @Test
    @DisplayName("異常系: 存在しない商品IDを指定して登録を試みる（外部キー制約がない場合は登録される）")
    void order_nonExistentItemId() {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(999999);
        orderItem.setOrderId(1);
        orderItem.setQuantity(1);
        orderItem.setSize("L");

        Integer resultId = orderItemRepository.order(orderItem);
        assertNotNull(resultId);
    }

    @Test
    @DisplayName("正常系: 注文IDに紐づく全ての注文明細を取得できる")
    void findByOrderId_Success() {
        // setUpで1つ入れているので、更に追加する
        OrderItem item = new OrderItem();
        item.setItemId(508);
        item.setOrderId(1);
        item.setQuantity(1);
        item.setSize("M");
        orderItemRepository.order(item);

        List<OrderItem> result = orderItemRepository.findByOrderId(1);

        // setUpで1件、ここで1件の計2件
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getOrderId());
    }

    @Test
    @DisplayName("正常系: 存在しない注文IDの場合は空リストを返す")
    void findByOrderId_Empty() {
        List<OrderItem> result = orderItemRepository.findByOrderId(999);
        assertTrue(result.isEmpty());
    }
}
