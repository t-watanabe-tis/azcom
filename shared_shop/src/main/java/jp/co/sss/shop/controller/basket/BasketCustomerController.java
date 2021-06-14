package jp.co.sss.shop.controller.basket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BasketCustomerController {


	//買い物かご一覧表示
	@RequestMapping(path = "/basket/list", method = RequestMethod.GET)
	public String showBasketList() {

		return "basket/shopping_basket";
	}


	//買い物かごへの商品追加
	@RequestMapping(path = "/basket/add", method = RequestMethod.POST)
	public String addItemToBasket(Integer id) {


	}
 }
