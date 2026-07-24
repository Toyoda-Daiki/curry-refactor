package com.example.form;

import java.util.List;

/**
 * @author satakemisako
 * 
 * ショッピングカートに商品追加するためのフォームクラス
 *
 */
public class ItemCartInForm {

	//商品名
	private String name;
	//商品サイズ
	private String size;
	//Mサイズの本体の値段
	private Integer priceM;
	//Lサイズの本体の値段
	private Integer priceL;
	
	//トッピングのリスト作成用のインデックス
	private List<String> toppingIndex;
	//数量
	private Integer quantity;
	//商品id
	private Integer id;
	//image
	private String imagePath;
	
	
	
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
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
	public List<String> getToppingIndex() {
		return toppingIndex;
	}
	public void setToppingIndex(List<String> toppingIndex) {
		this.toppingIndex = toppingIndex;
	}
	
	
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "ItemCartInForm [name=" + name + ", size=" + size + ", priceM=" + priceM + ", priceL=" + priceL
				+ ", toppingIndex=" + toppingIndex + ", quantity=" + quantity + ", id=" + id + ", imagePath="
				+ imagePath + "]";
	}
}
