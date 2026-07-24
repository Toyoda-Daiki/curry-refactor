package com.example.form;

/**
 * 配送先住所1件分のフォームクラス
 * リファクタリング課題#13 動的フォームの作成
 */
public class AddressItemForm {
  private String name;
  private String address;
  private String tel;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }
}
