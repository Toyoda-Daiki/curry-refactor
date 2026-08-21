package com.example.form;

import com.example.validation.PhoneNumber;
import com.example.validation.PostalCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録用フォーム
 * @author matsunagadai
 *
 */
public class InsertForm {

	//ユーザー氏名
	@NotBlank(message="{error.empty.name}")
	private String name;
	@NotBlank(message="{error.empty.email}")
	@Email(message="{error.mail.format}")
	//メールアドレス
	private String email;
	//郵便番号
	@NotBlank(message="{error.empty.zipCode}")
	// @Pattern(regexp = "^[0-9]{3}-[0-9]{4}$", message = "{error.zipCode}")
	
	// リファクタリング課題#12 カスタムバリデーションの実装
	@PostalCode
	private String zipcode;
	//住所
	@NotBlank(message="{error.empty.address}")
	private String address;
	//電話番号
	@NotBlank(message="{error.empty.phone}")
	// @Pattern(regexp = "^[0-9]+-[0-9]+-[0-9]+$", message = "{error.telephone}")
	
	// リファクタリング課題#12 カスタムバリデーションの実装
	@PhoneNumber
	private String telephone;
	//パスワード
	@NotBlank(message="パスワードを入力して下さい")
	@Size(min=8, max=16, message="パスワードは８文字以上１６文字以内で設定してください")
	@Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
    message = "パスワードは英大文字、英小文字、数字をそれぞれ1文字以上含めてください")
	private String password;
	//確認用パスワード
	@NotBlank(message="{error.empty.confirmPassword}")
	private String confirmPassword;
	
	//ゲッターセッター
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	//toStringのオーバーライド
	@Override
	public String toString() {
		return "InsertForm [name=" + name + ", email=" + email + ", zipcode=" + zipcode + ", address=" + address
				+ ", telephone=" + telephone + ", password=" + password + ", confirmPassword=" + confirmPassword + "]";
	}
}
