package com.example.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.Topping;
import com.example.domain.User;
import com.example.form.ItemCartInForm;
import com.example.service.CartService;
import com.example.service.ItemService;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * @author satakemisako
 *         カートに商品を追加する
 *
 */
@Controller
@RequestMapping("")
public class CartController {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private CartService service;

	@Autowired
	private ItemService itemService;

	@Autowired
	private HttpSession session;

	// @Autowired
	// private ServletContext application;

	public ItemCartInForm setupForm() {
		return new ItemCartInForm();
	}

	// Cartに商品を追加
	@RequestMapping("/inCart")
	public String inCart(ItemCartInForm form) {

		CartItem cartItem = new CartItem();
		BeanUtils.copyProperties(form, cartItem);
		cartItem.setId(null); // form.id(商品ID)がcartItem.id(主キー)にコピーされるのを防ぐ
		cartItem.setItemId(form.getId());

		// cartItemにitemの金額を設置
		cartItem.setItemPrice(service.getPriceSize(form));

		// トッピングをcartItemに代入
		// List<Topping> toppingList = (List<Topping>) application.getAttribute("toppingList");

		// リファクタリング課題#28 型安全コレクション：applicationスコープの生キャストをitemService.findAllTopping()に変更
		List<Topping> toppingList = itemService.findAllTopping();
		List<Topping> toppings = service.getToppingIndex(toppingList, form.getToppingIndex());
		cartItem.setToppingList(toppings);

		// DBへの保存
		User user = (User) session.getAttribute("user");
		Integer userId = (user != null) ? user.getId() : null;

		// ゲストユーザーの場合、セッションを明示的に開始してIDを固定化し、追跡用IDを属性に持つ
		if (userId == null) {
			if (session.isNew()) {
				session.setAttribute("sessionInit", true);
			}
			session.setAttribute("previousSessionId", session.getId());
		}
		String sessionId = session.getId();

		Cart cart = service.getOrCreateCart(userId, sessionId);
		service.addItemToCart(cart, cartItem);

		log.info("カート追加(DB): cartId={}, itemId={}, quantity={}, size={}, toppingCount={}",
				cart.getId(),
				form.getId(),
				form.getQuantity(),
				form.getSize(),
				form.getToppingIndex() == null ? 0 : form.getToppingIndex().size());

		return "redirect:/showCart";
	}

	// Cartの中身を表示するメソッド
	@RequestMapping("/showCart")
	public String showCart(Model model) {
		User user = (User) session.getAttribute("user");
		Integer userId = (user != null) ? user.getId() : null;
		String sessionId = session.getId();

		Cart cart = service.getOrCreateCart(userId, sessionId);
		List<CartItem> cartItemList = service.findItemsByCartId(cart.getId());

		int totalPrice = 0;
		if (cartItemList.size() == 0) {
			model.addAttribute("cartNothing", "error.cartNothing");
		} else {
			totalPrice = service.calcTotal(cartItemList);
		}
		session.setAttribute("cartItemList", cartItemList); // 既存のJSPで使用している可能性があるためセット
		session.setAttribute("totalPrice", totalPrice);
		return "cart/cart_list";
	}

	@RequestMapping("/delete")
	public String delete(String id, Model model) { // 引数をindexからDBのID(id)に変更
		Integer cartItemId = Integer.parseInt(id);
		service.deleteItem(cartItemId);

		log.info("カート商品削除(DB): cartItemId={}", cartItemId);

		return showCart(model);
	}

}
