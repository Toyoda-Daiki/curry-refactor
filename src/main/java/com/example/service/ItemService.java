package com.example.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.domain.Item;
import com.example.domain.Topping;
import com.example.repository.ItemRepository;
import com.example.repository.ToppingRepository;

@Service
@Transactional
public class ItemService {

	/** ログ出力用Logger */
	private static final Logger log = LoggerFactory.getLogger(ItemService.class);

	@Autowired
	private ItemRepository repository;

	@Autowired
	private ToppingRepository toppingRepository;

	public List<Item> findAll() {
		return repository.findAll();
	}

	public List<Item> findByName(String name) {
		if (name == null || "".equals(name)) {
			log.debug("商品名未入力のため全件取得");
			return repository.findAll();
		} else {
			return repository.findByName(name);
		}
	}

	/**
	 * 商品IDをItemRepository内のshowItemDetailに渡す
	 * 
	 * @param id
	 * @return 商品情報を１件（存在しない場合はOptional.empty()）
	 */
	// リファクタリング課題#2（対応漏れ修正）Optional<Item>をそのまま呼び出し元に伝播する
	// public Item showItemDetail(Integer id) {
	public Optional<Item> showItemDetail(Integer id) {
		return repository.showItemDetail(id);
	}

	/**
	 * 登録されてるトッピングを全件表示
	 * 
	 * @return トッピングの全件リスト
	 */
	@Cacheable("toppings")
	public List<Topping> findAllTopping() {
		return toppingRepository.findAllTopping();
	}

	/**
	 * 登録されている商品の名前全件
	 * 
	 * @return 商品の名前全件
	 */
	public List<String> getAllNames() {
		return repository.getAllNames();
	}

	/**
	 * 商品をページごとに表示
	 * 
	 */
	public Page<Item> showListPaging(int page, int size, List<Item> itemList) {
		// 表示させたいページ数を-1しなければうまく動かない
		page--;
		// どの従業員から表示させるかと言うカウント値
		int startItemCount = page * size;
		// 絞り込んだ後の従業員リストが入る変数
		List<Item> list;

		if (itemList.size() < startItemCount) {
			// (ありえないが)もし表示させたい従業員カウントがサイズよりも大きい場合は空のリストを返す
			list = Collections.emptyList();
		} else {
			// 該当ページに表示させる従業員一覧を作成
			int toIndex = Math.min(startItemCount + size, itemList.size());
			list = itemList.subList(startItemCount, toIndex);
		}

		// 丸ごと出力は情報量が多いため、コメントアウト
		// log.debug("itemList = {}", itemList);
		// 上記で作成した該当ページに表示させる従業員一覧をページングできる形に変換して返す
		Page<Item> employeePage = new PageImpl<Item>(list, PageRequest.of(page, size), itemList.size());
		return employeePage;
	}
}
