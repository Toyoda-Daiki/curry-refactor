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

import com.example.domain.Cart;

import java.util.List;

@Repository
public class CartRepository {

    @Autowired
    private NamedParameterJdbcTemplate template;

    private static final RowMapper<Cart> CART_ROWMAPPER = (rs, i) -> {
        Cart cart = new Cart();
        cart.setId(rs.getInt("id"));
        cart.setUserId((Integer) rs.getObject("user_id"));
        cart.setSessionId(rs.getString("session_id"));
        return cart;
    };

    /**
     * session_idでカートを取得する。
     */
    public Cart findBySessionId(String sessionId) {
        String sql = "SELECT id, user_id, session_id FROM carts WHERE session_id = :sessionId";
        SqlParameterSource param = new MapSqlParameterSource().addValue("sessionId", sessionId);
        List<Cart> cartList = template.query(sql, param, CART_ROWMAPPER);
        if (cartList.size() == 0) {
            return null;
        }
        return cartList.get(0);
    }

    /**
     * user_idでカートを取得する。
     */
    public Cart findByUserId(Integer userId) {
        String sql = "SELECT id, user_id, session_id FROM carts WHERE user_id = :userId";
        SqlParameterSource param = new MapSqlParameterSource().addValue("userId", userId);
        List<Cart> cartList = template.query(sql, param, CART_ROWMAPPER);
        if (cartList.size() == 0) {
            return null;
        }
        return cartList.get(0);
    }

    /**
     * カートを保存する。
     */
    public Cart save(Cart cart) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(cart);
        if (cart.getId() == null) {
            String sql = "INSERT INTO carts (user_id, session_id) VALUES (:userId, :sessionId)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            String[] keyColumnNames = { "id" };
            template.update(sql, param, keyHolder, keyColumnNames);
            cart.setId(keyHolder.getKey().intValue());
        } else {
            String sql = "UPDATE carts SET user_id = :userId, session_id = :sessionId WHERE id = :id";
            template.update(sql, param);
        }
        return cart;
    }

    /**
     * カートを削除する。
     */
    public void deleteById(Integer id) {
        String sql = "DELETE FROM carts WHERE id = :id";
        SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);
        template.update(sql, param);
    }
}
