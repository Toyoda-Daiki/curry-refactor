package com.example.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrderItemTest {

    @Test
    @DisplayName("Mサイズの税抜き小計計算テスト")
    public void testGetSubTotalBeforeTaxM() {
        OrderItem orderItem = new OrderItem();
        Item item = new Item();
        item.setPriceM(1000);
        orderItem.setItem(item);
        orderItem.setSize("M");
        orderItem.setQuantity(1);
        orderItem.setOrderTopping(new ArrayList<>());

        // (1000 + 0) * 1 = 1000 (税込)
        // 1000 / 1.1 = 909 (税抜)
        assertEquals(909, orderItem.getSubTotalBeforeTax(), "Mサイズの税抜き小計が正しくありません");
    }

    @Test
    @DisplayName("Lサイズトッピングありの税抜き小計計算テスト")
    public void testGetSubTotalBeforeTaxLWithToppings() {
        OrderItem orderItem = new OrderItem();
        Item item = new Item();
        item.setPriceL(2000);
        orderItem.setItem(item);
        orderItem.setSize("L");
        orderItem.setQuantity(2);
        
        List<OrderTopping> toppings = new ArrayList<>();
        toppings.add(new OrderTopping());
        orderItem.setOrderTopping(toppings);

        // (2000 + 1 * 300) * 2 = 4600 (税込)
        // 4600 / 1.1 = 4181 (税抜)
        assertEquals(4181, orderItem.getSubTotalBeforeTax(), "Lサイズ・トッピングありの税抜き小計が正しくありません");
    }

    @Test
    @DisplayName("null項目がある場合の小計計算テスト（防御コードの検証）")
    public void testGetSubTotalWithNulls() {
        OrderItem orderItem = new OrderItem();
        // 全てnullの場合、0を返すこと
        assertEquals(0, orderItem.getSubTotal(), "全てnullのときは0を返すべきです");

        orderItem.setSize("M");
        assertEquals(0, orderItem.getSubTotal(), "itemがnullのときは0を返すべきです");

        Item item = new Item();
        item.setPriceM(1000);
        orderItem.setItem(item);
        assertEquals(0, orderItem.getSubTotal(), "quantityがnullのときは0を返すべきです");

        orderItem.setQuantity(1);
        // orderToppingはコンストラクタ(初期化)で空リストになっているはずなので、ここで計算ができる
        assertEquals(1000, orderItem.getSubTotal(), "項目が揃えば計算できるはずです");
    }
}
