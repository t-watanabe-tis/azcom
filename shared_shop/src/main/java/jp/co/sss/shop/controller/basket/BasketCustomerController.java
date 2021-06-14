package jp.co.sss.shop.controller.basket;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class BasketCustomerController {


	@RequestMapping(path = "/basket/list", method = RequestMethod.GET)
	public String showBasketList() {

		return "basket/shopping_basket";
	}
 }
