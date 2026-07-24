package com.example.domain;

/**
 * トッピングのドメイン
 * 
 * @author naramasato
 *
 */
public class Topping {

	// id
	private Integer id;
	// トッピング名
	private String name;
	// Mサイズ時の価格
	private Integer priceM;
	// Lサイズ時の価格
	private Integer priceL;
	// トッピングの在庫数
	private Integer stockAmount;

	// コンストラクター
	public Topping() {
	}

	public Topping(Integer id, String name, Integer priceM, Integer priceL, Integer stockAmount) {
		this.id = id;
		this.name = name;
		this.priceM = priceM;
		this.priceL = priceL;
		this.stockAmount = stockAmount;
	}

	// ゲッターとセッター
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

	public Integer getPriceM() {
		return priceM;
	}

	public void setPriceM(Integer priceM) {
		this.priceM = priceM;
	}

	public Integer getPriceL() {
		return priceL;
	}

	public void setPriceL(Integer priceL) {
		this.priceL = priceL;
	}

	public Integer getStockAmount() {
		return stockAmount;
	}

	public void setStockAmount(Integer stockAmount) {
		this.stockAmount = stockAmount;
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
		Topping other = (Topping) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Topping [id=" + id + ", name=" + name + ", priceM=" + priceM + ", priceL=" + priceL + ", stockAmount="
				+ stockAmount + "]";
	}

	// toString

}
