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

/**
 * 買い物かご管理のコントロールクラス
 * @author SystemShared
 *
 */
@Controller
public class BasketCustomerController {


	/**
	 * 商品情報
	*/
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 買い物かごに入っている商品情報
	 */
	@Autowired
	HttpSession session;


	/**
	 * ナビゲーションバーから買い物かご一覧表示
	 *
	 * @return "basket/shopping_basket"買い物かご一覧画面へ
	 */
	@RequestMapping(path = "/basket/list", method = RequestMethod.GET)
	public String basketListGet() {

		//買い物かご内商品在庫数の更新
		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		if(!basket.isEmpty()) {

			for(ItemBean item: basket) {

				Integer currentStock = itemRepository.getOne(item.getId()).getStock();
				item.setStock(currentStock);
			}
		}

		return "basket/shopping_basket";
	}

	/**
	 * リダイレクト用買い物かご一覧表示処理
	 *
	 * @return"basket/shopping_basket"買い物かご一覧画面へ
	 */
	@RequestMapping(path = "/basket/list", method = RequestMethod.POST)
	public String basketList() {

		return "basket/shopping_basket";
	}

	/**
	 * 買い物かごへの商品追加
	 *
	 * @param id itemRepositoryから取ってくる商品id
	 * @param session 買い物かご情報
	 * @param model viewとの値渡し
	 * @return
	 */
	@RequestMapping(path = "/basket/add", method = RequestMethod.POST)
	public String addItem(Integer id, HttpSession session, Model model) {

		Item item = itemRepository.getOne(id);
		int currentStock = item.getStock();

		//在庫が1つ以上の場合、カートに追加
		if(item.getStock() > 0) {

			ItemBean itemBean = BeanCopy.copyEntityToBean(item);

			List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");


			//既に同じ商品が含まれている場合、商品数を加算する
			if(basket.contains(itemBean)) {

				ItemBean ib = basket.get(basket.indexOf(itemBean));

				//商品の総量が在庫数以下であれば、商品数を加算
				if( ib.getQuantityInBasket() + 1 <= currentStock ) {

					ib.setQuantityInBasket(ib.getQuantityInBasket() + 1);
				}
				else {

					//追加した分の総量が在庫数より多ければ、メッセージを表示
					model.addAttribute("orverStockItemName", ib.getName());
					return "basket/shopping_basket";
				}

			}
			//同じ商品が含まれていない場合、バスケットリストに商品オブジェクトを追加し、商品数を１にする
			else {

				itemBean.setQuantityInBasket(1);
				basket.add(itemBean);
			}
		}

		return "redirect:/basket/list";
	}


	/**
	 * 買い物かご内の商品削除
	 *
	 * @param itemBean 商品情報クラス
	 * @return "redirect:/basket/list"リダイレクト用買い物かご一覧表示画面へ
	 */
	@RequestMapping(path = "/basket/delete", method = RequestMethod.POST)
	public String deleteItem(ItemBean itemBean) {

		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		ItemBean itemInBasket = basket.get(basket.indexOf(itemBean));

		//削除対象商品が1つの場合商品を買い物かごから除外
		if(itemInBasket.getQuantityInBasket() == 1) {

			basket.remove(basket.indexOf(itemBean));
		}
		//削除対象商品が2つ以上の場合、個数を1減算する
		else if (itemInBasket.getQuantityInBasket() >= 2) {

			itemInBasket.setQuantityInBasket(itemInBasket.getQuantityInBasket() - 1);
		}

		return "redirect:/basket/list";
	}


	/**
	 * 買い物かご内の全商品削除
	 *
	 * @return"redirect:/basket/list"リダイレクト用買い物かご一覧表示画面へ
	 */
	@RequestMapping(path = "/basket/allDelete", method = RequestMethod.POST)
	public String deleteAll() {

		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		basket.clear();

		return "redirect:/basket/list";
	}


}
