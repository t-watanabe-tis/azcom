package jp.co.sss.shop.controller.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.BeanCopy;
import jp.co.sss.shop.util.Constant;

/**
 * 商品管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class ItemShowCustomerController {
	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @param pageable ページング情報
	 * @return "/" トップ画面へ
	 */
	@RequestMapping(path = "/")
	public String index(Model model, Pageable pageable) {

		return "/index";
	}

//
//	@RequestMapping("/item/list")
//	public String showNewItemList(Model model) {
//
//		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemRepository.findAll());
//		model.addAttribute("items", itemBeanList);
//		return "item/list/item_list";
//	}
//

	@RequestMapping("/item/list")
	public String showNewItemList(Model model, Pageable pageable) {
		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByInsertDateDesc(Constant.NOT_DELETED, pageable);
		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());
		// 会員情報をViewに渡す
		model.addAttribute("pages", itemList);
		model.addAttribute("items", itemBeanList);
		model.addAttribute("url", "/item/list");
		return "item/list/item_list";
	}



	//商品詳細画面
	@RequestMapping(path = "/item/detail/{id}")
	public String create(@PathVariable int id, Model model) {
		model.addAttribute("item", itemRepository.getOne(id));
		System.out.println("detail");
		System.out.println(itemRepository.getOne(id).getName());
		return "item/detail/item_detail";
	}

	//カテゴリー別検索
	@RequestMapping("/category")
	public String category(Model model) {
		model.addAttribute("categories", categoryRepository.findAll());
		return "common/sidebar";
	}

	@RequestMapping(path = "/item/list/category", method = RequestMethod.GET)
	public String categorySearch(Integer categoryId, Model model) {
		Category category = new Category();

		category.setId(categoryId);
		System.out.println(categoryId);

		model.addAttribute("categories", categoryRepository.findAll());
		List<Item> itemList = itemRepository.findByCategory(categoryRepository.getOne(categoryId));
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList);
		model.addAttribute("items", itemBeanList);
		return "item/list/item_list";
	}

	//商品名検索
	@RequestMapping("/itemNameSearch")
	public String itemNameSearch(Model model) {
		model.addAttribute("items", itemRepository.findAll());
		return "common/sidebar";
	}

	@RequestMapping(path = "/item/list/categoryName", method = RequestMethod.GET)
	public String itemName(String categoryName, Model model) {

		List<Item> itemNameList = itemRepository.findByNameLike("%" + categoryName + "%");
		List<ItemBean> itemNameBeanList = BeanCopy.copyEntityToItemBean(itemNameList);
		model.addAttribute("items", itemNameBeanList);
		return "item/list/item_list";

	}

}
