package com.example.form;
/**
 * 
 * @author satakemisako
 * 
 *ユーザーログイン時のフォームクラス
 *
 */
public class LoginForm {

	private String password;
	private String email;
	
	
	
	@Override
	public String toString() {
		return "LoginForm [password=" + password + ", mailAddress=" + email + "]";
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
