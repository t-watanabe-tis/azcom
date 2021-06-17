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

	//商品を新着順で取得し、一覧表示
	@RequestMapping("/item/list")
	public String showNewItemList(Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByInsertDateDescIdAsc(Constant.NOT_DELETED, pageable);
		List<Item> il = itemList.getContent();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());
		// 会員情報をViewに渡す

		for(Item item: il) {
			System.out.println(item.getName());
		}

		model.addAttribute("pages", itemList);
		model.addAttribute("items", itemBeanList);
		model.addAttribute("url", "/item/list");
		return "item/list/item_list";
	}



	//商品詳細画面
	@RequestMapping(path = "/item/detail/{id}")
	public String create(@PathVariable int id, Model model) {

		model.addAttribute("item", itemRepository.getOne(id));
		return "item/detail/item_detail";
	}


	//カテゴリー別検索
	@RequestMapping(path = "/item/list/category", method = RequestMethod.GET)
	public String categorySearch(Integer categoryId, Model model, Pageable pageable) {

		Category category = new Category();
		category.setId(categoryId);

		Page<Item> itemList = itemRepository.findByDeleteFlagAndCategory(Constant.NOT_DELETED, category, pageable);
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list");

		return "item/list/item_list";
	}


	//商品名検索
	@RequestMapping(path = "/item/list/categoryName", method = RequestMethod.GET)
	public String itemName(String categoryName, Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagAndNameLike(Constant.NOT_DELETED, "%" + categoryName + "%", pageable);
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list");

		return "item/list/item_list";

	}


	//商品一覧並び替え【　価格の低い順　】
	@RequestMapping(path = "/item/list/priceAsc", method = RequestMethod.GET)
	public String showItemListPriceAsc(Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByPriceAscIdAsc(Constant.NOT_DELETED, pageable);
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list");

		return "item/list/item_list";
	}


	//商品一覧並び替え【　価格の高い順　】
	@RequestMapping(path = "/item/list/priceDesc", method = RequestMethod.GET)
	public String showItemListPriceDesc(Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByPriceDescIdAsc(Constant.NOT_DELETED, pageable);
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list");

		return "item/list/item_list";
	}


}
