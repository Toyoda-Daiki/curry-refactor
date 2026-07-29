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

import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderStatus;
import com.example.domain.OrderTopping;

/**
 * OrderRepositoryの統合テスト.
 * SpringBootTestでSpringコンテキストを起動し、実DBを用いてOrderRepositoryの動作を確認する。
 * 
 * @Transactional により各テスト終了後にロールバックされるため、DBは汚染されない。
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private NamedParameterJdbcTemplate template;

	private Order sampleOrder;
	private Integer sampleUserId;
	private Integer sampleItemId;
	private Integer sampleOrderItemId;

	/**
	 * 各テスト実行前に users / items / orders / order_items へテストデータを投入する.
	 */
	@BeforeEach
	void setUp() {
		String uniqueEmail = "test_" + UUID.randomUUID() + "@example.com";

		sampleUserId = insertUser(
				"テスト 太郎",
				"password",
				uniqueEmail,
				"123-4567",
				"東京都渋谷区1-1-1",
				"090-1234-5678");

		sampleItemId = insertItem(
				"テストカレー",
				"テスト用商品",
				1000,
				1500,
				"/images/test.jpg",
				false);

		// sampleOrder = new Order();
		// sampleOrder.setUserId(sampleUserId);
		// sampleOrder.setStatus(OrderStatus.IN_CART);
		// sampleOrder.setTotalPrice(3000);
		// sampleOrder.setDestinationName("テスト 太郎");
		// sampleOrder.setDestinationEmail(uniqueEmail);
		// sampleOrder.setDestinationZipcode("123-4567");
		// sampleOrder.setDestinationAddress("東京都渋谷区1-1-1");
		// sampleOrder.setDestinationTel("090-1234-5678");
		// sampleOrder.setPaymentMethod(1);

		sampleOrder = Order.builder()
				.userId(sampleUserId)
				.status(OrderStatus.IN_CART)
				.totalPrice(3000)
				.destinationName("テスト 太郎")
				.destinationEmail(uniqueEmail)
				.destinationZipcode("123-4567")
				.destinationAddress("東京都渋谷区1-1-1")
				.destinationTel("090-1234-5678")
				.paymentMethod(1)
				.build();

		orderRepository.insert(sampleOrder);

		sampleOrderItemId = insertOrderItem(sampleItemId, sampleOrder.getId(), 2,
				"M");
	}

	/**
	 * usersテーブルへ登録し、発行されたIDを返す.
	 */
	private Integer insertUser(String name, String password, String email,
			String zipcode, String address, String telephone) {

		SqlParameterSource userParam = new MapSqlParameterSource()
				.addValue("name", name)
				.addValue("password", password)
				.addValue("email", email)
				.addValue("zipcode", zipcode)
				.addValue("address", address)
				.addValue("telephone", telephone);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(
				"INSERT INTO users (name, password, email, zipcode, address, telephone) "
						+ "VALUES (:name, :password, :email, :zipcode, :address, :telephone)",
				userParam, keyHolder, new String[] { "id" });

		return keyHolder.getKey().intValue();
	}

	/**
	 * itemsテーブルへ登録し、発行されたIDを返す.
	 */
	private Integer insertItem(String name, String description, Integer priceM,
			Integer priceL, String imagePath, Boolean deleted) {

		SqlParameterSource itemParam = new MapSqlParameterSource()
				.addValue("name", name)
				.addValue("description", description)
				.addValue("priceM", priceM)
				.addValue("priceL", priceL)
				.addValue("imagePath", imagePath)
				.addValue("deleted", deleted);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(
				"INSERT INTO items (name, description, price_m, price_l, image_path, deleted) "
						+ "VALUES (:name, :description, :priceM, :priceL, :imagePath, :deleted)",
				itemParam, keyHolder, new String[] { "id" });

		return keyHolder.getKey().intValue();
	}

	/**
	 * order_itemsテーブルへ登録し、発行されたIDを返す.
	 */
	private Integer insertOrderItem(Integer itemId, Integer orderId, Integer quantity, String size) {
		SqlParameterSource orderItemParam = new MapSqlParameterSource()
				.addValue("itemId", itemId)
				.addValue("orderId", orderId)
				.addValue("quantity", quantity)
				.addValue("size", size);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		template.update(
				"INSERT INTO order_items (item_id, order_id, quantity, size) "
						+ "VALUES (:itemId, :orderId, :quantity, :size)",
				orderItemParam, keyHolder, new String[] { "id" });

		return keyHolder.getKey().intValue();
	}

	/**
	 * toppingsテーブルへ登録し、指定したIDを返す.
	 * ※ toppings.id が自動採番でない環境向けに、idを明示的にINSERTする.
	 */
	private Integer insertTopping(Integer id, String name, Integer priceM, Integer priceL) {
		SqlParameterSource toppingParam = new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("name", name)
				.addValue("priceM", priceM)
				.addValue("priceL", priceL);

		template.update(
				"INSERT INTO toppings (id, name, price_m, price_l) "
						+ "VALUES (:id, :name, :priceM, :priceL)",
				toppingParam);

		return id;
	}

	/**
	 * order_toppingsテーブルへ登録する.
	 */
	private void insertOrderTopping(Integer toppingId, Integer orderItemId) {
		SqlParameterSource orderToppingParam = new MapSqlParameterSource()
				.addValue("toppingId", toppingId)
				.addValue("orderItemId", orderItemId);

		template.update(
				"INSERT INTO order_toppings (topping_id, order_item_id) "
						+ "VALUES (:toppingId, :orderItemId)",
				orderToppingParam);
	}

	@Test
	@DisplayName("insert: 正常系 - Orderが登録され、発行されたIDが返される")
	void insert_正常系_発行されたIDが返される() {
		Integer returnedId = sampleOrder.getId();

		assertNotNull(returnedId, "発行されたIDはnullであってはならない");
		assertTrue(returnedId > 0, "発行されたIDは正の整数であるべき");
	}

	@Test
	@DisplayName("insert: 正常系 - 登録した注文情報がordersテーブルに保存される")
	void insert_正常系_注文情報が保存される() {
		List<Order> orders = template.query(
				"SELECT id, user_id, status, total_price, destination_name, destination_email, "
						+ "destination_zipcode, destination_address, destination_tel, payment_method "
						+ "FROM orders WHERE id = :id",
				new MapSqlParameterSource("id", sampleOrder.getId()),
				(rs, rowNum) -> {
					Order order = Order.builder()
							.userId(rs.getInt("user_id"))
							.status(OrderStatus.values()[rs.getInt("status")])
							.totalPrice(rs.getInt("total_price"))
							.destinationName(rs.getString("destination_name"))
							.destinationEmail(rs.getString("destination_email"))
							.destinationZipcode(rs.getString("destination_zipcode"))
							.destinationAddress(rs.getString("destination_address"))
							.destinationTel(rs.getString("destination_tel"))
							.paymentMethod(rs.getInt("payment_method"))
							.build();
					order.setId(rs.getInt("id")); // idはBuilder外（DB採番結果）のため、残したsetIdで設定
					return order;
				});

		assertEquals(1, orders.size());
		Order actual = orders.get(0);
		assertEquals(sampleOrder.getId(), actual.getId());
		assertEquals(sampleOrder.getUserId(), actual.getUserId());
		assertEquals(OrderStatus.IN_CART, actual.getStatus());
		assertEquals(3000, actual.getTotalPrice());
		assertEquals("テスト 太郎", actual.getDestinationName());
		assertEquals(sampleOrder.getDestinationEmail(), actual.getDestinationEmail());
		assertEquals("123-4567", actual.getDestinationZipcode());
		assertEquals("東京都渋谷区1-1-1", actual.getDestinationAddress());
		assertEquals("090-1234-5678", actual.getDestinationTel());
		assertEquals(1, actual.getPaymentMethod());
	}

	@Test
	@DisplayName("orderLoad: 正常系 - insertしたOrderをorderIdで取得できる")
	void orderLoad_正常系_Orderリストが返される() {
		List<Order> result = orderRepository.orderLoad(sampleOrder.getId());

		assertNotNull(result, "insertしたデータがorderLoadで取得できていない");
		assertEquals(1, result.size());

		Order order = result.get(0);
		assertEquals(sampleOrder.getId(), order.getId());
		assertEquals("テスト 太郎", order.getDestinationName());
		assertEquals(sampleUserId, order.getUserId());
	}

	@Test
	@DisplayName("orderLoad: 異常系 - 存在しないorderIdの場合、空リストが返される")
	void orderLoad_該当なし_空リストが返される() {
		List<Order> result = orderRepository.orderLoad(999999);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	@DisplayName("orderLoad: 正常系 - 注文に複数の商品が含まれる場合、全て取得できる")
	void orderLoad_複数商品_全て取得できる() {
		Integer secondItemId = insertItem(
				"追加カレー",
				"2件目の商品",
				800,
				1200,
				"/images/test2.jpg",
				false);

		insertOrderItem(secondItemId, sampleOrder.getId(), 1, "L");

		List<Order> result = orderRepository.orderLoad(sampleOrder.getId());

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(2, result.get(0).getOrderItemList().size());
	}

	@Test
	@DisplayName("orderLoad: 正常系 - トッピング付き商品が正しく取得できる")
	void orderLoad_トッピングあり_正しく取得できる() {
		Integer toppingId = insertTopping(9999, "チーズ", 200, 300);
		insertOrderTopping(toppingId, sampleOrderItemId);

		List<Order> result = orderRepository.orderLoad(sampleOrder.getId());

		assertNotNull(result);
		assertEquals(1, result.size());

		OrderItem orderItem = result.get(0).getOrderItemList().get(0);
		assertNotNull(orderItem.getOrderTopping());
		assertEquals(1, orderItem.getOrderTopping().size());

		OrderTopping orderTopping = orderItem.getOrderTopping().get(0);
		assertEquals(toppingId, orderTopping.getToppingId());
		assertNotNull(orderTopping.getTopping());
		assertEquals("チーズ", orderTopping.getTopping().getName());
		assertEquals(200, orderTopping.getTopping().getPriceM());
		assertEquals(300, orderTopping.getTopping().getPriceL());
	}

	@Test
	@DisplayName("orderLoad: 正常系 - トッピングなし商品は空のトッピングリストを持つ")
	void orderLoad_トッピングなし_空リストになる() {
		List<Order> result = orderRepository.orderLoad(sampleOrder.getId());

		assertNotNull(result);
		assertEquals(1, result.size());

		OrderItem orderItem = result.get(0).getOrderItemList().get(0);
		assertNotNull(orderItem.getOrderTopping(), "トッピングなしでもnullではなく空リストであるべき");
		assertTrue(orderItem.getOrderTopping().isEmpty(), "トッピングなしの場合は空リストであるべき");
	}

	@Test
	@DisplayName("orderLoad: 正常系 - OrderItemのItem情報が正しくマッピングされている")
	void orderLoad_itemMapping() {
		List<Order> result = orderRepository.orderLoad(sampleOrder.getId());

		assertNotNull(result);
		assertFalse(result.get(0).getOrderItemList().isEmpty());

		OrderItem orderItem = result.get(0).getOrderItemList().get(0);
		assertNotNull(orderItem.getItem());
		assertEquals(sampleItemId, orderItem.getItem().getId());
		assertEquals("テストカレー", orderItem.getItem().getName());
		assertEquals("テスト用商品", orderItem.getItem().getDescription());
		assertEquals(1000, orderItem.getItem().getPriceM());
		assertEquals(1500, orderItem.getItem().getPriceL());
		assertEquals("/images/test.jpg", orderItem.getItem().getImagePath());
		assertFalse(orderItem.getItem().getDeleted());
	}

	@Test
	@DisplayName("findByOrdertable: 正常系 - insertしたOrderをuserIdで取得できる")
	void findByOrdertable_正常系_Orderリストが返される() {
		List<Order> result = orderRepository.findByOrdertable(sampleUserId);

		assertNotNull(result, "insertしたデータがfindByOrdertableで取得できていない");
		assertTrue(result.size() >= 1, "1件以上のOrderが取得できるべき");
		result.forEach(order -> assertEquals(sampleUserId, order.getUserId()));
	}

	@Test
	@DisplayName("findByOrdertable: 異常系 - 該当0件の場合はnullが返される")
	void findByOrdertable_0件_nullが返される() {
		List<Order> result = orderRepository.findByOrdertable(999999);

		assertNull(result, "Repository実装では0件時にnullが返る想定");
	}

	@Test
	@DisplayName("findByOrdertable: 正常系 - 取得したOrderのUser情報が正しくマッピングされている")
	void findByOrdertable_userMapping() {
		List<Order> result = orderRepository.findByOrdertable(sampleUserId);

		assertNotNull(result);
		Order order = result.get(0);
		assertNotNull(order.getUser());
		assertEquals(sampleUserId, order.getUser().getId());
		assertEquals("テスト 太郎", order.getUser().getName());
		assertEquals(sampleOrder.getDestinationEmail(), order.getUser().getEmail());
	}

	@Test
	@DisplayName("findByOrdertable: 正常系 - 別ユーザーの注文は取得されない")
	void findByOrdertable_別ユーザーは取得されない() {
		Integer otherUserId = insertUser(
				"別ユーザー",
				"password",
				"other_" + UUID.randomUUID() + "@example.com",
				"987-6543",
				"東京都新宿区2-2-2",
				"080-9999-8888");

		// Order otherOrder = new Order();
		// otherOrder.setUserId(otherUserId);
		// otherOrder.setStatus(OrderStatus.PAID);
		// otherOrder.setTotalPrice(9999);
		// otherOrder.setDestinationName("別ユーザー");
		// otherOrder.setDestinationEmail("other@example.com");
		// otherOrder.setDestinationZipcode("987-6543");
		// otherOrder.setDestinationAddress("東京都新宿区2-2-2");
		// otherOrder.setDestinationTel("080-9999-8888");
		// otherOrder.setPaymentMethod(2);

		Order otherOrder = Order.builder()
				.userId(otherUserId)
				.status(OrderStatus.PAID)
				.totalPrice(9999)
				.destinationName("別ユーザー")
				.destinationEmail("other@example.com")
				.destinationZipcode("987-6543")
				.destinationAddress("東京都新宿区2-2-2")
				.destinationTel("080-9999-8888")
				.paymentMethod(2)
				.build();

		orderRepository.insert(otherOrder);

		insertOrderItem(sampleItemId, otherOrder.getId(), 1, "M");

		List<Order> result = orderRepository.findByOrdertable(sampleUserId);

		assertNotNull(result);
		assertTrue(result.stream().allMatch(order -> order.getUserId().equals(sampleUserId)));
		assertTrue(result.stream().noneMatch(order -> order.getId().equals(otherOrder.getId())));
	}
}
