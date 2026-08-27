package com.example.domain;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Orderのドメイン
 * 
 * @author naramasato
 *         リファクタリング課題#4 Builderパターンで安全なオブジェクト生成に変更
 */
public class Order {

	private Integer id;
	private Integer userId;
	private OrderStatus status;
	private Integer totalPrice;
	private Date orderDate;
	private String destinationName;
	private String destinationEmail;
	private String destinationZipcode;
	private String destinationAddress;
	private String destinationTel;
	private Timestamp deliveryTime;
	private Integer paymentMethod;
	private User user;
	private List<OrderItem> orderItemList;

	// Builderからのみ生成可能なprivateコンストラクタ
	private Order(Builder builder) {
		this.id = builder.id;
		this.userId = builder.userId;
		this.status = builder.status;
		this.totalPrice = builder.totalPrice;
		this.orderDate = builder.orderDate;
		this.destinationName = builder.destinationName;
		this.destinationEmail = builder.destinationEmail;
		this.destinationZipcode = builder.destinationZipcode;
		this.destinationAddress = builder.destinationAddress;
		this.destinationTel = builder.destinationTel;
		this.deliveryTime = builder.deliveryTime;
		this.paymentMethod = builder.paymentMethod;
		this.user = builder.user;
		this.orderItemList = builder.orderItemList;
	}

	// 既存コードとの互換性のため引数なしコンストラクタも残す
	// public Order() {
	// }

	// Builderを取得するstaticメソッド
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Integer id;
		private Integer userId;
		private OrderStatus status;
		private Integer totalPrice;
		private Date orderDate;
		private String destinationName;
		private String destinationEmail;
		private String destinationZipcode;
		private String destinationAddress;
		private String destinationTel;
		private Timestamp deliveryTime;
		private Integer paymentMethod;
		private User user;
		private List<OrderItem> orderItemList;

		public Builder id(Integer id) {
			this.id = id;
			return this;
		}

		public Builder userId(Integer userId) {
			this.userId = userId;
			return this;
		}

		public Builder status(OrderStatus status) {
			this.status = status;
			return this;
		}

		public Builder totalPrice(Integer totalPrice) {
			this.totalPrice = totalPrice;
			return this;
		}

		public Builder orderDate(Date orderDate) {
			this.orderDate = orderDate;
			return this;
		}

		public Builder destinationName(String destinationName) {
			this.destinationName = destinationName;
			return this;
		}

		public Builder destinationEmail(String destinationEmail) {
			this.destinationEmail = destinationEmail;
			return this;
		}

		public Builder destinationZipcode(String destinationZipcode) {
			this.destinationZipcode = destinationZipcode;
			return this;
		}

		public Builder destinationAddress(String destinationAddress) {
			this.destinationAddress = destinationAddress;
			return this;
		}

		public Builder destinationTel(String destinationTel) {
			this.destinationTel = destinationTel;
			return this;
		}

		public Builder deliveryTime(Timestamp deliveryTime) {
			this.deliveryTime = deliveryTime;
			return this;
		}

		public Builder paymentMethod(Integer paymentMethod) {
			this.paymentMethod = paymentMethod;
			return this;
		}

		public Builder user(User user) {
			this.user = user;
			return this;
		}

		public Builder orderItemList(List<OrderItem> orderItemList) {
			this.orderItemList = orderItemList;
			return this;
		}

		// 必須フィールドのバリデーションを行いOrderを生成
		public Order build() {
			if (userId == null)
				throw new IllegalStateException("userIdは必須です");
			if (status == null)
				throw new IllegalStateException("statusは必須です");
			if (destinationName == null)
				throw new IllegalStateException("destinationNameは必須です");
			if (destinationEmail == null)
				throw new IllegalStateException("destinationEmailは必須です");
			if (destinationZipcode == null)
				throw new IllegalStateException("destinationZipcodeは必須です");
			if (destinationAddress == null)
				throw new IllegalStateException("destinationAddressは必須です");
			if (destinationTel == null)
				throw new IllegalStateException("destinationTelは必須です");
			if (paymentMethod == null)
				throw new IllegalStateException("paymentMethodは必須です");
			return new Order(this);
		}
	}

	// ゲッター
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	// public void setUserId(Integer userId) {
	// 	this.userId = userId;
	// }

	public OrderStatus getStatus() {
		return status;
	}

	// リファクタリング課題#1 statusをOrderStatus enumに変更
	// public void setStatus(OrderStatus status) {
	// 	this.status = status;
	// }

	public Integer getTotalPrice() {
		return totalPrice;
	}

	// public void setTotalPrice(Integer totalPrice) {
	// 	this.totalPrice = totalPrice;
	// }

	public Date getOrderDate() {
		return orderDate;
	}

	// public void setOrderDate(Date orderDate) {
	// 	this.orderDate = orderDate;
	// }

	public String getDestinationName() {
		return destinationName;
	}

	// public void setDestinationName(String destinationName) {
	// 	this.destinationName = destinationName;
	// }

	public String getDestinationEmail() {
		return destinationEmail;
	}

	// public void setDestinationEmail(String destinationEmail) {
	// 	this.destinationEmail = destinationEmail;
	// }

	public String getDestinationZipcode() {
		return destinationZipcode;
	}

	// public void setDestinationZipcode(String destinationZipcode) {
	// 	this.destinationZipcode = destinationZipcode;
	// }

	public String getDestinationAddress() {
		return destinationAddress;
	}

	// public void setDestinationAddress(String destinationAddress) {
	// 	this.destinationAddress = destinationAddress;
	// }

	public String getDestinationTel() {
		return destinationTel;
	}

	// public void setDestinationTel(String destinationTel) {
	// 	this.destinationTel = destinationTel;
	// }

	public Timestamp getDeliveryTime() {
		return deliveryTime;
	}

	// public void setDeliveryTime(Timestamp deliveryTime) {
	// 	this.deliveryTime = deliveryTime;
	// }

	public Integer getPaymentMethod() {
		return paymentMethod;
	}

	// public void setPaymentMethod(Integer paymentMethod) {
	// 	this.paymentMethod = paymentMethod;
	// }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	// リファクタリング課題#20 防御的コピーを返すように変更
	public List<OrderItem> getOrderItemList() {
		if (orderItemList == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(orderItemList);
	}

	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
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
		Order other = (Order) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", userId=" + userId + ", status=" + status + ", totalPrice=" + totalPrice
				+ ", orderDate=" + orderDate + ", destinationName=" + destinationName + ", destinationEmail="
				+ destinationEmail + ", destinationZipcode=" + destinationZipcode + ", destinationAddress="
				+ destinationAddress + ", destinationTel=" + destinationTel + ", deliveryTime=" + deliveryTime
				+ ", paymentMethod=" + paymentMethod + ", user=" + user + ", orderItemList=" + orderItemList + "]";
	}
}

