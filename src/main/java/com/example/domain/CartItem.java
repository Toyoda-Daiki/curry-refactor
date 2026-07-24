package com.example.domain;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CartItem {

	// カート商品ID
	private Integer id;
	// カートID
	private Integer cartId;
	// 商品Id
	private Integer itemId;
	// 商品名
	private String name;
	// 商品サイズ
	private String size;
	// 商品画像
	private String imagePath;
	// トッピングのList
	private List<Topping> toppingList = new java.util.ArrayList<>();
	// 数量
	private Integer quantity;
	// 商品の元々の金額
	private Integer itemPrice;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Integer itemPrice) {
		this.itemPrice = itemPrice;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<Topping> getToppingList() {
		return toppingList;
	}

	public void setToppingList(List<Topping> topping) {
		this.toppingList = topping;
	}

	public Integer getSubTotal() {
		if (size == null || itemPrice == null || quantity == null)
			return 0;
		int toppingCount = (toppingList != null) ? toppingList.size() : 0;
		if (size.equals("M")) {
			return (this.getItemPrice() + (toppingCount * 200)) * this.getQuantity();
		} else {
			return (this.getItemPrice() + (toppingCount * 300)) * this.getQuantity();
		}
	}

	/**
	 * 税抜き価格の小計を返す.
	 * 
	 * @return 税抜き価格の小計
	 */
	public Integer getSubTotalBeforeTax() {
		return BigDecimal.valueOf(this.getSubTotal())
				.divide(BigDecimal.valueOf(1.1), 0, RoundingMode.DOWN)
				.intValue();
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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
		CartItem other = (CartItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		int toppingCount = (toppingList != null) ? toppingList.size() : 0;
		return "CartItem [id=" + id + ", cartId=" + cartId + ", itemId=" + itemId + ", name=" + name + ", size=" + size
				+ ", imagePath=" + imagePath + ", toppingList=" + toppingList + ", subTotalPrice=" + (toppingCount > 0 ? this.getSubTotal() : "N/A")
				+ ", quantity=" + quantity + ", itemPrice=" + itemPrice + "]";
	}

}
