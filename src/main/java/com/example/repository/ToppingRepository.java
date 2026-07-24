package com.example.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Topping;

/**
 * トッピングのripository
 * 
 * @author naramasato
 *
 */
@Repository
public class ToppingRepository {

	@Autowired
	private NamedParameterJdbcTemplate template;

	private static final RowMapper<Topping> TOPPING_ROW_MAPPER = (rs, i) -> {
		Topping topping = new Topping();
		topping.setId(rs.getInt("id"));
		topping.setName(rs.getString("name"));
		topping.setPriceM(rs.getInt("price_m"));
		topping.setPriceL(rs.getInt("price_l"));
		topping.setStockAmount(rs.getInt("stock_amount"));
		return topping;
	};

	/**
	 * トッピングを全件探す
	 * 
	 * @return 検索されたトッピングテーブルの情報
	 */
	public List<Topping> findAllTopping() {
		String findAllToppingSql = "SELECT id, name, price_m, price_l, stock_amount FROM toppings ORDER BY id";
		List<Topping> toppingList = template.query(findAllToppingSql, TOPPING_ROW_MAPPER);
		return toppingList;
	}

	/**
	 * トッピングの在庫を減らす
	 * @param id トッピングID
	 * @param amount 減らす個数
	 */
	public void decrementStock(Integer id, Integer amount) {
		String sql = "UPDATE toppings SET stock_amount = stock_amount - :amount WHERE id = :id AND stock_amount >= :amount";
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("id", id)
				.addValue("amount", amount);
		
		int affected = template.update(sql, param);
		if (affected == 0) {
			Topping topping = findById(id);
			throw new com.example.exception.StockShortageException("トッピング「" + topping.getName() + "」の在庫が不足しています（現在の在庫: " + topping.getStockAmount() + "）");
		}
	}

	/**
	 * 指定されたIDのトッピング情報を1件取得する
	 * 
	 * @param id トッピングID
	 * @return 在庫数を含むトッピング情報
	 */
	public Topping findById(Integer id) {
		String sql = "SELECT id, name, price_m, price_l, stock_amount FROM toppings WHERE id = :id";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		// queryForObjectは1件だけ取得するときに使います
		return template.queryForObject(sql, param, TOPPING_ROW_MAPPER);
	}

}
