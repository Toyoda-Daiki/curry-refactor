package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.form.RandomCheckForm;
import com.example.service.UserService;

import jakarta.servlet.http.HttpSession;

/**
 * ２段階認証を行うコントローラー
 * 
 * @author naramasato
 *
 */
@Controller
@RequestMapping("")
public class TwoStepVerificationController {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(TwoStepVerificationController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private HttpSession session;

	@ModelAttribute
	public RandomCheckForm setUprandomForm() {
		return new RandomCheckForm();
	}

	/**
	 * メールアドレス入力画面
	 * 
	 * @return
	 */
	@RequestMapping("mailInsert")
	public String mailInsert() {
		session.removeAttribute("emailcheck");
		session.removeAttribute("email");
		session.removeAttribute("checkPass");
		return "/insert/mail_insert";
	}

	/**
	 * 入力されたメールアドレスに生成された数列を送信
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping("mailsend")
	public String mailSend(@Validated RandomCheckForm form, BindingResult result, Model model) {

		if (result.hasErrors()) {
			return mailInsert();
		}
		// ランダム生成された整数を受けとる
		String checkPass = userService.randomPass();

		session.setAttribute("checkPass", checkPass);
		session.setAttribute("emailcheck", form.getMail());
		//入力されたメールアドレスに送信
		userService.sendMail(form.getMail(),checkPass);
		
		return "redirect:/passCheck";
	}

	@RequestMapping("/passCheck")
	public String passCheck() {
		String emailcheck = (String) session.getAttribute("emailcheck");
		if (emailcheck == null) {
			log.warn("認証コード入力画面遷移不可: emailcheckなし");
			return "redirect:/mailInsert";
		}

		return "/insert/pass_check";
	}

	/**
	 * 数列を判断
	 * 
	 * @param form
	 * @param model
	 * @return 正しければ新規登録画面へ遷移
	 */
	@RequestMapping("check")
	public String check(RandomCheckForm form, Model model) {

		String emailcheck = (String) session.getAttribute("emailcheck");
		String sessionPass = (String) session.getAttribute("checkPass");

		if(emailcheck == null) {
			return "redirect:/mailInsert";
		}
		
		String message =  userService.checkpass(form.getPassCheck(),sessionPass);
	
		
		if(message.equals("OK")) {
			session.setAttribute("email", emailcheck);
			session.removeAttribute("checkPass");
			return "redirect:/insert";
		} else {
			model.addAttribute("error","error.number.not.match");
			return "/insert/pass_check";
		}
	}
}
