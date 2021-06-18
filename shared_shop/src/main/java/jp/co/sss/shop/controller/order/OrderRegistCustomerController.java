package jp.co.sss.shop.controller.order;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.Item;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.ItemForm;
import jp.co.sss.shop.form.OrderForm;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

@Controller
public class OrderRegistCustomerController {

	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	OrderItemRepository orderItemRepository;


	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	//買い物かご画面から届け先入力画面
	@RequestMapping(path = "/address/input", method = RequestMethod.POST)
	public String inputAddress(@ModelAttribute UserForm form, boolean backFlg) {
		/*public String inputAddress(@ModelAttribute OrderForm orderForm, UserForm form, boolean backFlg) {*/

		if(!backFlg) {
			UserBean userBean = (UserBean) session.getAttribute("user");
			//			User user =  userRepository.getOne(userBean.getId());
			User user =  userRepository.getOne(userBean.getId());
			form.setId(user.getId());
			form.setPostalCode(user.getPostalCode());
			form.setAddress(user.getAddress());
			form.setName(user.getName());
			form.setPhoneNumber(user.getPhoneNumber());

		}
		return "order/regist/order_address_input";
	}
	//届け先入力画面
	@RequestMapping(path = "/address/input", method = RequestMethod.GET)
public String inputAddressRedirect(@ModelAttribute UserForm form, boolean backFlg) {
//	public String inputAddressRedirect(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, UserForm form, boolean backFlg) {
//		if(result.hasErrors()) {
//			return inputAddress(orderForm);
//		}


		if(backFlg) {
			Order order = new Order();
			order.setId(form.getId());
		} else  {

			//			UserBean userBean = (UserBean) session.getAttribute("user");
			//			User user =  userRepository.getOne(userBean.getId());
			UserBean userBean = (UserBean) session.getAttribute("user");
			User user =  userRepository.getOne(userBean.getId());
			form.setId(user.getId());
			form.setPostalCode(user.getPostalCode());
			form.setAddress(user.getAddress());
			form.setName(user.getName());
			form.setPhoneNumber(user.getPhoneNumber());

		}

		return "order/regist/order_address_input";
	}

	//届け先入力画面から支払方法選択画面
	@RequestMapping(path = "/payment/input", method = RequestMethod.POST)
	public String inputPayment(@ModelAttribute UserForm form, BindingResult result, boolean backFlg, Model model) {

		//
		if(result.hasErrors()) {
			//				return "order/regist/order_address_input";
			return inputAddress(form, !backFlg);
		} else {

			model.addAttribute("order", form);
			return "order/regist/order_payment_input";
		}
		//			return "order/regist/order_payment_input";

	}

	//支払方法選択画面から注文登録確認画面
	@RequestMapping(path = "/order/check", method = RequestMethod.POST)
	public String checkOrder(@ModelAttribute ItemForm itemForm, BindingResult result, UserForm form, boolean backFlg, Model model) {

		//買い物かごの商品個数が在庫数を超過しているか判定
		List<ItemBean> orverStockItems = new ArrayList<>();
		List<ItemBean> zeroStockItems = new ArrayList<>();
		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		for(ItemBean item: basket) {

			int currentStock = itemRepository.getOne(item.getId()).getStock();
			if(currentStock == 0) {

				zeroStockItems.add(item);
			}
			if(item.getQuantityInBasket() > currentStock) {

				orverStockItems.add(item);
			}
		}

		//買い物かご内商品の合計額
		int totalPrice = 0;
		for(ItemBean item: basket) {

			int s = item.getQuantityInBasket() * item.getPrice();
			totalPrice += s;
		}
		model.addAttribute("totalPrice", totalPrice);

		/*System.out.println(totalPrice);*/

		model.addAttribute("orverStockItems", orverStockItems);

		model.addAttribute("order", form);
		return "order/regist/order_check";
	}

    //注文登録確認画面から注文登録完了画面
	@RequestMapping(path = "/order/complete", method = RequestMethod.POST)
	public String completeOrder(OrderForm form, UserForm userForm) {
		Order order = new Order();

		User user = new User();
		user.setId(form.getId());



		// ordersテーブルに登録
//		order.setId(form.getId());
		order.setPostalCode(form.getPostalCode());
		order.setAddress(form.getAddress());
		order.setName(form.getName());
		order.setPhoneNumber(form.getPhoneNumber());
		order.setPayMethod(form.getPayMethod());
		order.setUser(user);
		order.setInsertDate(new Date(new java.util.Date().getTime()));
//		order.setOrderItemsList(orderItemList);
		orderRepository.save(order);


//		  order_itemsテーブルに登録
		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		for(ItemBean item: basket) {

			OrderItem orderItem = new OrderItem();
			Item i = new Item();
			i.setId(item.getId());

			orderItem.setQuantity(item.getQuantityInBasket());
			orderItem.setPrice(item.getPrice());
			orderItem.setItem(i);
			orderItem.setOrder(order);

			orderItemRepository.save(orderItem);
		}

		//買い物かごの中身を空にする
		basket.clear();

		return "order/regist/order_complete";

	}
}

