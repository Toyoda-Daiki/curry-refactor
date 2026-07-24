package com.example.domain;
/**
 * 
 * @author satakemisako
 * ユーザー情報のドメインクラス
 *
 */
public class User {

	//ID
	private Integer id;
	//ユーザー氏名
	private String name;
	//パスワード
	private String password;
	//メールアドレス
	private String email;
	//郵便番号
	private String zipcode;
	//住所
	private String address;
	//電話番号
	private String telephone;
	
	
	//引数ありコンストラクタ
	public User(Integer id, String name, String password, String email, String zipcode, String address,
			String telephone) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
		this.zipcode = zipcode;
		this.address = address;
		this.telephone = telephone;
	}
	//引数なしコンストラクタ
	public User() {}
	
	//以下getter及びsetter
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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

	
	// リファクタリング課題#21 equals()とhashCode()をidのみで実装
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	//toStringメソッド
	// リファクタリング課題#23 パスワードをマスク化
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", password=" + "*****" + ", email=" + email + ", zipcode="
				+ zipcode + ", address=" + address + ", telephone=" + telephone + "]";
	}
	
	
	
}
