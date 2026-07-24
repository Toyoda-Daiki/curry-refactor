package com.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import com.example.domain.CartTopping;
import com.example.domain.Topping;

import java.util.List;

@Repository
public class CartToppingRepository {

    @Autowired
    private NamedParameterJdbcTemplate template;

    private static final RowMapper<CartTopping> CART_TOPPING_ROWMAPPER = (rs, i) -> {
        CartTopping cartTopping = new CartTopping();
        cartTopping.setId(rs.getInt("id"));
        cartTopping.setCartItemId(rs.getInt("cart_item_id"));
        cartTopping.setToppingId(rs.getInt("topping_id"));

        Topping topping = new Topping();
        topping.setId(rs.getInt("topping_id"));
        topping.setName(rs.getString("name"));
        topping.setPriceM(rs.getInt("price_m"));
        topping.setPriceL(rs.getInt("price_l"));
        cartTopping.setTopping(topping);

        return cartTopping;
    };

    /**
     * カートIDに紐づく全トッピングを取得する。
     */
    //課題 16 SQLを複数回実行しない用のメソッドで追加。
    public List<CartTopping> findByCartId(Integer cartId) {
        String sql = "SELECT ct.id, ct.cart_item_id, ct.topping_id, t.name, t.price_m, t.price_l " +
                "FROM cart_toppings ct " +
                "JOIN toppings t ON ct.topping_id = t.id " +
                "JOIN cart_items ci ON ct.cart_item_id = ci.id " +
                "WHERE ci.cart_id = :cartId";
        SqlParameterSource param = new MapSqlParameterSource().addValue("cartId", cartId);
        return template.query(sql, param, CART_TOPPING_ROWMAPPER);
    }

    /**
     * cart_item_idに紐づくトッピングリストを取得する。
     */
    public List<CartTopping> findByCartItemId(Integer cartItemId) {
        String sql = "SELECT ct.id, ct.cart_item_id, ct.topping_id, t.name, t.price_m, t.price_l " +
                "FROM cart_toppings ct " +
                "JOIN toppings t ON ct.topping_id = t.id " +
                "WHERE ct.cart_item_id = :cartItemId";
        SqlParameterSource param = new MapSqlParameterSource().addValue("cartItemId", cartItemId);
        return template.query(sql, param, CART_TOPPING_ROWMAPPER);
    }

    /**
     * カートトッピングを保存する。
     */
    public void save(CartTopping cartTopping) {
        String sql = "INSERT INTO cart_toppings (cart_item_id, topping_id) VALUES (:cartItemId, :toppingId)";
        SqlParameterSource param = new BeanPropertySqlParameterSource(cartTopping);
        template.update(sql, param);
    }

    /**
     * カートアイテムIDに紐づくトッピングをすべて削除する。
     */
    public void deleteByCartItemId(Integer cartItemId) {
        String sql = "DELETE FROM cart_toppings WHERE cart_item_id = :cartItemId";
        SqlParameterSource param = new MapSqlParameterSource().addValue("cartItemId", cartItemId);
        template.update(sql, param);
    }
}
