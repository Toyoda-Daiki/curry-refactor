package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.Item;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.Topping;
import com.example.domain.OrderStatus;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.OrderToppingRepository;
import com.example.repository.ToppingRepository;

/**
 * {@link OrderService} の単体テストクラス。
 *
 * <p>
 * Mockito を使用して依存するリポジトリ・メール送信機能をモック化し、
 * {@code OrderService} のビジネスロジックを単体で検証する。
 * </p>
 *
 * <p>
 * テスト対象メソッド一覧:
 * </p>
 * <ul>
 * <li>{@link OrderService#orderLoad(int)}</li>
 * <li>{@link OrderService#findByOrder(int)}</li>
 * <li>{@link OrderService#paymentMethodJudge(Order)}</li>
 * <li>{@link OrderService#order(Order, Integer)}</li>
 * <li>{@link OrderService#sendMail(String, Integer)}</li>
 * </ul>
 *
 * @author toyodadaiki
 * @see OrderService
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  /** 注文情報を操作するリポジトリのモック。 */
  @Mock
  private OrderRepository orderRepository;

  /** 注文明細を操作するリポジトリのモック。 */
  @Mock
  private OrderItemRepository orderItemRepository;

  /** 注文トッピングを操作するリポジトリのモック。 */
  @Mock
  private OrderToppingRepository orderToppingRepository;

  /** メール送信機能のモック。 */
  @Mock
  private MailSender sender;

  /**
   * テスト対象の {@link OrderService}。
   * モックが自動的に注入される。
   */
  @Mock
  private ToppingRepository toppingRepository;

  @Mock
  private CartService cartService;

  @Mock
  private ResourceLoader resourceLoader;

  /**
   * テスト対象の {@link OrderService}。
   * モックが自動的に注入される。
   */
  @InjectMocks
  private OrderService orderService;

  @org.junit.jupiter.api.BeforeEach
  void initFields() {
    ReflectionTestUtils.setField(orderService, "mailFrom", "noreply@example.com");
    ReflectionTestUtils.setField(orderService, "mailSubject", "ご注文ありがとうございます");
  }

  private void mockResourceLoader() throws Exception {
    Resource mockResource = mock(Resource.class);
    String template = "関係者様\n注文日時: ${orderDate}\n配達日時: ${deliveryDate}" +
        "\n商品: ${orderItemsText}\n郵便番号: ${zipCode}" +
        "\n住所: ${address}\nお名前: ${customerName}" +
        "\n支払い: ${paymentMethod}\n合計: ${totalAmount}";
    lenient().when(mockResource.getInputStream()).thenReturn(
        new java.io.ByteArrayInputStream(template.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
    lenient().when(resourceLoader.getResource("classpath:mail-template.txt")).thenReturn(mockResource);
  }

  /**
   * {@link OrderService#orderLoad(int)} のテストグループ。
   *
   * <p>
   * 指定されたユーザーIDに紐づく注文一覧を取得する処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("orderLoad() のテスト")
  class OrderLoad {

    /**
     * リポジトリの {@code orderLoad} が呼び出され、その結果がそのまま返されることを確認する。
     */
    @Test
    @DisplayName("正常系: orderRepositoryのorderLoadを呼び出し結果を返す")
    void delegateToRepository() {
      List<Order> expected = List.of(createSampleOrder(1));
      when(orderRepository.orderLoad(1)).thenReturn(expected);

      List<Order> result = orderService.orderLoad(1);

      assertEquals(expected, result);
      verify(orderRepository, times(1)).orderLoad(1);
    }

    /**
     * リポジトリが空リストを返した場合、空リストがそのまま返されることを確認する。
     */
    @Test
    @DisplayName("正常系: 空リストが返る場合もそのまま返す")
    void returnEmptyList_whenRepositoryReturnsEmpty() {
      when(orderRepository.orderLoad(999)).thenReturn(List.of());

      List<Order> result = orderService.orderLoad(999);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }
  }

  /**
   * {@link OrderService#findByOrder(int)} のテストグループ。
   *
   * <p>
   * テーブルIDに紐づく注文一覧を取得する処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("findByOrder() のテスト")
  class FindByOrder {

    /**
     * リポジトリの {@code findByOrdertable} が呼び出され、その結果がそのまま返されることを確認する。
     */
    @Test
    @DisplayName("正常系: orderRepositoryのfindByOrdertableを呼び出し結果を返す")
    void delegateToRepository() {
      List<Order> expected = List.of(createSampleOrder(1), createSampleOrder(2));
      when(orderRepository.findByOrdertable(10)).thenReturn(expected);

      List<Order> result = orderService.findByOrder(10);

      assertEquals(expected, result);
      verify(orderRepository, times(1)).findByOrdertable(10);
    }

    /**
     * リポジトリが空リストを返した場合、空リストがそのまま返されることを確認する。
     */
    @Test
    @DisplayName("正常系: 空リストが返る場合もそのまま返す")
    void returnEmptyList_whenRepositoryReturnsEmpty() {
      when(orderRepository.findByOrdertable(999)).thenReturn(List.of());

      List<Order> result = orderService.findByOrder(999);

      assertNotNull(result);
      assertTrue(result.isEmpty());
    }
  }

  /**
   * {@link OrderService#paymentMethodJudge(Order)} のテストグループ。
   *
   * <p>
   * 支払い方法に応じたステータス値を返す処理を検証する。
   * </p>
   */
  @Nested
  @DisplayName("paymentMethodJudge() のテスト")
  class PaymentMethodJudge {

    /**
     * 支払い方法が {@code 1}（クレジットカード等）の場合、ステータス {@code 1} が返されることを確認する。
     */
    @Test
    @DisplayName("支払い方法が1のとき、statusが1を返す")
    void returnStatus1_whenPaymentMethodIs1() {
      Order order = createSampleOrder(1);
      order.setPaymentMethod(1);

      OrderStatus result = orderService.paymentMethodJudge(order);

      assertEquals(OrderStatus.PAID, result);
    }

    /**
     * 支払い方法が {@code 2}（代金引換等）の場合、ステータス {@code 2} が返されることを確認する。
     */
    @Test
    @DisplayName("支払い方法が2のとき、statusが2を返す")
    void returnStatus2_whenPaymentMethodIs2() {
      Order order = createSampleOrder(1);
      order.setPaymentMethod(2);

      OrderStatus result = orderService.paymentMethodJudge(order);

      assertEquals(OrderStatus.CASH_ON_DELIVERY, result);
    }

    /**
     * 支払い方法が {@code 1} 以外の未定義値の場合、デフォルトとしてステータス {@code 2} が返されることを確認する。
     */
    @Test
    @DisplayName("支払い方法が1以外の場合、statusが2を返す")
    void returnStatus2_whenPaymentMethodIsOther() {
      Order order = createSampleOrder(1);
      order.setPaymentMethod(99);

      OrderStatus result = orderService.paymentMethodJudge(order);

      assertEquals(OrderStatus.CASH_ON_DELIVERY, result);
    }
  }

  /**
   * {@link OrderService#order(Order, Integer, List)} のテストグループ。
   *
   * <p>
   * セッション依存を排除し、引数で渡されたデータに基づく注文確定処理全体を検証する。
   * </p>
   */
  @Nested
  @DisplayName("order() のテスト")
  class OrderMethod {

    @Test
    @DisplayName("正常系: statusとuserIdがセットされorderRepositoryのinsertが呼ばれる")
    void setStatusAndUserIdAndCallInsert() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      Integer userId = 10;
      List<CartItem> cartItemList = new ArrayList<>();

      when(orderRepository.insert(order)).thenReturn(100);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(userId)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(cartItemList);

      orderService.order(order, userId);

      assertEquals(OrderStatus.PAID, order.getStatus());
      assertEquals(10, order.getUserId());
      verify(orderRepository, times(1)).insert(order);
    }

    @Test
    @DisplayName("正常系: トッピングなしのCartItemでorderItemRepositoryが呼ばれる")
    void callOrderItemRepository_whenCartItemHasNoTopping() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(2);

      Integer userId = 5;
      CartItem cartItem = new CartItem();
      cartItem.setToppingList(new ArrayList<>());
      List<CartItem> cartItemList = List.of(cartItem);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(userId)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(cartItemList);

      orderService.order(order, userId);

      verify(orderItemRepository, times(1)).order(any(OrderItem.class));
      verify(orderToppingRepository, never()).insert(any(OrderTopping.class));
    }

    /**
     * トッピングありの {@link CartItem} が存在する場合、
     * トッピングの件数分だけ {@link OrderToppingRepository#insert(OrderTopping)}
     * が呼び出されることを確認する。
     */
    @Test
    @DisplayName("正常系: トッピングありのCartItemでorderToppingRepositoryが呼ばれる")
    void callOrderToppingRepository_whenCartItemHasTopping() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      Integer userId = 5;
      Topping topping1 = new Topping();
      topping1.setId(1);
      Topping topping2 = new Topping();
      topping2.setId(2);

      CartItem cartItem = new CartItem();
      cartItem.setToppingList(List.of(topping1, topping2));
      List<CartItem> cartItemList = List.of(cartItem);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(userId)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(cartItemList);

      orderService.order(order, userId);

      verify(orderToppingRepository, times(2)).insert(any(OrderTopping.class));
    }

    /**
     * 複数の {@link CartItem} が存在する場合、
     * カートアイテムの件数分だけ {@link OrderItemRepository#order(OrderItem)} が呼び出されることを確認する。
     */
    @Test
    @DisplayName("正常系: CartItemが複数の場合、件数分orderItemRepositoryが呼ばれる")
    void callOrderItemRepository_forEachCartItem() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      Integer userId = 5;
      CartItem cartItem1 = new CartItem();
      cartItem1.setToppingList(new ArrayList<>());
      CartItem cartItem2 = new CartItem();
      cartItem2.setToppingList(new ArrayList<>());

      List<CartItem> cartItemList = List.of(cartItem1, cartItem2);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(userId)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(cartItemList);

      orderService.order(order, userId);

      verify(orderItemRepository, times(2)).order(any(OrderItem.class));
    }

    /**
     * 在庫不足が発生した場合に例外が伝播することを確認する。
     */
    @Test
    @DisplayName("異常系: 在庫不足時にStockShortageExceptionがスローされる")
    void throwStockShortageException_whenStockIsInsufficient() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      Integer userId = 5;
      Topping topping = new Topping();
      topping.setId(1);
      CartItem cartItem = new CartItem();
      cartItem.setToppingList(List.of(topping));
      List<CartItem> cartItemList = List.of(cartItem);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(userId)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(cartItemList);
      when(orderItemRepository.order(any())).thenReturn(1);

      // トッピングの在庫減算時に例外を投げるように設定
      doThrow(new com.example.exception.StockShortageException("在庫不足"))
          .when(toppingRepository).decrementStock(eq(1), any());

      assertThrows(com.example.exception.StockShortageException.class, () -> {
        orderService.order(order, userId);
      });
    }
  }

  /**
   * {@link OrderService#sendMail(String)} のテストグループ。
   *
   * <p>
   * 注文完了メールの送信処理（宛先・送信元・件名・本文）を検証する。
   * </p>
   */
  @Nested
  @DisplayName("sendMail() のテスト")
  class SendMail {

    @org.junit.jupiter.api.BeforeEach
    void setUp() throws Exception {
      lenient().when(orderRepository.orderLoad(anyInt())).thenReturn(List.of(createSampleOrder(1)));
      lenient().when(orderItemRepository.findByOrderId(any())).thenReturn(new ArrayList<>());
    }

    @Test
    @DisplayName("正常系: MailSenderのsendが1回呼ばれる")
    void callSenderSendOnce() throws Exception {
      mockResourceLoader();
      orderService.sendMail("test@example.com", 1);
      verify(sender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("正常系: 送信先メールアドレスが正しくセットされる")
    void setCorrectToAddress() throws Exception {
      mockResourceLoader();
      ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("user@example.com", 1);
      verify(sender).send(captor.capture());
      assertArrayEquals(new String[] { "user@example.com" }, captor.getValue().getTo());
    }

    @Test
    @DisplayName("正常系: 送信元アドレスが正しくセットされる")
    void setCorrectFromAddress() throws Exception {
      mockResourceLoader();
      ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("user@example.com", 1);
      verify(sender).send(captor.capture());
      // mailFrom is injected via @Value; default is noreply@example.com
      assertNotNull(captor.getValue().getFrom());
    }

    @Test
    @DisplayName("正常系: 件名が正しくセットされる")
    void setCorrectSubject() throws Exception {
      mockResourceLoader();
      ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("user@example.com", 1);
      verify(sender).send(captor.capture());
      assertNotNull(captor.getValue().getSubject());
    }

    @Test
    @DisplayName("正常系: 本文が正しくセットされる")
    void setCorrectText() throws Exception {
      mockResourceLoader();
      ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("user@example.com", 1);
      verify(sender).send(captor.capture());
      assertNotNull(captor.getValue().getText());
    }

    @Test
    @DisplayName("正常系: 商品リストがnullの場合でもメール送信ができる")
    void sendMail_NullOrderList() throws Exception {
      mockResourceLoader();
      Order order = createSampleOrder(1);
      order.setOrderItemList(null);
      lenient().when(orderRepository.orderLoad(1)).thenReturn(List.of(order));

      orderService.sendMail("test@example.com", 1);
      verify(sender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("sendMail: トッピングがある場合にインデントされて表示されること")
    void sendMail_WithToppings() throws Exception {
      mockResourceLoader();
      Order order = createSampleOrder(1);
      OrderItem item = new OrderItem();
      item.setQuantity(1);
      item.setSize("M");
      Item product = new Item();
      product.setName("テストカレー");
      item.setItem(product);

      OrderTopping ot = new OrderTopping();
      Topping t = new Topping();
      t.setName("福神漬け");
      ot.setTopping(t);
      item.setOrderTopping(List.of(ot));
      order.setOrderItemList(List.of(item));

      when(orderRepository.orderLoad(1)).thenReturn(List.of(order));

      ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("test@example.com", 1);

      verify(sender).send(messageCaptor.capture());
      String text = messageCaptor.getValue().getText();
      assertTrue(text.contains("・テストカレー (M) 1個"));
      assertTrue(text.contains(" └ 福神漬け"));
    }

    @Test
    @DisplayName("sendMail: トッピングがない場合に商品名のみ表示されること")
    void sendMail_NoToppings() throws Exception {
      mockResourceLoader();
      Order order = createSampleOrder(1);
      OrderItem item = new OrderItem();
      item.setQuantity(1);
      item.setSize("L");
      Item product = new Item();
      product.setName("具なしカレー");
      item.setItem(product);
      item.setOrderTopping(new ArrayList<>()); // 空リスト
      order.setOrderItemList(List.of(item));

      when(orderRepository.orderLoad(1)).thenReturn(List.of(order));

      ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("test@example.com", 1);

      verify(sender).send(messageCaptor.capture());
      String text = messageCaptor.getValue().getText();
      assertTrue(text.contains("・具なしカレー (L) 1個"));
      assertFalse(text.contains("└"));
    }

    @Test
    @DisplayName("正常系: 商品リストとトッピングがある場合のメール本文生成")
    void sendMail_WithItemsAndToppings() throws Exception {
      mockResourceLoader();

      Order order = createSampleOrder(1);
      OrderItem item = new OrderItem();
      item.setId(10);
      com.example.domain.Item innerItem = new com.example.domain.Item();
      innerItem.setName("ピザ");
      item.setItem(innerItem);
      item.setSize("M");
      item.setQuantity(2);

      OrderTopping ot = new OrderTopping();
      Topping t = new Topping();
      t.setName("チーズ");
      ot.setTopping(t);
      item.setOrderTopping(List.of(ot));

      order.setOrderItemList(List.of(item));
      when(orderRepository.orderLoad(1)).thenReturn(List.of(order));

      ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("test@example.com", 1);
      verify(sender).send(captor.capture());

      SimpleMailMessage message = captor.getValue();
      assertNotNull(message);
      String text = message.getText();
      assertNotNull(text);
      assertTrue(text.contains("・ピザ (M) 2個"));
      assertTrue(text.contains("└ チーズ"));
    }

    @Test
    @DisplayName("正常系: 支払い方法が2(クレジットカード)の場合のテキスト置換")
    void sendMail_PaymentMethod2() throws Exception {
      mockResourceLoader();
      Order order = createSampleOrder(1);
      order.setPaymentMethod(2);
      lenient().when(orderRepository.orderLoad(1)).thenReturn(List.of(order));

      ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
      orderService.sendMail("test@example.com", 1);
      verify(sender).send(captor.capture());
      assertTrue(captor.getValue().getText().contains("クレジットカード払い"));
    }

    @Test
    @DisplayName("異常系: テンプレート読み込み失敗時にRuntimeExceptionをスローする")
    void sendMail_IOException() throws Exception {
      Resource mockResource = mock(Resource.class);
      lenient().when(mockResource.getInputStream()).thenThrow(new java.io.IOException("File not found"));
      lenient().when(resourceLoader.getResource(anyString())).thenReturn(mockResource);

      assertThrows(RuntimeException.class, () -> {
        orderService.sendMail("test@example.com", 1);
      });
    }
  }

  @Nested
  @DisplayName("追加のテスト")
  class AdditionalTests {

    @Test
    @DisplayName("orderLoad: Repositoryがnullを返す場合のハンドリング")
    void orderLoad_returnNull() {
      when(orderRepository.orderLoad(anyInt())).thenReturn(null);
      List<Order> result = orderService.orderLoad(1);
      assertNull(result);
    }

    @Test
    @DisplayName("findByOrder: Repositoryがnullを返す場合のハンドリング")
    void findByOrder_returnNull() {
      when(orderRepository.findByOrdertable(anyInt())).thenReturn(null);
      List<Order> result = orderService.findByOrder(1);
      assertNull(result);
    }

    @Test
    @DisplayName("order: カートがnullの場合に正常に終了すること（NPE回避の検証）")
    void order_NullCart() {
      Order order = createSampleOrder(null);
      when(orderRepository.insert(order)).thenReturn(100);
      when(cartService.getOrCreateCart(anyInt())).thenReturn(null);

      Integer orderId = orderService.order(order, 1);

      assertEquals(100, orderId);
      verify(cartService, never()).findItemsByCartId(anyInt());
      verify(cartService, never()).clearCart(anyInt());
    }

    @Test
    @DisplayName("order: OrderItemのフィールドが正しくコピーされているか")
    void order_verifyOrderItemFieldsMapping() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      CartItem cartItem = new CartItem();
      cartItem.setItemId(10);
      cartItem.setQuantity(5);
      cartItem.setSize("L");
      cartItem.setToppingList(new ArrayList<>());

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(1)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(List.of(cartItem));

      when(orderRepository.insert(any())).thenReturn(1);
      ArgumentCaptor<OrderItem> orderItemCaptor = ArgumentCaptor.forClass(OrderItem.class);
      when(orderItemRepository.order(orderItemCaptor.capture())).thenReturn(1);

      orderService.order(order, 1);

      OrderItem captured = orderItemCaptor.getValue();
      assertEquals(10, captured.getItemId());
      assertEquals(5, captured.getQuantity());
      assertEquals("L", captured.getSize());
      verify(cartService).clearCart(1);
    }

    @Test
    @DisplayName("order: OrderToppingのフィールドが正しくセットされているか")
    void order_verifyOrderToppingFields() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      Topping topping = new Topping();
      topping.setId(50);
      CartItem cartItem = new CartItem();
      cartItem.setToppingList(List.of(topping));

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(1)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(List.of(cartItem));

      when(orderRepository.insert(any())).thenReturn(1);
      when(orderItemRepository.order(any())).thenReturn(100);

      ArgumentCaptor<OrderTopping> toppingCaptor = ArgumentCaptor.forClass(OrderTopping.class);
      orderService.order(order, 1);

      verify(orderToppingRepository).insert(toppingCaptor.capture());
      assertEquals(100, toppingCaptor.getValue().getOrderItemId());
      assertEquals(50, toppingCaptor.getValue().getToppingId());
      verify(cartService).clearCart(1);
    }

    @Test
    @DisplayName("order: 複数の商品とトッピングが混在する場合の整合性")
    void order_complexMixedItems() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);

      Topping topping1 = new Topping();
      topping1.setId(1);
      Topping topping2 = new Topping();
      topping2.setId(2);
      Topping topping3 = new Topping();
      topping3.setId(3);

      CartItem c1 = new CartItem();
      c1.setQuantity(1);
      c1.setToppingList(List.of(topping1, topping2));
      CartItem c2 = new CartItem();
      c2.setQuantity(2);
      c2.setToppingList(List.of(topping3));

      when(orderRepository.insert(any())).thenReturn(1);
      when(orderItemRepository.order(any())).thenReturn(10, 20);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(1)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(List.of(c1, c2));

      orderService.order(order, 1);

      verify(orderItemRepository, times(2)).order(any());
      verify(orderToppingRepository, times(3)).insert(any());
      // 在庫減算の検証: 数量分減らされているか
      verify(toppingRepository).decrementStock(1, 1);
      verify(toppingRepository).decrementStock(2, 1);
      verify(toppingRepository).decrementStock(3, 2);
    }

    @Test
    @DisplayName("order: 在庫減算が数量(quantity)分正しく行われること")
    void order_verifyStockDecrementWithQuantity() {
      Order order = createSampleOrder(null);
      CartItem item = new CartItem();
      item.setQuantity(3);
      Topping topping = new Topping();
      topping.setId(100);
      item.setToppingList(List.of(topping));

      when(orderRepository.insert(any())).thenReturn(1);
      when(orderItemRepository.order(any())).thenReturn(1);
      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(1)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(List.of(item));

      orderService.order(order, 1);

      verify(toppingRepository).decrementStock(100, 3);
    }

    @Test
    @DisplayName("order: 支払い方法1の場合のステータス値1の検証")
    void order_verifyStatus1ForPaymentMethod1() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(1);
      when(orderRepository.insert(order)).thenReturn(1);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(1)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(new ArrayList<>());

      orderService.order(order, 1);

      assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("order: 支払い方法2の場合のステータス値2の検証")
    void order_verifyStatus2ForPaymentMethod2() {
      Order order = createSampleOrder(null);
      order.setPaymentMethod(2);
      when(orderRepository.insert(order)).thenReturn(1);

      Cart cart = new Cart();
      cart.setId(1);
      when(cartService.getOrCreateCart(1)).thenReturn(cart);
      when(cartService.findItemsByCartId(1)).thenReturn(new ArrayList<>());

      orderService.order(order, 1);

      assertEquals(OrderStatus.CASH_ON_DELIVERY, order.getStatus());
    }

    @Test
    @DisplayName("sendMail: 送信先が複数回呼ばれても正しく動作する")
    void sendMail_multipleCalls() throws Exception {
      mockResourceLoader();
      lenient().when(orderRepository.orderLoad(anyInt())).thenReturn(List.of(createSampleOrder(1)));
      orderService.sendMail("e1@example.com", 1);
      orderService.sendMail("e2@example.com", 1);

      verify(sender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("paymentMethodJudge: 境界値 - 0を指定した場合")
    void paymentMethodJudge_zero() {
      Order order = new Order();
      order.setPaymentMethod(0);

      assertEquals(OrderStatus.CASH_ON_DELIVERY, orderService.paymentMethodJudge(order));
    }

    @Test
    @DisplayName("paymentMethodJudge: 境界値 - 負の値を指定した場合")
    void paymentMethodJudge_negative() {
      Order order = new Order();
      order.setPaymentMethod(-1);

      assertEquals(OrderStatus.CASH_ON_DELIVERY, orderService.paymentMethodJudge(order));
    }
  }

  private Order createSampleOrder(Integer id) {
    // Order order = new Order();
    // if (id != null) {
    // order.setId(id);

    // }

    // order.setUserId(10);
    // order.setStatus(OrderStatus.IN_CART);
    // order.setTotalPrice(1500);
    // order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
    // order.setDestinationName("テスト太郎");
    // order.setDestinationEmail("test@example.com");
    // order.setDestinationZipcode("123-4567");
    // order.setDestinationAddress("東京都渋谷区1-2-3");
    // order.setDestinationTel("090-0000-0000");
    // order.setDeliveryTime(new Timestamp(System.currentTimeMillis()));
    // order.setPaymentMethod(1);
    // return order;

    Order order = Order.builder()
        .userId(10)
        .status(OrderStatus.IN_CART)
        .destinationName("テスト太郎")
        .destinationEmail("test@example.com")
        .destinationZipcode("123-4567")
        .destinationAddress("東京都渋谷区1-2-3")
        .destinationTel("090-0000-0000")
        .paymentMethod(1)
        .build();
    if (id != null) {
      order.setId(id);
    }
    order.setTotalPrice(1500);
    order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
    order.setDeliveryTime(new Timestamp(System.currentTimeMillis()));
    return order;
  }
}
