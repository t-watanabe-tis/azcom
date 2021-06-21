package jp.co.sss.shop.controller.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
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

	@Autowired
	OrderItemRepository orderItemRepository;

	@Autowired
	EntityManager entityManager;

	/**
	 * トップ画面 表示処理
	 *
	 * @param model    Viewとの値受渡し
	 * @param categoryId
	 * @param pageable ページング情報
	 * @return "/" トップ画面へ
	 */
	@RequestMapping(path = "/")
	public String index(Model model, Pageable pageable) {

		Page<Item> sale = itemRepository.findBySaleItemsQuery(pageable);

		model.addAttribute("saleItems", sale);

		return "/index";
	}

	@RequestMapping("/item/list")
	public String showNewItemList(Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByInsertDateDescIdAsc(Constant.NOT_DELETED, pageable);
		List<Item> il = itemList.getContent();

		// エンティティ内の検索結果をJavaBeansにコピー
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());
		// 会員情報をViewに渡す

		for (Item item : il) {
			System.out.println(item.getName());
		}

		model.addAttribute("pages", itemList);
		model.addAttribute("items", itemBeanList);
		model.addAttribute("url", "/item/list");

		model.addAttribute("sort", "NewArrival");

		return "item/list/item_list";
	}

	//商品詳細画面
	@RequestMapping(path = "/item/detail/{id}")
	public String create(@PathVariable int id, Model model) {
		//		商品関連表示
		//		id（item）をもとに商品情報を取得 例：オレンジ

		Item targetItem = itemRepository.getOne(id);
//		ItemBean itemBean = BeanCopy.copyEntityToBean(itemRepository.getOne(id));


		//		取得した商品情報のid(item)をもとにしてid(category)が一致するものを取得　例：食料品
//		Integer targetCategoryId = targetItem.getCategoryId();
//		List<Item> sameCategoryItems = itemRepository.findByCategoryId(targetCategoryId);
		List<Item> sameCategoryItems = itemRepository.findByIdNotAndCategoryId(id, targetItem.getCategoryId());



		model.addAttribute("targetItem", sameCategoryItems);
		model.addAttribute("item", targetItem);

		return "item/detail/item_detail";
	}

	//カテゴリー別検索
	@RequestMapping(path = "/item/list/category", method = RequestMethod.GET)
	public String categorySearch(String[] args, Integer categoryId, Model model, Pageable pageable, String sorted) {

		Category category = new Category();
		category.setId(categoryId);

		Page<Item> itemList = itemRepository.findByDeleteFlagAndCategory(Constant.NOT_DELETED, category, pageable);

		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		if (sorted.equals("NewArrival")) {

			ArrayList<ItemBean> ItemBeansort = new ArrayList<>();
			ItemBeansort.addAll(itemBeanList);
			Collections.sort(itemBeanList, new Comparator<ItemBean>() {
				@Override
				public int compare(ItemBean ItemBeanFirst, ItemBean ItemBeanSecond) {
					return ItemBeanFirst.getInsertDate().compareTo(ItemBeanSecond.getInsertDate());
				}
			});

		}

		else if (sorted.equals("saleDesc")) {

			itemList = itemRepository.findBySumQuantityQuery(categoryId, pageable);
			itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		} else if (sorted.equals("PriceAsc")) {

			ArrayList<ItemBean> ItemBeansort = new ArrayList<>();
			ItemBeansort.addAll(itemBeanList);
			Collections.sort(itemBeanList, new Comparator<ItemBean>() {
				@Override
				public int compare(ItemBean ItemBeanFirst, ItemBean ItemBeanSecond) {
					return ItemBeanFirst.getPrice().compareTo(ItemBeanSecond.getPrice());
				}
			});

		} else if (sorted.equals("PriceDesc")) {

			ArrayList<ItemBean> ItemBeansort = new ArrayList<>();
			ItemBeansort.addAll(itemBeanList);
			Collections.sort(itemBeanList, new Comparator<ItemBean>() {
				@Override
				public int compare(ItemBean ItemBeanFirst, ItemBean ItemBeanSecond) {
					return ItemBeanSecond.getPrice().compareTo(ItemBeanFirst.getPrice());
				}
			});

		}

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list");
		model.addAttribute("textCategoryId", categoryId);

		return "item/list/item_list";
	}

	//商品一覧並び替え【　商品の売れ筋順　】
	@RequestMapping(path = "/item/list/saleDesc", method = RequestMethod.GET)
	public String showItemListSaleDesc(Integer categoryId, Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findBySaleItemsQuery(pageable);

		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list/saleDesc");

		model.addAttribute("sort", "saleDesc");

		return "item/list/item_list";
	}

	//商品一覧並び替え【　価格の低い順　】
	@RequestMapping(path = "/item/list/priceAsc", method = RequestMethod.GET)
	public String showItemListPriceAsc(Integer categoryId, Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByPriceAscIdAsc(Constant.NOT_DELETED, pageable);
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list/priceAsc");

		model.addAttribute("sort", "PriceAsc");

		return "item/list/item_list";
	}

	//商品一覧並び替え【　価格の高い順　】
	@RequestMapping(path = "/item/list/priceDesc", method = RequestMethod.GET)
	public String showItemListPriceDesc(Model model, Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagOrderByPriceDescIdAsc(Constant.NOT_DELETED, pageable);
		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());

		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list/priceDesc");

		model.addAttribute("sort", "PriceDesc");

		return "item/list/item_list";
	}

	//商品名検索
	@RequestMapping(path = "/item/list/categoryName", method = RequestMethod.GET)
	public String itemName(@ModelAttribute ItemBean itemBean, String categoryName, Model model,
			Pageable pageable) {

		Page<Item> itemList = itemRepository.findByDeleteFlagAndNameLike(Constant.NOT_DELETED, "%" + categoryName + "%",
				pageable);

		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemList.getContent());
		model.addAttribute("items", itemBeanList);
		model.addAttribute("pages", itemList);
		model.addAttribute("url", "/item/list");
		model.addAttribute("textCategoryName", categoryName);

		return "item/list/item_list";

	}

}
