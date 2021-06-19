package jp.co.sss.shop.controller.basket;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.util.BeanCopy;

@Controller
public class BasketCustomerController {

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	HttpSession session;


	//ナビゲーションバーからの買い物かご一覧表示
	@RequestMapping(path = "/basket/list", method = RequestMethod.GET)
	public String basketListGet() {

		//買い物かご内の在庫数の更新
		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		if(!basket.isEmpty()) {

			for(ItemBean item: basket) {

				Integer currentStock = itemRepository.getOne(item.getId()).getStock();
				item.setStock(currentStock);
			}
		}

		return "basket/shopping_basket";
	}

	//リダイレクト用買い物かご一覧表示
	@RequestMapping(path = "/basket/list", method = RequestMethod.POST)
	public String basketList() {

		return "basket/shopping_basket";
	}



	/*
	 * 買い物かごへの商品追加
	 *
	 *
	 */
	@RequestMapping(path = "/basket/add", method = RequestMethod.POST)
	public String addItem(Integer id, HttpSession session, Model model) {

		Item item = itemRepository.getOne(id);
		int currentStock = item.getStock();

		//在庫が1つ以上の場合、カートに追加
		if(item.getStock() > 0) {

			ItemBean itemBean = BeanCopy.copyEntityToBean(item);

			List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");

			//既に同じ商品が買い物かごに含まれているか判定
			if(basket.contains(itemBean)) {

				ItemBean ib = basket.get(basket.indexOf(itemBean));

				//商品の総量が在庫数を超過しているか判定
				//trueの場合カートに商品を追加
				if( ib.getQuantityInBasket() + 1 <= currentStock ) {

					ib.setQuantityInBasket(ib.getQuantityInBasket() + 1);
				} else {

					model.addAttribute("orverStockItemName", ib.getName());
					return "basket/shopping_basket";
				}

			}
			else {

				itemBean.setQuantityInBasket(1);
				basket.add(itemBean);
			}
		}

		return "redirect:/basket/list";
	}


	//カート内商品の削除
	@RequestMapping(path = "/basket/delete", method = RequestMethod.POST)
	public String deleteItem(ItemBean itemBean) {

		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		basket.remove(basket.indexOf(itemBean));

		return "redirect:/basket/list";
	}


	//カート内の全商品削除
	@RequestMapping(path = "/basket/allDelete", method = RequestMethod.POST)
	public String deleteAll() {

		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		basket.clear();

		return "redirect:/basket/list";
	}


}
