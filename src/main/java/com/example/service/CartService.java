package com.example.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Cart;
import com.example.domain.CartItem;
import com.example.domain.CartTopping;
import com.example.domain.Topping;
import com.example.form.ItemCartInForm;
import com.example.repository.CartItemRepository;
import com.example.repository.CartRepository;
import com.example.repository.CartToppingRepository;

/**
 * カート内に商品を入れる際に使うservice
 * 
 * @author naramasato
 *
 */
@Service
@Transactional
public class CartService {

	// @Autowired
	// private CartRepository cartRepository;

	// @Autowired
	// private CartItemRepository cartItemRepository;

	// @Autowired
	// private CartToppingRepository cartToppingRepository;

	private final CartRepository cartRepository;

	private final CartItemRepository cartItemRepository;

	private final CartToppingRepository cartToppingRepository;

	public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
			CartToppingRepository cartToppingRepository) {
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.cartToppingRepository = cartToppingRepository;
	}

	public ItemCartInForm setupForm() {
		return new ItemCartInForm();
	}

	/**
	 * サイズに合わせてitemの金額を判定
	 * 
	 * @param form
	 * @return itemの金額
	 */
	public Integer getPriceSize(ItemCartInForm form) {
		if (form.getSize().equals("M")) {
			return form.getPriceM();
		} else {
			return form.getPriceL();
		}
	}

	/**
	 * 
	 * @param toppingList  applicationスコープ内に格納してあるトッピング一覧
	 * @param toppingIndex formから送られてきたトッピングの情報
	 * @return トッピング一覧を格納したList 一覧がなければLinkedListで返す
	 * 
	 */
	public List<Topping> getToppingIndex(List<Topping> toppingList, List<String> toppingIndex) {
		// if (toppingIndex == null || toppingList == null) {
		// return new LinkedList<>();
		// }
		List<Topping> toppings = new LinkedList<>();
		// for (String index : toppingIndex) {
		// Topping topping = toppingList.get(Integer.parseInt(index));
		// toppings.add(topping);
		// }

		// リファクタリング課題#40 O(n²)のネストループをMapを使ったO(n)に改善
		Map<Integer, Topping> toppingMap = toppingList.stream()
				.collect(Collectors.toMap(Topping::getId, t -> t));

		for (String index : toppingIndex) {
			Topping topping = toppingMap.get(Integer.parseInt(index));
			if (topping != null) {
				toppings.add(topping);
			}
		}
		return toppings;
	}

	// 合計金額を計算するメソッド
	public Integer calcTotal(List<CartItem> totalPriceList) {

		// Integer totalPrices = 0;
		// for (CartItem price : totalPriceList) {
		// 	totalPrices += price.getSubTotal();
		// }
		// return totalPrices;

		// リファクタリング課題#11 forループをStreamに変換
		return totalPriceList.stream()
            .mapToInt(CartItem::getSubTotal)
            .sum();
	}

	/**
	 * ユーザーIDに基づいてカートを取得、存在しなければ作成する。
	 * 
	 * @param userId ユーザーID
	 * @return カート情報
	 */
	public Cart getOrCreateCart(Integer userId) {
		return getOrCreateCart(userId, null);
	}

	/**
	 * ユーザーIDまたはセッションIDに基づいてカートを取得、存在しなければ作成する。
	 */
	public Cart getOrCreateCart(Integer userId, String sessionId) {
		Cart cart = null;
		if (userId != null) {
			cart = cartRepository.findByUserId(userId);
		} else {
			cart = cartRepository.findBySessionId(sessionId);
		}

		if (cart == null) {
			cart = new Cart();
			cart.setUserId(userId);
			cart.setSessionId(sessionId);
			cart = cartRepository.save(cart);
		}
		return cart;
	}

	/**
	 * カートに商品を追加する。
	 */
	public void addItemToCart(Cart cart, CartItem cartItem) {
		cartItem.setCartId(cart.getId());
		cartItemRepository.save(cartItem);

		if (cartItem.getToppingList() != null) {
			for (Topping topping : cartItem.getToppingList()) {
				CartTopping cartTopping = new CartTopping();
				cartTopping.setCartItemId(cartItem.getId());
				cartTopping.setToppingId(topping.getId());
				cartToppingRepository.save(cartTopping);
			}
		}
	}

	/**
	 * カートIDに紐づくすべてのアイテム（トッピング含む）を取得する。
	 */
	public List<CartItem> findItemsByCartId(Integer cartId) {
		// List<CartItem> items = cartItemRepository.findByCartId(cartId);
		// for (CartItem item : items) {
		// List<CartTopping> cartToppings =
		// cartToppingRepository.findByCartItemId(item.getId());
		// List<Topping> toppings = new ArrayList<>();
		// for (CartTopping ct : cartToppings) {
		// toppings.add(ct.getTopping());
		// }
		// item.setToppingList(toppings);
		// }

		// リファクタリング課題#16 N+1問題解消：全トッピングを1回で取得しJava側で振り分ける
		List<CartItem> items = cartItemRepository.findByCartId(cartId);
		List<CartTopping> allToppings = cartToppingRepository.findByCartId(cartId);
		for (CartItem item : items) {
			// Java側でアイテムIDに一致するトッピングを振り分ける（SQLなし）
			List<Topping> toppings = allToppings.stream()
					.filter(ct -> ct.getCartItemId().equals(item.getId()))
					.map(CartTopping::getTopping)
					.collect(Collectors.toList());
			item.setToppingList(toppings);
		}
		return items;
	}

	/**
	 * 個別のカートアイテムを削除する。
	 */
	public void deleteItem(Integer cartItemId) {
		// カスケード削除が設定されている前提（または手動でトッピングを消す）
		// SQLで ON DELETE CASCADE を設定したので、トッピングは自動で消える
		cartItemRepository.deleteById(cartItemId);
	}

	/**
	 * ゲスト時のカート情報をログイン済みユーザーのカートに統合する。
	 */
	public void mergeCart(String previousSessionId, Integer userId) {
		if (previousSessionId == null || userId == null) {
			return;
		}

		// ゲスト時のカートを取得
		Cart guestCart = cartRepository.findBySessionId(previousSessionId);
		if (guestCart == null) {
			return;
		}

		// ユーザーの正規カートを取得（なければ作成）
		Cart userCart = cartRepository.findByUserId(userId);
		if (userCart == null) {
			// ユーザー用のカートレコードがない場合、ゲスト時のカートレコードをユーザーIDに紐付け直す
			guestCart.setUserId(userId);
			cartRepository.save(guestCart);
		} else {
			// すでにユーザー用のカートがある場合、ゲストカートのアイテムをユーザーカートに移す
			List<CartItem> guestItems = cartItemRepository.findByCartId(guestCart.getId());
			for (CartItem item : guestItems) {
				item.setCartId(userCart.getId());
				cartItemRepository.save(item);
			}
			// ゲスト時のカートレコード自体は不要になるため削除
			cartRepository.deleteById(guestCart.getId());
		}
	}

	/**
	 * カートIDに紐づくすべてのアイテム（項目、トッピング、カート本体）を削除する。
	 */
	public void clearCart(Integer cartId) {
		// 1. カート内の全アイテムを取得
		List<CartItem> items = cartItemRepository.findByCartId(cartId);

		for (CartItem item : items) {
			// 2. 各アイテムに紐づくトッピングを明示的に削除
			cartToppingRepository.deleteByCartItemId(item.getId());
			// 3. アイテム自体を削除
			cartItemRepository.deleteById(item.getId());
		}

		// 4. 親のカートレコードを削除
		cartRepository.deleteById(cartId);
	}
}
