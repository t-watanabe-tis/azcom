package jp.co.sss.shop.controller.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.repository.ItemRepository;

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

	@RequestMapping("/newItems")
	public String showNewItemList(Model model) {
		return "item/list/item_list";
	}


	//商品詳細画面 嘉川
	@RequestMapping(path="newItems/item/detail/{id}")
		public String create(@PathVariable int id, Model model) {
			model.addAttribute("item", itemRepository.getOne(id));
//			System.out.println(itemRepository.getOne(id).getName());
				return "item/detail/item_detail";
		}

}
