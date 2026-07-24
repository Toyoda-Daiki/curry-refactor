package com.example.form;

import java.util.List;

/**
 * 配送先住所一覧のフォームクラス
 * リファクタリング課題#13 動的フォームの作成
 */
public class AddressForm {
  private List<AddressItemForm> addresses;

  public List<AddressItemForm> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<AddressItemForm> addresses) {
    this.addresses = addresses;
  }
}
