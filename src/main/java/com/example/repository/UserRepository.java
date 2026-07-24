package com.example.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import com.example.domain.User;

@Repository
public class UserRepository {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(UserRepository.class);

	private static final RowMapper<User> USER_ROW_MAPPER = (rs, i) -> {
		User user = new User();
		user.setId(rs.getInt("id"));
		user.setName(rs.getString("name"));
		user.setEmail(rs.getString("email"));
		user.setPassword(rs.getString("password"));
		user.setZipcode(rs.getString("zipcode"));
		user.setAddress(rs.getString("address"));
		user.setTelephone(rs.getString("telephone"));
		return user;
	};

	@Autowired
	private NamedParameterJdbcTemplate template;

	public User findByMailAddress(String email) {
		String sql = "SELECT * FROM users WHERE email=:email";

		SqlParameterSource param = new MapSqlParameterSource().addValue("email", email);

		try {
			User user = template.queryForObject(sql, param, USER_ROW_MAPPER);
			// 見つからなかったときのdebugで十分なため、コメントアウト
			// log.debug("User取得成功", user);

			return user;

		} catch (Exception e) {
			log.debug("ユーザー取得結果なし: email={}", email);
			return null;
		}

	}

	public void insert(User user) {
		// user丸ごとだとPWなどが出る可能性あり、そのためコメントアウト
		// log.info("User登録処理開始: {}", user);
		SqlParameterSource param = new BeanPropertySqlParameterSource(user);
		String sql = "INSERT INTO users (name, email, password, zipcode, address, telephone) "
				+ "VALUES (:name, :email, :password, :zipcode, :address, :telephone);";
		template.update(sql, param);

		log.info("ユーザー登録完了: email={}", user.getEmail());
	}

}
