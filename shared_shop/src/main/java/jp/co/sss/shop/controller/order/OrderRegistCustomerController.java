package jp.co.sss.shop.controller.order;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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


		if(!backFlg) {
			UserBean userBean = (UserBean) session.getAttribute("user");
			//			User user =  userRepository.getOne(userBean.getId());
			User user =  userRepository.getOne(userBean.getId());
			form.setId(user.getId());
			form.setPostalCode(user.getPostalCode());
			form.setAddress(user.getAddress());
			form.setName(user.getName());
			form.setPhoneNumber(user.getPhoneNumber());

			form.setEmail(user.getEmail());
			form.setPassword(user.getPassword());
			form.setAuthority(user.getAuthority());
			form.setDeleteFlag(user.getDeleteFlag());
			/*form.setInsertDate(user.getInsertDate());*/

		}
		return "order/regist/order_address_input";
	}
	//届け先入力画面
	@RequestMapping(path = "/address/input", method = RequestMethod.GET)
	public String inputAddressRedirect(@Valid @ModelAttribute UserForm form, BindingResult result, boolean backFlg) {



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

			form.setEmail(user.getEmail());
			form.setPassword(user.getPassword());
			form.setAuthority(user.getAuthority());
			form.setDeleteFlag(user.getDeleteFlag());


		}


		return "order/regist/order_address_input";
	}




	//届け先入力画面から支払方法選択画面
	@RequestMapping(path = "/payment/input", method = RequestMethod.POST)
	public String inputPayment(@Valid @ModelAttribute UserForm form, BindingResult result, boolean backFlg, Model model) {

		System.out.println(result.hasErrors());

		if(result.hasErrors()) {
			//				return "order/regist/order_address_input";
			return inputAddress(form, !backFlg);
		} else {

			model.addAttribute("order", form);
			return "order/regist/order_payment_input";
		}
		//				return "order/regist/order_payment_input";

	}

	//支払方法選択画面から注文登録確認画面
	@RequestMapping(path = "/order/check", method = RequestMethod.POST)
	//	public String checkOrder(@ModelAttribute ItemForm itemForm, BindingResult result, UserForm form, boolean backFlg, Model model) {
	public String checkOrder(@Valid UserForm form, BindingResult result, Model model) {

//		System.out.println(result.hasErrors());
		System.out.println(form.getPayMethod());


		//買い物かごの商品個数が在庫数を超過しているか判定
		List<ItemBean> orverStockItems = new ArrayList<>();
		List<ItemBean> zeroStockItems = new ArrayList<>();
		List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
		for(ItemBean item: basket) {

			int currentStock = itemRepository.getOne(item.getId()).getStock();
			if(currentStock == 0) {

				item.setStock(0);
				zeroStockItems.add(item);
			}
			if( currentStock != 0  &&  item.getQuantityInBasket() > currentStock) {

				item.setQuantityInBasket(currentStock);
				orverStockItems.add(item);
			}
		}


		//在庫がない商品を買い物かごから削除
		Iterator<ItemBean> iter = basket.iterator();
		while(iter.hasNext()) {

			ItemBean ib = iter.next();
			if(ib.getStock() == 0) {
				iter.remove();
			}
		}




		//買い物かご内商品の合計額
		int totalPrice = 0;
		for(ItemBean ib: basket) {

			int s = ib.getQuantityInBasket() * ib.getPrice();
			totalPrice += s;
		}

		//注文情報を一意に識別するトークンの生成
		String toReturn = null;
		Integer randNum = new Random().nextInt(100);
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update((new java.util.Date().toString() + randNum.toString()).getBytes());
			toReturn = String.format("%04x", new BigInteger(1, digest.digest()));

		} catch(Exception e) {

			e.printStackTrace();
		}

		model.addAttribute("totalPrice", totalPrice);
		model.addAttribute("token", toReturn);
		model.addAttribute("orverStockItems", orverStockItems);
		model.addAttribute("zeroStockItems", zeroStockItems);
		model.addAttribute("order", form);

		return "order/regist/order_check";

	}

	//注文登録確認画面から注文登録完了画面
	@RequestMapping(path = "/order/complete", method = RequestMethod.POST)
	public String completeOrder(OrderForm form, UserForm userForm, Model model) {
		Order order = new Order();

		User user = new User();
		user.setId(form.getId());

		//ダブルクリック対策機能の確認のため、3秒経過後に画面遷移するよう設定
		try {
			Thread.sleep(3000); // 3秒間だけ処理を止める
		} catch (InterruptedException e) {
		}

		//過去に同じ注文がされているか確認
		List<Order> orderList = orderRepository.findByToken(form.getToken());
		if(orderList.isEmpty()) {

			// ordersテーブルに登録
			//			order.setId(form.getId());
			order.setPostalCode(form.getPostalCode());
			order.setAddress(form.getAddress());
			order.setName(form.getName());
			order.setPhoneNumber(form.getPhoneNumber());
			order.setPayMethod(form.getPayMethod());
			order.setUser(user);
			order.setInsertDate(new Date(new java.util.Date().getTime()));
			order.setToken(form.getToken());
			//			order.setOrderItemsList(orderItemList);
			orderRepository.save(order);


			//			  order_itemsテーブルに登録
			List<ItemBean> basket = (List<ItemBean>) session.getAttribute("basket");
			for(ItemBean item: basket) {

				OrderItem orderItem = new OrderItem();
				Item i = new Item();
				i.setId(item.getId());

				orderItem.setQuantity(item.getQuantityInBasket());
				orderItem.setPrice(item.getPrice());
				orderItem.setItem(i);
				orderItem.setOrder(order);


				//				在庫数を減らす
				itemRepository.updateStockById(item.getStock() - item.getQuantityInBasket(), item.getId());


				orderItemRepository.save(orderItem);
			}

			//買い物かごの中身を空にする
			basket.clear();
			//			model.addAttribute("duplicatedOrder", false);
			return "redirect:/order/complete";
		}
		else {

			model.addAttribute("duplicatedOrder", true);
//			return checkOrder(userForm, model);
			return "order/regist/order_check";
		}




	}

	@RequestMapping(path = "/order/complete")
	public String showOrderComp() {
		return "order/regist/order_complete";
	}
}

