package com.example.controller;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.Order;
import com.example.domain.User;
import com.example.form.OrderForm;
import com.example.service.CartService;
import com.example.service.OrderService;

import jakarta.servlet.http.HttpSession;

/**
 * 注文確認画面に遷移するためのコントローラー
 * 
 * @author MatsunagaDai,MiyazawaNami
 *
 */
@Controller
@RequestMapping("")
public class OrderControler {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(OrderControler.class);

	@ModelAttribute
	public OrderForm setOrderForm() {
		OrderForm form = new OrderForm();
		// 初期値を10時に設定
		form.setDeliveryTime("10");
		return form;
	}

	@Autowired
	private OrderService service;

	@Autowired
	private CartService cartService;

	@Autowired
	private HttpSession session;

	public OrderForm setUpOrderForm() {
		return new OrderForm();
	}

	@RequestMapping("/toOrder")
	public String toOrder() {

		// セッションにログイン情報があれば確認画面に遷移
		// ログイン情報がなければログイン画面に遷移
		if (session.getAttribute("user") == null) {
			log.warn("注文確認画面遷移不可: 未ログイン");
			return "redirect:/toLogin";
		} else {
			return "order/order_confirm";
		}
	}

	@RequestMapping("/orderCo")
	public String orderCo() {
		return "/order/order_confirm";
	}

	/**
	 * 注文完了画面に遷移
	 * 
	 * @param form
	 * @return 完了画面
	 */
	@RequestMapping("/order")
	public String orderCompletion(@Validated OrderForm form, BindingResult result, Model model) {
		User user = (User) session.getAttribute("user");
		// 昨日の日付を取得し配達日と比較
		Date now = new Date();
		Calendar calYesterday = Calendar.getInstance();
		calYesterday.setTime(now);
		calYesterday.add(Calendar.DAY_OF_MONTH, -1);
		Date yesterday = calYesterday.getTime();

		if (result.hasErrors()) {
			// illigalArgumentExsepsionで捕まるのでif文の中に記載
			if (form.getOrderDate() == null) {
				model.addAttribute("errorDeliveryDate", "配達日を入力してください");
				return "order/order_confirm";
			}
			if (yesterday.after(form.getOrderDate())) {
				model.addAttribute("errorDeliveryDate", "配達日が過去の日付になっています");
				return "order/order_confirm";
			}

			return "order/order_confirm";
		}

		// --- 以下の古い配達時間チェックは、142行目以降の新しいロジックに置き換えられたため削除します ---

		// --- 【修正箇所】配達日時の組み立てロジックの刷新 ---
		/**
		 * 1970年問題を解決するため、Calendarクラスを使用して
		 * 「ユーザーが選択した日付」と「ユーザーが選択した時間」を正確に合成します。
		 */
		Calendar deliveryCal = Calendar.getInstance();
		// ユーザーがフォームで入力した日付をセット (例: 2026/03/16)
		deliveryCal.setTime(form.getOrderDate());
		// ユーザーが選択した時間をセット (例: 10時)
		deliveryCal.set(Calendar.HOUR_OF_DAY, form.getIntegerDeliveryTime());
		deliveryCal.set(Calendar.MINUTE, 0);
		deliveryCal.set(Calendar.SECOND, 0);
		deliveryCal.set(Calendar.MILLISECOND, 0);

		// 合成した日時をDate型およびTimestamp型として取得
		Date deliveryDate = deliveryCal.getTime();
		Timestamp deliveryTimestamp = new Timestamp(deliveryCal.getTimeInMillis());

		// 現在時刻から3時間後の時刻を計算してチェック
		Calendar calThreeHoursLater = Calendar.getInstance();
		calThreeHoursLater.setTime(now);
		calThreeHoursLater.add(Calendar.HOUR_OF_DAY, 3);

		if (calThreeHoursLater.getTime().after(deliveryDate)) {
			model.addAttribute("errorDeliveryDate", "今から3時間後の日時をご入力ください");
			return "/order/order_confirm";
		}

		// Order order = new Order();
		// BeanUtils.copyProperties(form, order);
		// order.setDeliveryTime(deliveryTimestamp);

		// // 郵便番号のハイフンを消してドメインにセット
		// order.setDestinationZipcode(form.getDestinationZipcode().replace("-", ""));

		// リファクタリング課題#4 Builderパターンで安全なオブジェクト生成に変更
		Order order = Order.builder()
				.destinationName(form.getDestinationName())
				.destinationEmail(form.getDestinationEmail())
				.destinationZipcode(form.getDestinationZipcode().replace("-", ""))
				.destinationAddress(form.getDestinationAddress())
				.destinationTel(form.getDestinationTel())
				.deliveryTime(deliveryTimestamp)
				.paymentMethod(form.getPaymentMethod())
				.totalPrice((Integer) session.getAttribute("totalPrice"))
				.status(service.paymentMethodJudge(form.getPaymentMethod()))
				.userId(user.getId())
				.build();

		Integer orderId = null;
		try {
			orderId = service.order(order, user.getId(), user.getEmail());
		} catch (com.example.exception.StockShortageException e) {
			// 在庫不足例外をキャッチ
			log.warn("注文エラー: 在庫不足エラー発生 - {}", e.getMessage());

			// // DBとセッションの両方から在庫不足の商品を特定して削除
			// @SuppressWarnings("unchecked")
			// List<CartItem> cartList = (List<CartItem>)
			// session.getAttribute("cartItemList");

			// if (cartList != null) {
			// String errorMsg = e.getMessage();
			// // エラーメッセージに含まれるトッピング名を持つ商品を探して削除
			// java.util.Iterator<CartItem> it = cartList.iterator();
			// while (it.hasNext()) {
			// CartItem item = it.next();
			// if (item.getToppingList() != null && item.getToppingList().stream()
			// .anyMatch(t -> errorMsg.contains(t.getName()))) {
			// // DBから削除
			// cartService.deleteItem(item.getId());
			// // セッションリストから削除
			// it.remove();
			// }
			// }
			// }

			// model.addAttribute("stockError", e.getMessage());
			// return "/order/order_confirm";

			Cart cart = cartService.getOrCreateCart(user.getId());
			List<CartItem> cartList = cartService.findItemsByCartId(cart.getId());

			if (!cartList.isEmpty()) {
				String errorMsg = e.getMessage();
				java.util.Iterator<CartItem> it = cartList.iterator();
				while (it.hasNext()) {
					CartItem item = it.next();
					if (item.getToppingList() != null && item.getToppingList().stream()
							.anyMatch(t -> errorMsg.contains(t.getName()))) {
						cartService.deleteItem(item.getId());
						it.remove();
					}
				}
			}

			model.addAttribute("stockError", e.getMessage());
			return "/order/order_confirm";

		}

		// リファクタリング課題#7 Observerパターン：メール送信はorder()内でのイベント発行経由で
		// OrderMailListenerが行うようになったため、直接のsendMail()呼び出しは不要になった
		// 注文完了後にメール送信（別トランザクション、失敗しても注文自体は成立）
		// try {
		// service.sendMail(user.getEmail(), orderId);
		// } catch (Exception e) {
		// log.error("メール送信時にエラーが発生しました: orderId={}", orderId, e);
		// // 注文自体は完了しているため、エラー画面にはせず続行
		// }

		return "redirect:/orderCompletion";

	}

