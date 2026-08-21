package com.example.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.Order;
import com.example.domain.OrderItem;
import com.example.domain.OrderTopping;
import com.example.domain.PaymentMethod;
import com.example.domain.Topping;
import com.example.event.OrderCompletedEvent;
import com.example.repository.OrderItemRepository;
import com.example.repository.OrderRepository;
import com.example.repository.OrderToppingRepository;
import com.example.repository.ToppingRepository;
import com.example.service.payment.PaymentProcessor;
import com.example.domain.OrderStatus;

/**
 * orderに関わる内容を行う
 * 
 * @author naramasato
 *
 */
@Service
@Transactional
public class OrderService {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	// @Autowired
	// private OrderRepository orderRepository;

	// @Autowired
	// private OrderItemRepository orderItemRepository;

	// @Autowired
	// private OrderToppingRepository orderToppingRepository;

	// @Autowired
	// private ToppingRepository toppingRepository;

	// @Autowired
	// private MailSender sender;

	// @Autowired
	// private CartService cartService;

	// @Autowired
	// private ResourceLoader resourceLoader;

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderToppingRepository orderToppingRepository;
	private final ToppingRepository toppingRepository;
	private final MailSender sender;
	private final CartService cartService;
	private final ResourceLoader resourceLoader;
	private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;
	private final ApplicationEventPublisher eventPublisher;

