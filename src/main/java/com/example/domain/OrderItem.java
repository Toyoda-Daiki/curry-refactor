package com.example.domain;

import java.util.List;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OrderItem {

	// id
	private Integer id;
	// 商品id
	private Integer itemId;
	// orderのid
	private Integer orderId;
	// 数量
	private Integer quantity;
	// サイズ
	private String size;
	// 小計
	// private Integer subTotal;
	// item
	private Item item;
	// 注文したトッピングのList
	private List<OrderTopping> orderTopping = new java.util.ArrayList<>();

	// ゲッターとセッター
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getItemId() {
		return itemId;
	}

	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<OrderTopping> getOrderTopping() {
		return orderTopping;
	}

	public void setOrderTopping(List<OrderTopping> orderTopping) {
		this.orderTopping = orderTopping;
	}

	// リファクタリング課題#17 オブジェクト生成の責務（Factory Method）
	public static OrderItem from(CartItem cartItem, Integer orderId) {
		OrderItem orderItem = new OrderItem();
		orderItem.itemId = cartItem.getItemId();
		orderItem.quantity = cartItem.getQuantity();
		orderItem.size = cartItem.getSize();
		orderItem.orderId = orderId;
		return orderItem;
		// idは意図的にセットしない（nullのまま） → setId(null)という後付けの帳尻合わせが不要になる
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
		OrderItem other = (OrderItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", itemId=" + itemId + ", orderId=" + orderId + ", quantity=" + quantity
				+ ", size=" + size + ", item=" + item + ", ordertopping=" + orderTopping
				+ "]";
	}

	public Integer getSubTotal() {
		if (size == null || item == null || quantity == null)
			return 0;
		int toppingCount = (orderTopping != null) ? orderTopping.size() : 0;
		if (size.equals("M")) {
			return (item.getPriceM() + (toppingCount * 200)) * this.getQuantity();
		} else {
			return (item.getPriceL() + (toppingCount * 300)) * this.getQuantity();
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

}
