package com.example.repository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.Item;

@Repository
public class ItemRepository {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(ItemRepository.class);

	@Autowired
	private NamedParameterJdbcTemplate template;

	private static final RowMapper<Item> ITEM_ROW_MAPPER = (rs, i) -> {
		Item item = new Item();
		item.setId(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setDescription(rs.getString("description"));
		item.setPriceM(rs.getInt("price_m"));
		item.setPriceL(rs.getInt("price_l"));
		item.setImagePath(rs.getString("image_path"));
		item.setDeleted(rs.getBoolean("deleted"));
		return item;
	};

	// 一覧表示用RowMapper（descriptionなし）
	private static final RowMapper<Item> ITEM_LIST_ROW_MAPPER = (rs, i) -> {
		Item item = new Item();
		item.setId(rs.getInt("id"));
		item.setName(rs.getString("name"));
		item.setPriceM(rs.getInt("price_m"));
		item.setPriceL(rs.getInt("price_l"));
		item.setImagePath(rs.getString("image_path"));
		item.setDeleted(rs.getBoolean("deleted"));
		return item;
	};

	private static final RowMapper<String> NAME_ROW_MAPPER = (rs, i) -> {
		String name = rs.getString("name");
		return name;
	};

	/**
	 * 商品全件検索
	 * 
	 * @return
	 */
	public List<Item> findAll() {
		String findAllSql = "SELECT id,name,description,price_m,price_l,image_path, deleted FROM items WHERE deleted = false ORDER BY price_m;";
		List<Item> itemList = template.query(findAllSql, ITEM_ROW_MAPPER);
		return itemList;
	}

	/**
	 * 商品名から検索
	 * 
	 * @param name
	 * @return
	 */

	public List<Item> findByName(String name) {
		// String findByNameSql = "SELECT * FROM items WHERE name like :name and deleted
		// = false ORDER BY price_m;";

		// リファクタリング課題#29 SELECT *を必要カラムのみに変更
		String findByNameSql = "SELECT id, name, price_m, price_l, image_path, deleted FROM items WHERE name like :name and deleted = false ORDER BY price_m;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("name", "%" + name + "%");
		List<Item> itemList = template.query(findByNameSql, param, ITEM_LIST_ROW_MAPPER);

		// リファクタリング課題#2によってnullチェックを削除
		// if(itemList.size() == 0) {
		// log.debug("商品名検索結果なし: name={}", name);
		// return null;
		// }
		return itemList;
	}

	/**
	 * 商品詳細のSQLを発行
	 * 
	 * @param id 商品ID
	 * @return Item情報１件
	 */
	// リファクタリング課題#39 queryForObjectをqueryに変更し0件時の例外を防止
	public Item showItemDetail(Integer id) {
		log.debug("商品詳細取得: itemId={}", id);
		String showItemDetailSql = "SELECT * FROM items WHERE id = :id;";
		SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
		// Item item = template.queryForObject(showItemDetailSql, param,
		// ITEM_ROW_MAPPER);
		// return item;

		List<Item> result = template.query(showItemDetailSql, param, ITEM_ROW_MAPPER);
		return result.isEmpty() ? null : result.get(0);
	}

	public void insert(Item item) {
		SqlParameterSource param = new BeanPropertySqlParameterSource(item);
		String sql = "INSERT INTO items (name, description, price_m, price_l, image_path, deleted)"
				+ " VALUES (:name, :description, :priceM, :priceL, :imagePath, :deleted);";
		template.update(sql, param);
	}

	/**
	 * 商品の名前をすべて返す
	 * 
	 * @return すべてのitemの名前
	 */
	public List<String> getAllNames() {
		String sql = "SELECT name from items WHERE deleted = false;";
		List<String> allNames = template.query(sql, NAME_ROW_MAPPER);
		return allNames;
	}
}
