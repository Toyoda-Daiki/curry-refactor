package com.example.service;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.User;
import com.example.repository.UserRepository;



@Service
@Transactional
public class UserService {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private MailSender sender;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public User login(String password, String email) {
		User user = repository.findByMailAddress(email);
		if (user == null) {
			log.debug("ログイン認証失敗: ユーザーなし email={}", email);
			return null;
		}
		// パスワードが不一致だった場合はnullを返す
		if (!passwordEncoder.matches(password, user.getPassword())) {
			log.debug("ログイン認証失敗: パスワード不一致 email={}", email);
			return null;
		}
		return user;
	}
	
	/**
	 * ユーザー登録を行う。
	 * @param user
	 */
	public void insert(User user) {
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		repository.insert(user);
		log.info("ユーザー登録処理完了: email={}", user.getEmail());
	}
	
	/**
	 * ２段階認証のパスワードを入力されたメールアドレスに送信
	 * 
	 * @param email
	 * @param checkPass
	 */
	public void sendMail(String email, String checkPass) {
        SimpleMailMessage msg = new SimpleMailMessage();
        

        msg.setFrom("curry-admin@example.com");
        msg.setTo(email);
        msg.setSubject("２段階認証パスワード発行");//タイトルの設定
        msg.setText(checkPass + " こちらを画面に入力してください"); //本文の設定

        this.sender.send(msg);
		log.info("2段階認証メール送信: email={}", email);
    }
	
	/**
	 * ランダムの数字を発行
	 * @return 生成された４桁の数字
	 */
	public String randomPass() {
		Random rand = new Random();
		String randomStr = "";
		for(int i = 0; i < 4; i++) {
			int num = rand.nextInt(10);
			randomStr += Integer.toString(num);
		}
		
		// 認証コードそのものをログに残してしまうのはNGなため、コメントアウト
		// log.debug("生成された認証コード: {}", randomStr);
		return randomStr;
	}
	
	/**
	 * 数字の確認
	 * @param numPass　入力された数字
	 * @return
	 */

	public String checkpass(String numPass,String sessionPass) {	
		if(sessionPass != null && sessionPass.equals(numPass)) {
			return "OK";
		} else {
			return "NO";
		}
	}
	
}
