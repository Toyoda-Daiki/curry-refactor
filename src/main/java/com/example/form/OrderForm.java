package com.example.form;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.repository.OrderRepository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class OrderForm {

	// 合計金額
	private Integer totalPrice;
	// お届け先 氏名
	@NotBlank(message = "{error.empty.name}")
	private String destinationName;
	// お届け先 メールアドレス
	@NotBlank(message = "{error.empty.email}")
	@Email(message = "{error.mail.format}")
	private String destinationEmail;
	// お届け先 郵便番号
	@NotBlank(message = "{error.empty.zipCode}")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{4}$", message = "{error.zipCode}")
	private String destinationZipcode;
	// お届け先 住所
	@NotBlank(message = "{error.empty.address}")
	private String destinationAddress;
	// お届け先 電話番号
	@NotBlank(message = "{error.empty.phone}")
	@Pattern(regexp = "^[0-9]+-[0-9]+-[0-9]+$", message = "{error.telephone}")
	private String destinationTel;
	// 日付
	private Date orderDate;
	// お届け時間
	@NotNull(message = "{error.empty.deliveryTime}")
	private String deliveryTime;
	// 支払い方法
	@NotNull(message = "{error.empty.paymentMethod}")
	private Integer paymentMethod;

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(OrderForm.class);

	// リファクタリング課題#9 e.printStackTrace()をlog.error()に変更、System.out.printlnを削除、nullではなく例外を投げるように変更
	public Timestamp getTimestamp() {

		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		java.util.Date date;
		try {
			date = sdf.parse(deliveryTime);
			Timestamp ts = new Timestamp(date.getTime());
			return ts;
		} catch (ParseException e) {
			log.error("配達時間のパースに失敗しました: deliveryTime={}", deliveryTime, e);
			throw new IllegalArgumentException("配達時間の形式が不正です: " + deliveryTime, e);
		}
	}

	public Integer getIntegerDeliveryTime() {
		return Integer.parseInt(deliveryTime);
	}

	// ゲッターセッター
	public Integer getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Integer totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public String getDestinationEmail() {
		return destinationEmail;
	}

	public void setDestinationEmail(String destinationEmail) {
		this.destinationEmail = destinationEmail;
	}

	public String getDestinationZipcode() {
		return destinationZipcode;
	}

	public void setDestinationZipcode(String destinationZipcode) {
		this.destinationZipcode = destinationZipcode;
	}

	public String getDestinationAddress() {
		return destinationAddress;
	}

	public void setDestinationAddress(String destinationAddress) {
		this.destinationAddress = destinationAddress;
	}

	public String getDestinationTel() {
		return destinationTel;
	}

	public void setDestinationTel(String destinationTel) {
		this.destinationTel = destinationTel;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public Integer getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Override
	public String toString() {
		return "OrderForm [totalPrice=" + totalPrice + ", destinationName=" + destinationName + ", destinationEmail="
				+ destinationEmail + ", destinationZipcode=" + destinationZipcode + ", destinationAddress="
				+ destinationAddress + ", destinationTel=" + destinationTel + ", orderDate=" + orderDate
				+ ", deliveryTime=" + deliveryTime + ", paymentMethod=" + paymentMethod + "]";
	}
}
