package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.Item;
import com.example.domain.Topping;
import com.example.domain.UserDetailData;
import com.example.service.ItemService;

import jakarta.servlet.ServletContext;

@Controller
@RequestMapping("")
public class ItemController {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ServletContext application;

	@Autowired
	private ItemService itemService;

	// 1ページに表示する従業員数は5名
	private static final int VIEW_SIZE = 5;

	/**
	 * 従業員一覧画面を出力
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/showList")
	public String showItem(Model model, Integer page, String searchName) {
		List<Item> itemList = null;

		// ページング機能追加
		if (page == null) {
			// ページ数の指定が無い場合は1ページ目を表示させる
			page = 1;
		}
		log.info("商品一覧表示: page={}, searchName={}", page, searchName);

		if (searchName == null) {
			// 検索文字列が空なら全件検索
			itemList = itemService.findAll();
		} else {
			// 検索文字列があれば曖昧検索
			itemList = itemService.findByName(searchName);
			// ページングの数字からも検索できるように検索文字列をスコープに格納しておく
			model.addAttribute("searchName", searchName);
			// 該当する検索結果がなければ全件返す
			if (itemList == null) {
				log.warn("商品検索結果なし: searchName={}", searchName);
				itemList = itemService.findAll();
				model.addAttribute("message", "error.itemNothing");
			}
		}

		// 表示させたいページ数、ページサイズ、従業員リストを渡し１ページに表示させる従業員リストを絞り込み
		Page<Item> itemPage = itemService.showListPaging(page, VIEW_SIZE, itemList);
		model.addAttribute("itemPage", itemPage);

		// ページングのリンクに使うページ数をスコープに格納 (例)28件あり1ページにつき10件表示させる場合→1,2,3がpageNumbersに入る
		List<Integer> pageNumbers = calcPageNumbers(model, itemPage);
		model.addAttribute("pageNumbers", pageNumbers);
		return "item/item_list_curry";
	}

	/**
	 * ページングのリンクに使うページ数をスコープに格納 (例)28件あり1ページにつき10件表示させる場合→1,2,3がpageNumbersに入る
	 * 
	 * @param model        モデル
	 * @param employeePage ページング情報
	 */
	private List<Integer> calcPageNumbers(Model model, Page<Item> itemPage) {
		int totalPages = itemPage.getTotalPages();
		log.debug("totalPages={}", totalPages);

		List<Integer> pageNumbers = null;
		if (totalPages > 0) {
			pageNumbers = new ArrayList<Integer>();
			for (int i = 1; i <= totalPages; i++) {
				pageNumbers.add(i);
			}
		}
		log.debug("itemPage={}", itemPage);
		model.addAttribute("itemPage", itemPage);
		return pageNumbers;
	}

	/**
	 * 商品詳細画面を表示
	 * 
	 * @param id    商品ID
	 * @param model リクエストスコープ
	 * @return item/detail.html
	 */
	@RequestMapping("/detail")
	public String showItemDetail(String id, Model model) {

		int itemId = Integer.parseInt(id);
		log.info("商品詳細表示: itemId={}", itemId);
		
		// 商品詳細を表示させる
		Item item = itemService.showItemDetail(Integer.parseInt(id));
		model.addAttribute("item", item);

		// トッピング一覧を表示
		List<Topping> toppingList = itemService.findAllTopping();
		model.addAttribute("toppingList", toppingList);

		// application.setAttribute("toppingList", toppingList);
		return "item/item_detail";
	}
}
