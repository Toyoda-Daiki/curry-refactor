package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.User;
import com.example.form.LoginForm;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("")
public class LoginController {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService service;

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public LoginForm setUpLoginForm() {
		LoginForm loginForm = new LoginForm();
		return loginForm;// リクエストパラメーターにloginFormが格納された
	}
	
	/**
	 * ログイン画面に遷移
	 * @return ログイン画面
	 */
	@RequestMapping("/toLogin")
	public String toLogin() {
		return "login/login";
	}

	/**
	 * ログアウト処理
	 * @return　ログアウト後の画面商品一覧画面
	 */

	@RequestMapping("/logout")
	public String logout() {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			log.info("ログアウト: userId={}", user.getId());
		} else {
			log.info("ログアウト: userなし");
		}
		session.invalidate();
		return "forward:/showList";
	}
}
