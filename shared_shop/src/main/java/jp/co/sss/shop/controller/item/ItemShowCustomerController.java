package jp.co.sss.shop.controller.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.BeanCopy;

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
	@RequestMapping("/item/list")
	public String showNewItemList(Model model) {

		List<ItemBean> itemBeanList = BeanCopy.copyEntityToItemBean(itemRepository.findAll());
		model.addAttribute("items", itemBeanList);
		return "item/list/item_list";
	}


	//商品詳細画面
	@RequestMapping(path="/item/detail/{id}")
	public String create(@PathVariable int id, Model model) {
		model.addAttribute("item", itemRepository.getOne(id));
		System.out.println("detail");
						System.out.println(itemRepository.getOne(id).getName());
		return "item/detail/item_detail";
	}



	@RequestMapping(path = "/categorySearch", method = RequestMethod.POST)
	public String categorySearch(Integer id, Model model) {
		Category category = new Category();

		category.setId(id);

		model.addAttribute("genre", categoryRepository.findAll());
		model.addAttribute("items", itemRepository.findByCategory(categoryRepository.getOne(id)));
		return "item/list/item_list";
	}

	@RequestMapping(path = "/itemSearch", method = RequestMethod.POST)
	public String itemSearch(String name, Model model) {
		model.addAttribute("items",itemRepository.findByNameLike("%" + name + "%"));

		return "item/list/itemSearch";

	}


}
