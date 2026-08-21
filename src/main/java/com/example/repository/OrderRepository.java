package com.example.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderStatus;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.domain.User;

/**
 * オーダー内容をSQLとやりとりする
 * 
 * @author naramasato
 *
 */
@Repository
public class OrderRepository {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(OrderRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate template;

	// orders,users,order_items,items,order_toppings,toppingsのテーブル結合に使うResultSetExtractor
	private static final ResultSetExtractor<List<Order>> ORDER_RESULTSET = (rs) -> {

		// OrderのListに新たに格納
		List<Order> orderList = new ArrayList<>();

		// orderItemのListを作成。外部キーに紐づいてるテーブル毎に発行してしまう為初めはnullで宣言しておく
		List<OrderItem> orderItemList = null;

		// orderToppoingのListを作成。外部キーに紐づいてるテーブル毎に発行してしまう為初めはnullで宣言しておく
		List<OrderTopping> orderTopping = null;

		// 前回のorderのidを入れる箱
		int beforeIdNum = 0;

		// 前回のorderItemのidを入れる箱
		int itemBefore = 0;

		while (rs.next()) {
			// 現在のorderidを代入
			int nowIdNum = rs.getInt("o_id");

			// 現在のidと前回のidが被ってないか確認
			if (nowIdNum != beforeIdNum) {

				// // orderオブジェクトにSQLのデータをセット
				// Order order = new Order();
				// order.setId(nowIdNum);
				// order.setUserId(rs.getInt("o_user_id"));
				// // order.setStatus(rs.getInt("o_status"));
				// order.setStatus(OrderStatus.values()[rs.getInt("o_status")]);
				// order.setTotalPrice(rs.getInt("o_total_price"));
				// order.setOrderDate(rs.getDate("o_order_date"));
				// order.setDestinationName(rs.getString("o_destination_name"));
				// order.setDestinationEmail(rs.getString("o_destination_email"));
				// order.setDestinationZipcode(rs.getString("o_destination_zipcode"));
				// order.setDestinationAddress(rs.getString("o_destination_address"));
				// order.setDestinationTel(rs.getString("o_destination_tel"));
				// order.setDeliveryTime(rs.getTimestamp("o_delivery_time"));
				// order.setPaymentMethod(rs.getInt("o_payment_method"));

				// リファクタリング課題#4 Builderパターンで安全なオブジェクト生成に変更
				Order order = Order.builder()
						.id(nowIdNum)
						.userId(rs.getInt("o_user_id"))
						.status(OrderStatus.values()[rs.getInt("o_status")])
						.totalPrice(rs.getInt("o_total_price"))
						.orderDate(rs.getDate("o_order_date"))
						.destinationName(rs.getString("o_destination_name"))
						.destinationEmail(rs.getString("o_destination_email"))
						.destinationZipcode(rs.getString("o_destination_zipcode"))
						.destinationAddress(rs.getString("o_destination_address"))
						.destinationTel(rs.getString("o_destination_tel"))
						.deliveryTime(rs.getTimestamp("o_delivery_time"))
						.paymentMethod(rs.getInt("o_payment_method"))
						.build();

				// Userをセット
				User user = new User();
				user.setId(rs.getInt("u_id"));
				user.setName(rs.getString("u_name"));
				user.setEmail(rs.getString("u_email"));
				user.setZipcode(rs.getString("u_zipcode"));
				user.setAddress(rs.getString("u_address"));
				user.setTelephone(rs.getString("u_telephone"));

				// Orderドメインが持っているuserフィールドに外部キーで紐づいているUserをセット
				order.setUser(user);

				// 既に宣言してるorderItemListに新規でArrayListをセット
				orderItemList = new ArrayList<OrderItem>();
				order.setOrderItemList(orderItemList);

				orderList.add(order);
			}

			// 現在のorderItemidを代入
			int itemNow = rs.getInt("oi_id");
			// 現在のorderItemidと前回のorderItemidが被ってないか確認
			if (itemNow != itemBefore) {
				// orderItemオブジェクトにSQLのデータをセット
				OrderItem orderItem = new OrderItem();
				orderItem.setId(rs.getInt("oi_id"));
				orderItem.setItemId(rs.getInt("oi_item_id"));
				orderItem.setOrderId(rs.getInt("oi_order_id"));
				orderItem.setQuantity(rs.getInt("oi_quantity"));
				orderItem.setSize(rs.getString("oi_size"));

				// Itemをセット
				Item item = new Item();
				item.setId(rs.getInt("i_id"));
				item.setName(rs.getString("i_name"));
				item.setDescription(rs.getString("i_description"));
				item.setPriceM(rs.getInt("i_price_m"));
				item.setPriceL(rs.getInt("i_price_l"));
				item.setImagePath(rs.getString("i_image_path"));
				item.setDeleted(rs.getBoolean("i_deleted"));

				// OrderItemドメインが持っているItemフィールドに外部キーで紐づいているItemをセット
				orderItem.setItem(item);

				// 既に宣言してるorderToppingListに新規でArrayListをセット
				orderTopping = new ArrayList<OrderTopping>();
				orderItem.setOrderTopping(orderTopping);

				orderItemList.add(orderItem);
			}

			// orderToppingを持っていないorderItemにオブジェクトを作らないようにする
			if (rs.getInt("ot_id") != 0) {
				// orderToppingオブジェクトにSQLのデータをセット
				OrderTopping orderToppings = new OrderTopping();
				orderToppings.setId(rs.getInt("ot_id"));
				orderToppings.setToppingId(rs.getInt("ot_topping_id"));
				orderToppings.setOrderItemId(rs.getInt("ot_order_item_id"));

				// toppingをセット
				Topping topping = new Topping();
				topping.setId(rs.getInt("t_id"));
				topping.setName(rs.getString("t_name"));
				topping.setPriceM(rs.getInt("t_price_m"));
				topping.setPriceL(rs.getInt("t_price_l"));
				// toppingの情報をorderToppingにセット
				orderToppings.setTopping(topping);
				orderTopping.add(orderToppings);
			}
			// 現在のOrderItemのidを代入
			itemBefore = itemNow;
			// 現在のOrderのidを代入
			beforeIdNum = nowIdNum;
		}
		return orderList;
	};

	/**
	 * orderの詳細を表示するメゾット
	 * 
	 * @param orderId
	 * @return List<Order> １件の注文内容
	 */
	// リファクタリング課題#23 パスワードの不要なSELECTを削除
	public List<Order> orderLoad(Integer orderId) {
		log.debug("注文詳細取得: orderId={}", orderId);
		String sql = "SELECT o.id AS o_id, o.user_id AS o_user_id, o.status AS o_status, o.total_price AS o_total_price, "
				+ "o.order_date AS o_order_date, o.destination_name AS o_destination_name, o.destination_email AS o_destination_email, "
				+ "o.destination_zipcode AS o_destination_zipcode, o.destination_address AS o_destination_address, o.destination_tel AS o_destination_tel, "
				+ "o.delivery_time AS o_delivery_time, o.payment_method AS o_payment_method, "
				+ "u.id AS u_id, u.name AS u_name, u.email As u_email, u.zipcode AS u_zipcode, u.address AS u_address, u.telephone AS u_telephone, "
				+ "oi.id AS oi_id, oi.item_id AS oi_item_id, oi.order_id AS oi_order_id, oi.quantity AS oi_quantity, "
				+ "oi.size AS oi_size, "
				+ "i.id AS i_id, i.name AS i_name, i.description AS i_description, i.price_m AS i_price_m, i.price_l AS i_price_l, "
				+ "i.image_path AS i_image_path, i.deleted AS i_deleted, "
				+ "ot.id AS ot_id, ot.topping_id AS ot_topping_id, ot.order_item_id AS ot_order_item_id, t.id AS t_id, t.name AS t_name, "
				+ "t.price_m AS t_price_m, t.price_l AS t_price_l "
				+ "FROM orders o "
				+ "JOIN users u ON o.user_id = u.id "
				// + "RIGHT JOIN order_items oi ON o.id = oi.order_id "
				// + "RIGHT JOIN items i ON oi.item_id = i.id "
				// + "LEFT OUTER JOIN order_toppings ot ON oi.id = ot.order_item_id "
				// + "LEFT OUTER JOIN toppings t ON ot.topping_id = t.id "

				// リファクタリング課題#30 RIGHT JOINをLEFT JOINに変更
				+ "LEFT JOIN order_items oi ON oi.order_id = o.id "
				+ "LEFT JOIN items i ON i.id = oi.item_id "
				+ "LEFT JOIN order_toppings ot ON ot.order_item_id = oi.id "
				+ "LEFT JOIN toppings t ON t.id = ot.topping_id "

				+ "WHERE o.id = :orderId ORDER BY i.id DESC";

		SqlParameterSource param = new MapSqlParameterSource().addValue("orderId", orderId);

		List<Order> orderList = template.query(sql, param, ORDER_RESULTSET);
		return orderList;
	}

	/**
	 * orders,users,order_items,items,order_toppings,toppingsのテーブルを結合して出力
	 * 
	 * @param userId
	 * @return
	 */
	// リファクタリング課題#23 パスワードの不要なSELECTを削除
	public List<Order> findByOrdertable(Integer userId) {
		String sql = "SELECT o.id AS o_id, o.user_id AS o_user_id, o.status AS o_status, o.total_price AS o_total_price, "
				+ "o.order_date AS o_order_date, o.destination_name AS o_destination_name, o.destination_email AS o_destination_email, "
				+ "o.destination_zipcode AS o_destination_zipcode, o.destination_address AS o_destination_address, o.destination_tel AS o_destination_tel, "
				+ "o.delivery_time AS o_delivery_time, o.payment_method AS o_payment_method, "
				+ "u.id AS u_id, u.name AS u_name, u.email As u_email, u.zipcode AS u_zipcode, u.address AS u_address, u.telephone AS u_telephone, "
				+ "oi.id AS oi_id, oi.item_id AS oi_item_id, oi.order_id AS oi_order_id, oi.quantity AS oi_quantity, "
				+ "oi.size AS oi_size, "
				+ "i.id AS i_id, i.name AS i_name, i.description AS i_description, i.price_m AS i_price_m, i.price_l AS i_price_l, "
				+ "i.image_path AS i_image_path, i.deleted AS i_deleted, "
				+ "ot.id AS ot_id, ot.topping_id AS ot_topping_id, ot.order_item_id AS ot_order_item_id, t.id AS t_id, t.name AS t_name, "
				+ "t.price_m AS t_price_m, t.price_l AS t_price_l "
				+ "FROM orders o "
				+ "JOIN users u ON o.user_id = u.id "
				// + "RIGHT JOIN order_items oi ON o.id = oi.order_id "
				// + "RIGHT JOIN items i ON oi.item_id = i.id "
				// + "LEFT OUTER JOIN order_toppings ot ON oi.id = ot.order_item_id "
				// + "LEFT OUTER JOIN toppings t ON ot.topping_id = t.id "

				// リファクタリング課題#30 RIGHT JOINをLEFT JOINに変更
				+ "LEFT JOIN order_items oi ON oi.order_id = o.id "
				+ "LEFT JOIN items i ON i.id = oi.item_id "
				+ "LEFT JOIN order_toppings ot ON ot.order_item_id = oi.id "
				+ "LEFT JOIN toppings t ON t.id = ot.topping_id "

				+ "WHERE o.user_id = :userId ORDER BY o.order_date DESC, o.id DESC, i.id DESC";

		SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);

		List<Order> orderList = template.query(sql, param, ORDER_RESULTSET);

		if (orderList.size() == 0) {
		// log.debug("注文履歴取得結果なし: userId={}", userId);
		// return null;
		// }

		// リファクタリング課題#2（対応漏れ修正）
		// 0件のとき null ではなく空リストを返す。呼び出し元でのnullチェック連鎖を撲滅するため
			log.debug("注文履歴取得結果なし: userId={}", userId);
		}
		return orderList;
	}

