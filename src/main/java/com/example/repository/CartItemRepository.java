package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.domain.CartItem;

import java.util.List;

@Repository
public class CartItemRepository {

    @Autowired
    private NamedParameterJdbcTemplate template;

    private static final RowMapper<CartItem> CART_ITEM_ROWMAPPER = (rs, i) -> {
        CartItem item = new CartItem();
        item.setId(rs.getInt("id"));
        item.setCartId(rs.getInt("cart_id"));
        item.setItemId(rs.getInt("item_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setSize(rs.getString("size"));
        item.setName(rs.getString("name"));
        item.setItemPrice(rs.getInt("item_price"));
        item.setImagePath(rs.getString("image_path"));
        return item;
    };

    /**
     * cart_idに紐づくアイテムリストを取得する。
     */
    public List<CartItem> findByCartId(Integer cartId) {
        String sql = "SELECT ci.id, ci.cart_id, ci.item_id, ci.quantity, ci.size, i.name, " +
                "CASE WHEN ci.size = 'M' THEN i.price_m ELSE i.price_l END as item_price, i.image_path " +
                "FROM cart_items ci " +
                "JOIN items i ON ci.item_id = i.id " +
                "WHERE ci.cart_id = :cartId " +
                "ORDER BY ci.id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("cartId", cartId);
        return template.query(sql, param, CART_ITEM_ROWMAPPER);
    }

    /**
     * カートアイテムを保存する。
     */
    public CartItem save(CartItem cartItem) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(cartItem);
        if (cartItem.getId() == null) {
            String sql = "INSERT INTO cart_items (cart_id, item_id, quantity, size) " +
                    "VALUES (:cartId, :itemId, :quantity, :size)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String[] keyColumnNames = { "id" };
            template.update(sql, param, keyHolder, keyColumnNames);
            cartItem.setId(keyHolder.getKey().intValue());
        } else {
            String sql = "UPDATE cart_items SET quantity = :quantity, size = :size WHERE id = :id";
            template.update(sql, param);
        }
        return cartItem;
    }

    /**
     * カートアイテムを削除する。
     */
    public void deleteById(Integer id) {
        String sql = "DELETE FROM cart_items WHERE id = :id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
        template.update(sql, param);
    }
}
