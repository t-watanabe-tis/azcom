package jp.co.sss.shop.controller.basket;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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


	//買い物かご一覧表示
	@RequestMapping(path = "/basket/list", method = RequestMethod.GET)
	public String showBasketList() {

		return "basket/shopping_basket";
	}


	/*
	 * 買い物かごへの商品追加
	 * 買い物かご内一覧
	 *
	 */
	@RequestMapping(path = "/basket/add", method = RequestMethod.POST)
	public String addItem(Integer id, HttpSession session) {

		/*
		 * idに一致するDBから商品の取得
		 * 在庫チェック
		 *
		 *かご内の同商品があれば数量の増加
		 * セッションのリストに格納
		 * 一覧へ遷移
		 *
		 */

		Item item = itemRepository.getOne(id);
		int currentStock = item.getStock();

		//在庫が1つ以上の場合、カートに追加
		if(item.getStock() > 0) {

			ItemBean itemBean = BeanCopy.copyEntityToBean(item);

			List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");

			//既に同じ商品が買い物かごに含まれているか判定
			if(basket.contains(itemBean)) {

				ItemBean ib = basket.get(basket.indexOf(itemBean));
				ib.setQuantityInBasket(ib.getQuantityInBasket() + 1);
				//DBの在庫数を反映
				ib.setStock(item.getStock());

				//追加した商品の総計が在庫数を超過するか判定
				if(ib.getQuantityInBasket() > currentStock) {
					System.out.println("在庫数を超過しています");
				}
			}
			else {

				itemBean.setQuantityInBasket(1);
				basket.add(itemBean);
			}
		}

		return "redirect:/basket/list";
	}
 }