	/**
	 * Orderの情報をSQLに登録
	 * 
	 * @param order
	 * @return ordersテーブルに登録された際に発行されたidを返す
	 */
	public Integer insert(Order order) {
		// SqlParameterSource param = new BeanPropertySqlParameterSource(order);
		// String insertSql = "INSERT INTO orders (user_id, status, total_price,
		// order_date, destination_name, destination_email, destination_zipcode,
		// destination_address, destination_tel, delivery_time, payment_method) "
		// + "VALUES (:userId, :status, :totalPrice, :orderDate, :destinationName,
		// :destinationEmail, :destinationZipcode, :destinationAddress, :destinationTel,
		// :deliveryTime, :paymentMethod);";

		// // 自動採番の際にidを取得する
		// KeyHolder keyHolder = new GeneratedKeyHolder();
		// String[] keyColumnNames = { "id" };
		// template.update(insertSql, param, keyHolder, keyColumnNames);
		// order.setId(keyHolder.getKey().intValue());

		// log.info("注文登録完了: orderId={}, userId={}", order.getId(), order.getUserId());

		// return order.getId();

		// BeanPropertySqlParameterSourceではenumを扱えないため手動でパラメータを設定
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("userId", order.getUserId())
				.addValue("status", order.getStatus().getValue()) // enumをintに変換
				.addValue("totalPrice", order.getTotalPrice())
				.addValue("orderDate", order.getOrderDate())
				.addValue("destinationName", order.getDestinationName())
				.addValue("destinationEmail", order.getDestinationEmail())
				.addValue("destinationZipcode", order.getDestinationZipcode())
				.addValue("destinationAddress", order.getDestinationAddress())
				.addValue("destinationTel", order.getDestinationTel())
				.addValue("deliveryTime", order.getDeliveryTime())
				.addValue("paymentMethod", order.getPaymentMethod());

		String insertSql = "INSERT INTO orders (user_id, status, total_price, order_date, destination_name, destination_email, destination_zipcode, destination_address, destination_tel, delivery_time, payment_method) "
				+ "VALUES (:userId, :status, :totalPrice, :orderDate, :destinationName, :destinationEmail, :destinationZipcode, :destinationAddress, :destinationTel, :deliveryTime, :paymentMethod);";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		String[] keyColumnNames = { "id" };
		template.update(insertSql, param, keyHolder, keyColumnNames);
		order.setId(keyHolder.getKey().intValue());
		log.info("注文登録完了: orderId={}, userId={}", order.getId(), order.getUserId());
		return order.getId();
	}

}
