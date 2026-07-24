package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CartItemTest {

    @Test
    @DisplayName("Mサイズの税抜き小計計算テスト")
    public void testGetSubTotalBeforeTaxM() {
        CartItem cartItem = new CartItem();
        cartItem.setItemPrice(1100);
        cartItem.setSize("M");
        cartItem.setQuantity(1);
        cartItem.setToppingList(new ArrayList<>());

        // (1100 + 0) * 1 = 1100 (税込)
        // 1100 / 1.1 = 1000 (税抜)
        assertEquals(1000, cartItem.getSubTotalBeforeTax(), "Mサイズの税抜き小計が正しくありません");
    }

    @Test
    @DisplayName("Lサイズトッピングありの税抜き小計計算テスト")
    public void testGetSubTotalBeforeTaxLWithToppings() {
        CartItem cartItem = new CartItem();
        cartItem.setItemPrice(2000);
        cartItem.setSize("L");
        cartItem.setQuantity(1);
        
        List<Topping> toppings = new ArrayList<>();
        toppings.add(new Topping());
        cartItem.setToppingList(toppings);

        // (2000 + 1 * 300) * 1 = 2300 (税込)
        // 2300 / 1.1 = 2090 (税抜)
        assertEquals(2090, cartItem.getSubTotalBeforeTax(), "Lサイズ・トッピングありの税抜き小計が正しくありません");
    }

    @Test
    @DisplayName("null項目がある場合の小計計算テスト（防御コードの検証）")
    public void testGetSubTotalWithNulls() {
        CartItem cartItem = new CartItem();
        // 全てnullの場合、0を返すこと
        assertEquals(0, cartItem.getSubTotal(), "全てnullのときは0を返すべきです");

        cartItem.setSize("M");
        assertEquals(0, cartItem.getSubTotal(), "itemPriceがnullのときは0を返すべきです");

        cartItem.setItemPrice(1000);
        assertEquals(0, cartItem.getSubTotal(), "quantityがnullのときは0を返すべきです");

        cartItem.setQuantity(1);
        // toppingListはコンストラクタ(初期化)で空リストになっているはずなので、ここで計算ができる
        assertEquals(1000, cartItem.getSubTotal(), "項目が揃えば計算できるはずです");
    }
}