// package com.example.domain;

// import java.sql.Date;
// import java.sql.Timestamp;
// import java.util.Collections;
// import java.util.List;

// /**
// * Orderのドメイン
// *
// * @author naramasato
// *
// */
// public class Order {

// // id
// private Integer id;
// // ユーザーid
// private Integer userId;
// // 状態
// // リファクタリング課題#1 statusをOrderStatus enumに変更
// private OrderStatus status;
// // 合計金額
// private Integer totalPrice;
// // 注文日
// private Date orderDate;
// // 宛先氏名
// private String destinationName;
// // 宛先Eメール
// private String destinationEmail;
// // 宛先郵便番号
// private String destinationZipcode;
// // 宛先住所
// private String destinationAddress;
// // 宛先TEL
// private String destinationTel;
// // 配達時間
// private Timestamp deliveryTime;
// // 支払方法
// private Integer paymentMethod;
// // ユーザー
// private User user;
// // OrderItemのリスト
// private List<OrderItem> orderItemList;

// // ゲッターとセッター
// public Integer getId() {
// return id;
// }

// public void setId(Integer id) {
// this.id = id;
// }

// public Integer getUserId() {
// return userId;
// }

// public void setUserId(Integer userId) {
// this.userId = userId;
// }

// public OrderStatus getStatus() {
// return status;
// }

// public void setStatus(OrderStatus status) {
// this.status = status;
// }

// public Integer getTotalPrice() {
// return totalPrice;
// }

// public void setTotalPrice(Integer totalPrice) {
// this.totalPrice = totalPrice;
// }

// public Date getOrderDate() {
// return orderDate;
// }

// public void setOrderDate(Date orderDate) {
// this.orderDate = orderDate;
// }

// public String getDestinationName() {
// return destinationName;
// }

// public void setDestinationName(String destinationName) {
// this.destinationName = destinationName;
// }

// public String getDestinationEmail() {
// return destinationEmail;
// }

// public void setDestinationEmail(String destinationEmail) {
// this.destinationEmail = destinationEmail;
// }

// public String getDestinationZipcode() {
// return destinationZipcode;
// }

// public void setDestinationZipcode(String destinationZipcode) {
// this.destinationZipcode = destinationZipcode;
// }

// public String getDestinationAddress() {
// return destinationAddress;
// }

// public void setDestinationAddress(String destinationAddress) {
// this.destinationAddress = destinationAddress;
// }

// public String getDestinationTel() {
// return destinationTel;
// }

// public void setDestinationTel(String destinationTel) {
// this.destinationTel = destinationTel;
// }

// public Timestamp getDeliveryTime() {
// return deliveryTime;
// }

// public void setDeliveryTime(Timestamp deliveryTime) {
// this.deliveryTime = deliveryTime;
// }

// public Integer getPaymentMethod() {
// return paymentMethod;
// }

// public void setPaymentMethod(Integer paymentMethod) {
// this.paymentMethod = paymentMethod;
// }

// public User getUser() {
// return user;
// }

// public void setUser(User user) {
// this.user = user;
// }

// // リファクタリング課題#20 防御的コピーを返すように変更
// public List<OrderItem> getOrderItemList() {
// if (orderItemList == null) {
// return Collections.emptyList();
// }
// return Collections.unmodifiableList(orderItemList);
// }

// public void setOrderItemList(List<OrderItem> orderItemList) {
// this.orderItemList = orderItemList;
// }

// // リファクタリング課題#21 equals()とhashCode()をidのみで実装
// @Override
// public int hashCode() {
// final int prime = 31;
// int result = 1;
// result = prime * result + ((id == null) ? 0 : id.hashCode());
// return result;
// }

// @Override
// public boolean equals(Object obj) {
// if (this == obj)
// return true;
// if (obj == null)
// return false;
// if (getClass() != obj.getClass())
// return false;
// Order other = (Order) obj;
// if (id == null) {
// if (other.id != null)
// return false;
// } else if (!id.equals(other.id))
// return false;
// return true;
// }

// @Override
// public String toString() {
// return "Order [id=" + id + ", userId=" + userId + ", status=" + status + ",
// totalPrice=" + totalPrice
// + ", orderDate=" + orderDate + ", destinationName=" + destinationName + ",
// destinationEmail="
// + destinationEmail + ", destinationZipcode=" + destinationZipcode + ",
// destinationAddress="
// + destinationAddress + ", destinationTel=" + destinationTel + ",
// deliveryTime=" + deliveryTime
// + ", paymentMethod=" + paymentMethod + ", user=" + user + ", orderItemList="
// + orderItemList + "]";
// }

// }
