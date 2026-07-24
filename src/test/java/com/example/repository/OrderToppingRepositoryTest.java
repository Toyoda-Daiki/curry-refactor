package com.example.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.OrderTopping;

/**
 * {@link OrderToppingRepository} の統合テストクラス。
 *
 * <p>
 * SpringBootTestでSpringコンテキストを起動し、実際のDBを使ってテストを実行する。
 * {@code @Transactional} により各テスト後にロールバックされるため、DBの状態が汚染されない。
 * </p>
 *
 * <p>
 * テスト対象メソッド一覧:
 * </p>
 * <ul>
 * <li>{@link OrderToppingRepository#insert(OrderTopping)}</li>
 * </ul>
 *
 * @author toyodadaiki
 *
 * @see OrderToppingRepository
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderToppingRepositoryTest {

  /** テスト対象の {@link OrderToppingRepository}。 */
  @Autowired
  private OrderToppingRepository orderToppingRepository;

  /** DBへの直接操作に使用する {@link NamedParameterJdbcTemplate}。 */
  @Autowired
  private NamedParameterJdbcTemplate template;

  /**
   * テストで使用する order_items テーブルのID。
   * {@code @BeforeEach} でセットアップ時に発行されたIDが格納される。
   */
  private int orderItemId;

  /**
   * 各テスト実行前に users、items、orders、order_items テーブルへテストデータをinsertする。
   *
   * <p>
   * order_toppings テーブルは order_items に対して外部キー制約を持つため、
   * 事前に依存するテーブルへデータを挿入し、{@code orderItemId} をフィールドに保持する。
   * メールアドレスはUUIDを用いてユニーク性を保証する。
   * </p>
   */
  @BeforeEach
  void setUp() {
    String uniqueEmail = "test_" + UUID.randomUUID() + "@example.com";

    SqlParameterSource userParam = new MapSqlParameterSource()
        .addValue("name", "テスト 太郎")
        .addValue("password", "password")
        .addValue("email", uniqueEmail)
        .addValue("zipcode", "123-4567")
        .addValue("address", "東京都渋谷区1-1-1")
        .addValue("telephone", "090-1234-5678");

    KeyHolder userKeyHolder = new GeneratedKeyHolder();
    template.update(
        "INSERT INTO users (name, password, email, zipcode, address, telephone) "
            + "VALUES (:name, :password, :email, :zipcode, :address, :telephone)",
        userParam, userKeyHolder, new String[] { "id" });
    int userId = userKeyHolder.getKey().intValue();

    SqlParameterSource itemParam = new MapSqlParameterSource()
        .addValue("name", "テストカレー")
        .addValue("description", "テスト用商品")
        .addValue("priceM", 1000)
        .addValue("priceL", 1500)
        .addValue("imagePath", "/images/test.jpg")
        .addValue("deleted", false);

    KeyHolder itemKeyHolder = new GeneratedKeyHolder();
    template.update(
        "INSERT INTO items (name, description, price_m, price_l, image_path, deleted) "
            + "VALUES (:name, :description, :priceM, :priceL, :imagePath, :deleted)",
        itemParam, itemKeyHolder, new String[] { "id" });
    int itemId = itemKeyHolder.getKey().intValue();

    SqlParameterSource orderParam = new MapSqlParameterSource()
        .addValue("userId", userId)
        .addValue("status", 0)
        .addValue("totalPrice", 3000)
        .addValue("destinationName", "テスト 太郎")
        .addValue("destinationEmail", uniqueEmail)
        .addValue("destinationZipcode", "123-4567")
        .addValue("destinationAddress", "東京都渋谷区1-1-1")
        .addValue("destinationTel", "090-1234-5678")
        .addValue("paymentMethod", 1);

    KeyHolder orderKeyHolder = new GeneratedKeyHolder();
    template.update(
        "INSERT INTO orders (user_id, status, total_price, destination_name, destination_email, "
            + "destination_zipcode, destination_address, destination_tel, payment_method) "
            + "VALUES (:userId, :status, :totalPrice, :destinationName, :destinationEmail, "
            + ":destinationZipcode, :destinationAddress, :destinationTel, :paymentMethod)",
        orderParam, orderKeyHolder, new String[] { "id" });
    int orderId = orderKeyHolder.getKey().intValue();

    SqlParameterSource orderItemParam = new MapSqlParameterSource()
        .addValue("itemId", itemId)
        .addValue("orderId", orderId)
        .addValue("quantity", 2)
        .addValue("size", "M");

    KeyHolder orderItemKeyHolder = new GeneratedKeyHolder();
    template.update(
        "INSERT INTO order_items (item_id, order_id, quantity, size) "
            + "VALUES (:itemId, :orderId, :quantity, :size)",
        orderItemParam, orderItemKeyHolder, new String[] { "id" });
    orderItemId = orderItemKeyHolder.getKey().intValue();
  }

  /**
   * insert正常系
   * {@link OrderTopping} を1件insertし、DBに正しく登録されていることを確認する。
   */
  @Test
  @DisplayName("insert: 正常系 - OrderToppingが登録される")
  void insert_正常系_OrderToppingが登録される() {
    OrderTopping orderTopping = new OrderTopping();
    orderTopping.setToppingId(1);
    orderTopping.setOrderItemId(orderItemId);

    orderToppingRepository.insert(orderTopping);

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("orderItemId", orderItemId);
    List<Integer> result = template.queryForList(
        "SELECT id FROM order_toppings WHERE order_item_id = :orderItemId",
        param, Integer.class);

    assertNotNull(result, "insertしたデータがDBから取得できていない");
    assertEquals(1, result.size(), "1件登録されているべき");
  }

  /**
   * insert正常系（複数件）
   * 異なるtoppingIdを持つ {@link OrderTopping} を2件insertし、
   * DBに件数分登録されていることを確認する。
   */
  @Test
  @DisplayName("insert: 正常系 - 複数のOrderToppingが登録される")
  void insert_正常系_複数のOrderToppingが登録される() {
    OrderTopping orderTopping1 = new OrderTopping();
    orderTopping1.setToppingId(1);
    orderTopping1.setOrderItemId(orderItemId);

    OrderTopping orderTopping2 = new OrderTopping();
    orderTopping2.setToppingId(2);
    orderTopping2.setOrderItemId(orderItemId);

    orderToppingRepository.insert(orderTopping1);
    orderToppingRepository.insert(orderTopping2);

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("orderItemId", orderItemId);
    List<Integer> result = template.queryForList(
        "SELECT id FROM order_toppings WHERE order_item_id = :orderItemId",
        param, Integer.class);

    assertNotNull(result, "insertしたデータがDBから取得できていない");
    assertEquals(2, result.size(), "2件登録されているべき");
  }

  /**
   * insert正常系（toppingIdの確認）
   * insertした {@link OrderTopping} の {@code toppingId} が
   * DBに正しい値で保存されていることを確認する。
   */
  @Test
  @DisplayName("insert: 正常系 - toppingIdが正しく保存される")
  void insert_正常系_toppingIdが正しく保存される() {
    OrderTopping orderTopping = new OrderTopping();
    orderTopping.setToppingId(1);
    orderTopping.setOrderItemId(orderItemId);

    orderToppingRepository.insert(orderTopping);

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("orderItemId", orderItemId);
    Integer savedToppingId = template.queryForObject(
        "SELECT topping_id FROM order_toppings WHERE order_item_id = :orderItemId",
        param, Integer.class);

    assertEquals(1, savedToppingId, "toppingIdが正しく保存されているべき");
  }
}