	@RequestMapping("orderCompletion")
	public String orderCompletion() {
		return "/order/order_finished";
	}

	/**
	 * 注文履歴のページを表示
	 * 
	 * @param model リクエストスコープ
	 * @return 注文履歴
	 */
	@RequestMapping("/orderHistory")
	public String orderHistory(Model model) {

		// ユーザーの情報を拾ってくる
		User user = (User) session.getAttribute("user");

		// もしログインしていなければログインに戻す
		if (user == null) {
			log.warn("注文履歴表示不可: 未ログイン");
			return "forward:/toLogin";
		}

		// リファクタリング課題#2（対応漏れ修正）
		// findByOrder()がnullを返さなくなったため、isEmpty()でチェックする
		List<Order> orderList = service.findByOrder(user.getId());
		// if (orderList == null) {
		if (orderList.isEmpty()) {
			model.addAttribute("orderNothing", "error.orderNothing");
		} else {
			model.addAttribute("orderList", orderList);
		}

		log.info("注文履歴表示: userId={}, orderCount={}",
				// user.getId(), orderList == null ? 0 : orderList.size());
				user.getId(), orderList.size());

		return "order/order_history";
	}

	@RequestMapping("orderdetail")
	public String orderDetail(Integer id, Model model) {
		log.info("注文詳細表示: orderId={}", id);
		List<Order> orderList = service.orderLoad(id);
		model.addAttribute("orderList", orderList);
		return "/order/order_detail";
	}
}
