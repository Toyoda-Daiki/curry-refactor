package com.example.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.domain.OrderItem;

/**
 * order_itemsとやりとりする
 * 
 * @author naramasato
 *
 */
@Repository
public class OrderItemRepository {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(OrderItemRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate template;

	private static final RowMapper<OrderItem> ORDER_ITEM_ROW_MAPPER
			= new BeanPropertyRowMapper<>(OrderItem.class);

	/**
	 * order_itemsにINSERTする
	 * 
	 * @param orderItem
	 * @return 自動採番されたid
	 */
	public Integer order(OrderItem orderItem) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(orderItem);

		String sql = "INSERT INTO order_items(item_id, order_id, quantity, size) "
				+ "VALUES(:itemId, :orderId, :quantity, :size)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		String[] keyColumnNames = { "id" };
		template.update(sql, param, keyHolder, keyColumnNames);

		orderItem.setId(keyHolder.getKey().intValue());

		log.debug("注文商品登録完了: orderItemId={}, orderId={}, itemId={}",
				orderItem.getId(), orderItem.getOrderId(), orderItem.getItemId());

		return orderItem.getId();
	}

	public List<OrderItem> findByOrderId(Integer orderId) {
		String sql = "SELECT * FROM order_items WHERE order_id = :orderId";
		SqlParameterSource param = new MapSqlParameterSource("orderId", orderId);
		return template.query(sql, param, ORDER_ITEM_ROW_MAPPER);
	}
}