	public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			OrderToppingRepository orderToppingRepository, ToppingRepository toppingRepository, MailSender sender,
			CartService cartService, ResourceLoader resourceLoader, List<PaymentProcessor> paymentProcessors,
			ApplicationEventPublisher eventPublisher) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.orderToppingRepository = orderToppingRepository;
		this.toppingRepository = toppingRepository;
		this.sender = sender;
		this.cartService = cartService;
		this.resourceLoader = resourceLoader;
		this.paymentProcessorMap = paymentProcessors.stream()
				.collect(Collectors.toMap(PaymentProcessor::getSupportedMethod, p -> p));
		this.eventPublisher = eventPublisher;
	}

	@Value("${spring.mail.from:noreply@example.com}")
	private String mailFrom;

	@Value("${mail.subject:ご注文ありがとうございます}")
	private String mailSubject;

	/**
	 * 注文詳細一件を取得
	 * 
	 * @param orderId
	 * @return
	 */
	public List<Order> orderLoad(Integer orderId) {
		return orderRepository.orderLoad(orderId);
	}

	/**
	 * 注文詳細全件を取得
	 * 
	 * @param id
	 * @return
	 */
	public List<Order> findByOrder(Integer id) {
		return orderRepository.findByOrdertable(id);
	}

	/**
	 * orderドメインに足りない物をセット
	 * 
	 * @param order
	 */
	// リファクタリング課題#7 Observerパターン：注文完了時の後続処理をイベント発行に変更
	// emailはOrderCompletedEvent発行のためOrderControlerから受け取る
	public Integer order(Order order, Integer userId, String email) {
		// order.setStatus(paymentMethodJudge(order));
		// order.setUserId(userId);
		Integer orderId = orderRepository.insert(order);
		Cart cart = cartService.getOrCreateCart(userId);
		if (cart != null) {
			List<CartItem> cartItemList = cartService.findItemsByCartId(cart.getId());
			insertOrderItem(orderId, cartItemList);
			cartService.clearCart(cart.getId());
			log.info("DB上のカートをクリアしました: cartId={}, userId={}", cart.getId(), userId);
		} else {
			log.warn("DB上にカートが見つかりませんでした: userId={}", userId);
		}

		log.info("注文処理完了: orderId={}, userId={}", orderId, order.getUserId());

		// リファクタリング課題#7 注文完了イベントを発行（メール送信等の後続処理はリスナーに委譲）
		eventPublisher.publishEvent(new OrderCompletedEvent(this, email, orderId));
		return orderId;
	}

	/**
	 * statusを判別するメゾット
	 * 
	 * @param order
	 * @return statusを整数で返す
	 */

	// public OrderStatus paymentMethodJudge(Integer paymentMethod) {
	// // if (order.getPaymentMethod() == 1) {
	// if (Integer.valueOf(1).equals(paymentMethod)) {
	// return OrderStatus.PAID;
	// } else {
	// return OrderStatus.CASH_ON_DELIVERY;
	// }
	// }

	public OrderStatus paymentMethodJudge(Integer paymentMethod) {
		PaymentMethod method = PaymentMethod.fromValue(paymentMethod);
		PaymentProcessor processor = paymentProcessorMap.get(method);
		OrderStatus status = processor.judge();
		return status;
	}

	/**
	 * order_itemsテーブルにINSERTするメゾット
	 * 
	 * @param orderId
	 */

	private void insertOrderItem(Integer orderId, List<CartItem> cartItemList) {
		for (CartItem cartItem : cartItemList) {
			// OrderItem orderItem = new OrderItem();
			// BeanUtils.copyProperties(cartItem, orderItem);

			// orderItem.setOrderId(orderId);

			// リファクタリング課題#17 オブジェクト生成の責務（Factory Method）
			OrderItem orderItem = OrderItem.from(cartItem, orderId);

			Integer orderItemid = orderItemRepository.order(orderItem);

			log.debug("注文商品登録: orderId={}, itemId={}, quantity={}",
					orderId, cartItem.getItemId(), cartItem.getQuantity());

			InsertOrdertopping(orderItemid, cartItem.getToppingList(), cartItem.getQuantity());
		}
	}

	/**
	 * order_toppingsテーブルにセット
	 * 
	 * @param orderItemId 注文商品の主キー
	 * @param toppingList 注文商品が持っているtoppingList
	 */
	private void InsertOrdertopping(Integer orderItemId, List<Topping> toppingList, Integer quantity) {

		log.debug("注文トッピング登録開始: orderItemId={}, toppingCount={}, quantity={}",
				orderItemId, toppingList.size(), quantity);

		for (Topping topping : toppingList) {
			// 1. 注文トッピングテーブルへの登録
			OrderTopping orderTopping = new OrderTopping();
			orderTopping.setOrderItemId(orderItemId);
			orderTopping.setToppingId(topping.getId());
			orderToppingRepository.insert(orderTopping);

			// 【修正箇所②】Repository側のdecrementStock(Integer, Integer)に合わせ、
			// 引数に「トッピングID」と「減らす個数(quantity)」の2つを渡すように修正
			// これにより、商品が9個あれば在庫も一気に9個減ります
			toppingRepository.decrementStock(topping.getId(), quantity);

			log.debug("在庫減算実行: toppingId={}, quantity={}", topping.getId(), quantity);
		}
	}

	/**
	 * 引数で受け取ったemailに完了メールを送付
	 * 
	 * @param email
	 */
	public void sendMail(String email, Integer orderId) {
		try {

			List<Order> orderList = orderRepository.orderLoad(orderId);

			// 1. テンプレートファイル（.txt）を読み込む（try-with-resourcesでInputStreamを確実にclose）
			org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:mail-template.txt");
			String template;

			// リファクタリング課題#27 try-with-resourcesでInputStreamを確実にclose
			try (InputStream is = resource.getInputStream()) {
				template = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			}

			// 2. 商品リストのテキストを作成（トッピング情報も含む）
			String itemsText = "";
			if (orderList.get(0).getOrderItemList() != null) {
				itemsText = orderList.get(0).getOrderItemList().stream()
						.map(order -> {
							// 基本ライン： ・商品名 (サイズ) 数量個
							StringBuilder sb = new StringBuilder();
							sb.append("・").append(order.getItem().getName())
									.append(" (").append(order.getSize()).append(") ")
									.append(order.getQuantity()).append("個");

							// トッピングがあればインデントを下げて追加
							if (order.getOrderTopping() != null && !order.getOrderTopping().isEmpty()) {
								for (OrderTopping t : order.getOrderTopping()) {
									sb.append("\n └ ").append(t.getTopping().getName());
								}
							}
							return sb.toString();
						})
						.collect(Collectors.joining("\n"));
			}

			// 3. 支払い方法の名称変換
			// String paymentMethodText = (orderList.get(0).getPaymentMethod() == 1) ?
			// "代金引換" : "クレジットカード払い";

			// リファクタリング課題#19（対応漏れ修正）Integerの==比較によるオートボクシング問題を解消
			// ※表示テキストの内容（1=代金引換／それ以外=クレジットカード払い）自体は変更せず、比較方法のみ修正
			String paymentMethodText = Integer.valueOf(1).equals(orderList.get(0).getPaymentMethod())
					? "代金引換"
					: "クレジットカード払い";

			// 4. 各種日時のフォーマット
			SimpleDateFormat sdfDelivery = new SimpleDateFormat("yyyy年MM月dd日 HH時");
			String formattedDeliveryTime = sdfDelivery.format(orderList.get(0).getDeliveryTime());

			SimpleDateFormat sdfOrder = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分");
			String now = sdfOrder.format(new java.util.Date());

			// 5. テンプレート内の変数を一括置換
			String mailText = template
					.replace("${orderDate}", now)
					.replace("${deliveryDate}", formattedDeliveryTime)
					.replace("${orderItemsText}", itemsText)
					.replace("${zipCode}", orderList.get(0).getDestinationZipcode())
					.replace("${address}", orderList.get(0).getDestinationAddress())
					.replace("${customerName}", orderList.get(0).getDestinationName())
					.replace("${paymentMethod}", paymentMethodText)
					.replace("${totalAmount}", String.format("%,d", orderList.get(0).getTotalPrice()));

			// 6. メールの作成と送信
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setFrom(mailFrom);
			msg.setTo(email);
			msg.setSubject(mailSubject);
			msg.setText(mailText);

			this.sender.send(msg);

		} catch (IOException e) {
			log.error("エラー", e);
			throw new RuntimeException("メールテンプレートの読み込みに失敗しました", e);
		}
	}
}
