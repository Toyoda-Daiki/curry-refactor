package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.CartTopping;
import com.example.domain.Topping;
import com.example.form.ItemCartInForm;
import com.example.repository.CartItemRepository;
import com.example.repository.CartRepository;
import com.example.repository.CartToppingRepository;

/**
 * {@link CartService} の単体テストクラス。
 *
 * <p>
 * {@link CartService} は外部依存を持たないため、Mockitoによるモック化は不要。
 * ビジネスロジックを直接単体で検証する。
 * </p>
 *
 * <p>
 * テスト対象メソッド一覧:
 * </p>
 * <ul>
 * <li>{@link CartService#getPriceSize(ItemCartInForm)}</li>
 * <li>{@link CartService#getToppingIndex(List, List)}</li>
 * <li>{@link CartService#calcTotal(List)}</li>
 * </ul>
 *
 * @author toyodadaiki
 *
 * @see CartService
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private CartRepository cartRepository;

  @Mock
  private CartItemRepository cartItemRepository;

  @Mock
  private CartToppingRepository cartToppingRepository;

  /**
   * テスト対象の {@link CartService}。
   */
  @InjectMocks
  private CartService cartService;

  /**
   * {@link CartService#setupForm()} のテストグループ。
   *
   * <p>
   * {@link ItemCartInForm} のインスタンスを生成して返す処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("setupForm() のテスト")
  class SetupForm {

    /**
     * 戻り値が {@code null} でなく、{@link ItemCartInForm} のインスタンスであることを確認する。
     */
    @Test
    @DisplayName("正常系: ItemCartInFormのインスタンスが返される")
    void returnItemCartInFormInstance() {
      ItemCartInForm result = cartService.setupForm();

      assertNotNull(result);
      assertInstanceOf(ItemCartInForm.class, result);
    }
  }

  /**
   * {@link CartService#getPriceSize(ItemCartInForm)} のテストグループ。
   *
   * <p>
   * サイズに応じた商品金額を返す処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("getPriceSize() のテスト")
  class GetPriceSize {

    /**
     * サイズが {@code "M"} の場合、Mサイズの金額が返されることを確認する。
     */
    @Test
    @DisplayName("サイズがMのとき、priceMを返す")
    void returnPriceM_whenSizeIsM() {
      ItemCartInForm form = new ItemCartInForm();
      form.setSize("M");
      form.setPriceM(1000);
      form.setPriceL(1500);

      Integer result = cartService.getPriceSize(form);

      assertEquals(1000, result);
    }

    /**
     * サイズが {@code "L"} の場合、Lサイズの金額が返されることを確認する。
     */
    @Test
    @DisplayName("サイズがLのとき、priceLを返す")
    void returnPriceL_whenSizeIsL() {
      ItemCartInForm form = new ItemCartInForm();
      form.setSize("L");
      form.setPriceM(1000);
      form.setPriceL(1500);

      Integer result = cartService.getPriceSize(form);

      assertEquals(1500, result);
    }
  }

  /**
   * {@link CartService#getToppingIndex(List, List)} のテストグループ。
   *
   * <p>
   * トッピング一覧とインデックスリストをもとに、選択済みトッピングのリストを返す処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("getToppingIndex() のテスト")
  class GetToppingIndex {

    /**
     * {@code toppingIndex} が {@code null} の場合、空リストが返されることを確認する。
     */
    @Test
    @DisplayName("toppingIndexがnullのとき、空リストが返される")
    void returnEmptyList_whenToppingIndexIsNull() {
      List<Topping> toppingList = createSampleToppingList();

      List<Topping> result = cartService.getToppingIndex(toppingList, null);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }

    /**
     * {@code toppingList} が {@code null} の場合、空リストが返されることを確認する。
     */
    @Test
    @DisplayName("toppingListがnullのとき、空リストが返される")
    void returnEmptyList_whenToppingListIsNull() {
      List<String> toppingIndex = List.of("0");

      List<Topping> result = cartService.getToppingIndex(null, toppingIndex);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }

    /**
     * {@code toppingIndex} に1件のインデックスが含まれる場合、
     * 対応するトッピングが1件返されることを確認する。
     */
    @Test
    @DisplayName("toppingIndexが1件のとき、対応するトッピングが1件返される")
    void returnOneToping_whenToppingIndexHasOneElement() {
      List<Topping> toppingList = createSampleToppingList();

      List<Topping> result = cartService.getToppingIndex(toppingList, List.of("0"));

      assertNotNull(result);
      assertEquals(1, result.size());
      assertEquals(toppingList.get(0), result.get(0));
    }

    /**
     * {@code toppingIndex} に複数のインデックスが含まれる場合、
     * 対応する複数のトッピングが返されることを確認する。
     */
    @Test
    @DisplayName("toppingIndexが複数のとき、対応するトッピングが複数返される")
    void returnMultipleToppings_whenToppingIndexHasMultipleElements() {
      List<Topping> toppingList = createSampleToppingList();

      List<Topping> result = cartService.getToppingIndex(toppingList, List.of("0", "1"));

      assertNotNull(result);
      assertEquals(2, result.size());
      assertEquals(toppingList.get(0), result.get(0));
      assertEquals(toppingList.get(1), result.get(1));
    }
  }

  /**
   * {@link CartService#calcTotal(List)} のテストグループ。
   *
   * <p>
   * カートアイテムの小計合計金額を計算する処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("calcTotal() のテスト")
  class CalcTotal {

    /**
     * カートアイテムが1件の場合、その小計金額が合計として返されることを確認する。
     */
    @Test
    @DisplayName("CartItemが1件のとき、小計金額が合計として返される")
    void returnSubTotal_whenOneCartItem() {
      CartItem cartItem = createSampleCartItem("M", 1000, 1, new LinkedList<>());
      // Mサイズ・トッピングなし・数量1 → (1000 + 0) * 1 = 1000

      Integer result = cartService.calcTotal(List.of(cartItem));

      assertEquals(1000, result);
    }

    /**
     * カートアイテムが複数件の場合、各小計の合計金額が返されることを確認する。
     */
    @Test
    @DisplayName("CartItemが複数件のとき、小計の合計金額が返される")
    void returnSumOfSubTotals_whenMultipleCartItems() {
      CartItem cartItem1 = createSampleCartItem("M", 1000, 1, new LinkedList<>());
      // Mサイズ・トッピングなし・数量1 → 1000
      CartItem cartItem2 = createSampleCartItem("L", 1500, 2, new LinkedList<>());
      // Lサイズ・トッピングなし・数量2 → 1500 * 2 = 3000

      Integer result = cartService.calcTotal(List.of(cartItem1, cartItem2));

      assertEquals(4000, result);
    }

    /**
     * カートアイテムにトッピングが含まれる場合、トッピング料金が加算された合計金額が返されることを確認する。
     */
    @Test
    @DisplayName("トッピングありのCartItemのとき、トッピング料金が加算された合計金額が返される")
    void returnTotalWithToppingPrice_whenCartItemHasTopping() {
      Topping topping = new Topping();
      List<Topping> toppings = List.of(topping);
      CartItem cartItem = createSampleCartItem("M", 1000, 1, toppings);
      // Mサイズ・トッピング1件(200円)・数量1 → (1000 + 200) * 1 = 1200

      Integer result = cartService.calcTotal(List.of(cartItem));

      assertEquals(1200, result);
    }

    /**
     * カートアイテムが空リストの場合、合計金額が {@code 0} で返されることを確認する。
     */
    @Test
    @DisplayName("CartItemが空リストのとき、0が返される")
    void returnZero_whenCartItemListIsEmpty() {
      Integer result = cartService.calcTotal(new LinkedList<>());

      assertEquals(0, result);
    }
  }

  // =========================================================
  // ヘルパー
  // =========================================================

  /**
   * テスト用のサンプルトッピングリストを生成するヘルパーメソッド。
   *
   * @return 2件のダミートッピングを持つリスト
   */
  private List<Topping> createSampleToppingList() {
    Topping topping1 = new Topping();
    topping1.setId(1);
    Topping topping2 = new Topping();
    topping2.setId(2);
    return List.of(topping1, topping2);
  }

  /**
   * テスト用のサンプル {@link CartItem} を生成するヘルパーメソッド。
   *
   * @param size      商品サイズ（{@code "M"} または {@code "L"}）
   * @param itemPrice 商品の元々の金額
   * @param quantity  数量
   * @param toppings  トッピングリスト
   * @return ダミーデータが設定された {@link CartItem} オブジェクト
   */
  private CartItem createSampleCartItem(String size, Integer itemPrice, Integer quantity, List<Topping> toppings) {
    CartItem cartItem = new CartItem();
    cartItem.setSize(size);
    cartItem.setItemPrice(itemPrice);
    cartItem.setQuantity(quantity);
    cartItem.setToppingList(toppings);
    return cartItem;
  }

  @Nested
  @DisplayName("追加のテスト")
  class AdditionalTests {

    @Test
    @DisplayName("setupForm: 新規インスタンスのフィールドが初期値であることを確認")
    void setupForm_checkDefaults() {
      ItemCartInForm form = cartService.setupForm();
      assertNull(form.getSize());
      assertNull(form.getPriceM());
    }

    @Test
    @DisplayName("getPriceSize: サイズが小文字 'm' の場合はLサイズ扱いになる（現状の仕様確認）")
    void getPriceSize_lowerCaseM() {
      ItemCartInForm form = new ItemCartInForm();
      form.setSize("m");
      form.setPriceM(100);
      form.setPriceL(200);
      assertEquals(200, cartService.getPriceSize(form));
    }

    @Test
    @DisplayName("getToppingIndex: toppingIndexが空リストのとき、空リストが返される")
    void getToppingIndex_emptyIndexList() {
      List<Topping> result = cartService.getToppingIndex(new LinkedList<>(), new LinkedList<>());
      assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getToppingIndex: インデックスの順番通りにトッピングが格納されること")
    void getToppingIndex_preserveOrder() {
      List<Topping> toppingList = createSampleToppingList();
      List<Topping> result = cartService.getToppingIndex(toppingList, List.of("1", "0"));
      assertEquals(toppingList.get(1), result.get(0));
      assertEquals(toppingList.get(0), result.get(1));
    }

    @Test
    @DisplayName("getToppingIndex: 重複したインデックスが指定された場合、同じトッピングが複数格納される")
    void getToppingIndex_duplicateIndices() {
      List<Topping> toppingList = createSampleToppingList();
      List<Topping> result = cartService.getToppingIndex(toppingList, List.of("0", "0"));
      assertEquals(2, result.size());
      assertEquals(toppingList.get(0), result.get(0));
      assertEquals(toppingList.get(0), result.get(1));
    }

    @Test
    @DisplayName("calcTotal: 0円の商品が含まれる場合")
    void calcTotal_withZeroPriceItem() {
      CartItem freeItem = createSampleCartItem("M", 0, 1, new LinkedList<>());
      CartItem paidItem = createSampleCartItem("M", 1000, 1, new LinkedList<>());
      assertEquals(1000, cartService.calcTotal(List.of(freeItem, paidItem)));
    }

    @Test
    @DisplayName("calcTotal: 大量の数量が含まれる場合")
    void calcTotal_largeQuantity() {
      CartItem item = createSampleCartItem("M", 1000, 100, new LinkedList<>());
      assertEquals(100000, cartService.calcTotal(List.of(item)));
    }

    @Test
    @DisplayName("getPriceSize: サイズがnullの場合、NullPointerExceptionが発生することを仕様として確認")
    void getPriceSize_nullSize() {
      ItemCartInForm form = new ItemCartInForm();
      form.setSize(null);
      assertThrows(NullPointerException.class, () -> cartService.getPriceSize(form));
    }

    @Test
    @DisplayName("getOrCreateCart(userId): 1引数バージョンが正しく動作すること")
    void getOrCreateCart_UserIdOnly() {
        Cart cart = new Cart();
        when(cartRepository.findByUserId(1)).thenReturn(cart);
        
        Cart result = cartService.getOrCreateCart(1);
        
        assertEquals(cart, result);
        verify(cartRepository).findByUserId(1);
    }

    @Test
    @DisplayName("getToppingIndex: 存在しないインデックスが指定された場合、IndexOutOfBoundsExceptionが発生する")
    void getToppingIndex_outOfBounds() {
      List<Topping> toppingList = createSampleToppingList();
      assertThrows(IndexOutOfBoundsException.class, () -> cartService.getToppingIndex(toppingList, List.of("99")));
    }

    @Test
    @DisplayName("getToppingIndex: 数値以外のインデックスが指定された場合、NumberFormatExceptionが発生する")
    void getToppingIndex_nonNumeric() {
      List<Topping> toppingList = createSampleToppingList();
      assertThrows(NumberFormatException.class, () -> cartService.getToppingIndex(toppingList, List.of("abc")));
    }
    @Test
    @DisplayName("getOrCreateCart: ユーザーIDがnullでセッションIDが指定された場合、セッションIDで検索されること")
    void getOrCreateCart_withSessionId() {
        Cart cart = new Cart();
        when(cartRepository.findBySessionId("session-123")).thenReturn(cart);
        
        Cart result = cartService.getOrCreateCart(null, "session-123");
        
        assertEquals(cart, result);
        verify(cartRepository).findBySessionId("session-123");
    }

    @Test
    @DisplayName("getOrCreateCart: カートが存在しない場合、新規作成して保存されること(セッションIDあり)")
    void getOrCreateCart_createNewWithSessionId() {
        when(cartRepository.findBySessionId("session-456")).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Cart result = cartService.getOrCreateCart(null, "session-456");
        
        assertNotNull(result);
        assertEquals("session-456", result.getSessionId());
        assertNull(result.getUserId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("addItemToCart: トッピングリストがある場合に正しく保存されること")
    void addItemToCart_WithToppings() {
        Cart cart = new Cart();
        cart.setId(10);
        CartItem item = new CartItem();
        item.setId(20);
        Topping t1 = new Topping();
        t1.setId(100);
        Topping t2 = new Topping();
        t2.setId(101);
        item.setToppingList(List.of(t1, t2));
        
        cartService.addItemToCart(cart, item);
        
        assertEquals(10, item.getCartId());
        verify(cartItemRepository).save(item);
        verify(cartToppingRepository, times(2)).save(any(CartTopping.class));
    }

    @Test
    @DisplayName("addItemToCart: トッピングリストがnullの場合でも商品が保存されること")
    void addItemToCart_NoTopping() {
        Cart cart = new Cart();
        cart.setId(10);
        CartItem item = new CartItem();
        item.setId(20);
        item.setToppingList(null);
        
        cartService.addItemToCart(cart, item);
        
        assertEquals(10, item.getCartId());
        verify(cartItemRepository).save(item);
        verify(cartToppingRepository, never()).save(any(CartTopping.class));
    }
    
    @Test
    @DisplayName("addItemToCart: トッピングリストが空の場合でも商品が保存されること")
    void addItemToCart_EmptyTopping() {
        Cart cart = new Cart();
        cart.setId(10);
        CartItem item = new CartItem();
        item.setId(20);
        item.setToppingList(new ArrayList<>());
        
        cartService.addItemToCart(cart, item);
        
        assertEquals(10, item.getCartId());
        verify(cartItemRepository).save(item);
        verify(cartToppingRepository, never()).save(any(CartTopping.class));
    }

    @Test
    @DisplayName("findItemsByCartId: 商品と関連するトッピングが取得されること")
    void findItemsByCartId_Success() {
        CartItem item = new CartItem();
        item.setId(1);
        when(cartItemRepository.findByCartId(10)).thenReturn(List.of(item));
        
        CartTopping ct = new CartTopping();
        Topping topping = new Topping();
        topping.setName("コーン");
        ct.setTopping(topping);
        when(cartToppingRepository.findByCartItemId(1)).thenReturn(List.of(ct));
        
        List<CartItem> result = cartService.findItemsByCartId(10);
        
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getToppingList().size());
        assertEquals("コーン", result.get(0).getToppingList().get(0).getName());
    }

    @Test
    @DisplayName("deleteItem: 商品が削除されること")
    void deleteItem_Success() {
        cartService.deleteItem(1);
        verify(cartItemRepository).deleteById(1);
    }

    @Test
    @DisplayName("mergeCart: 引数がnullの場合は何もしない")
    void mergeCart_NullArgs() {
        cartService.mergeCart(null, 10);
        verify(cartRepository, never()).findBySessionId(any());
        
        cartService.mergeCart("session", null);
        verify(cartRepository, never()).findBySessionId(any());
    }

    @Test
    @DisplayName("mergeCart: ゲストカートが存在しない場合は何もしない")
    void mergeCart_NoGuestCart() {
        when(cartRepository.findBySessionId("ghost")).thenReturn(null);
        cartService.mergeCart("ghost", 10);
        verify(cartRepository, never()).findByUserId(any());
    }

    @Test
    @DisplayName("mergeCart: ユーザーカートが存在しない場合、ゲストカートをユーザーに紐付ける")
    void mergeCart_NoUserCart() {
        Cart guestCart = new Cart();
        guestCart.setId(1);
        when(cartRepository.findBySessionId("guest")).thenReturn(guestCart);
        when(cartRepository.findByUserId(10)).thenReturn(null);
        
        cartService.mergeCart("guest", 10);
        
        assertEquals(10, guestCart.getUserId());
        verify(cartRepository).save(guestCart);
    }
    
    @Test
    @DisplayName("mergeCart: ゲストカートとユーザーカート両方がある場合、アイテムを移動する")
    void mergeCart_BothExist() {
        Cart guestCart = new Cart();
        guestCart.setId(1);
        Cart userCart = new Cart();
        userCart.setId(2);
        
        when(cartRepository.findBySessionId("guest")).thenReturn(guestCart);
        when(cartRepository.findByUserId(10)).thenReturn(userCart);
        
        CartItem item = new CartItem();
        item.setCartId(1);
        when(cartItemRepository.findByCartId(1)).thenReturn(List.of(item));
        
        cartService.mergeCart("guest", 10);
        
        assertEquals(2, item.getCartId());
        verify(cartItemRepository).save(item);
        verify(cartRepository).deleteById(1);
    }

    @Test
    @DisplayName("clearCart: カート内全アイテムとトッピング、カート本体が削除されること")
    void clearCart_Success() {
        CartItem item = new CartItem();
        item.setId(100);
        when(cartItemRepository.findByCartId(10)).thenReturn(List.of(item));
        
        cartService.clearCart(10);
        
        verify(cartToppingRepository).deleteByCartItemId(100);
        verify(cartItemRepository).deleteById(100);
        verify(cartRepository).deleteById(10);
    }
  }
}
