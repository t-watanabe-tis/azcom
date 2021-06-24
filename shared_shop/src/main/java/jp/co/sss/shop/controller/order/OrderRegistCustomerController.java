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
import jp.co.sss.shop.repository.ItemRepository;
import jp.co.sss.shop.repository.OrderItemRepository;
import jp.co.sss.shop.repository.OrderRepository;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 注文登録コントロールクラス
 *
 * @author SystemShared
 */
@Controller
public class OrderRegistCustomerController {

	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * ユーザー情報
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * 商品情報
	 */
	@Autowired
	ItemRepository itemRepository;

	/**
	 * 注文商品情報
	 */
	@Autowired
	OrderItemRepository orderItemRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;


	/**
	 * 買い物かご処理
	 *
	 * @param orderForm　注文情報
	 * @param backFlg　ページ遷移の真偽
	 * @return"order/regist/order_address_input"届け先入力画面へ
	 */
	@RequestMapping(path = "/address/input", method = RequestMethod.POST)
	public String inputAddress(@ModelAttribute OrderForm orderForm, boolean backFlg) {

		if(!backFlg) {
			UserBean userBean = (UserBean) session.getAttribute("user");
			User user =  userRepository.getOne(userBean.getId());

			orderForm.setPostalCode(user.getPostalCode());
			orderForm.setAddress(user.getAddress());
			orderForm.setName(user.getName());
			orderForm.setPhoneNumber(user.getPhoneNumber());
			orderForm.setUserId(user.getId());

		}

		return "order/regist/order_address_input";
	}


	/**
	 * 届け先入力処理
	 *
	 * @param orderForm　注文情報
	 * @param result　入力チェックの結果
	 * @param backFlg　ページ遷移の真偽
	 * @return"order/regist/order_address_input"届け先入力画面へ
	 */
	@RequestMapping(path = "/address/input", method = RequestMethod.GET)
	public String inputAddressRedirect(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, boolean backFlg) {

		if(!backFlg)  {

			UserBean userBean = (UserBean) session.getAttribute("user");
			User user =  userRepository.getOne(userBean.getId());
			orderForm.setPostalCode(user.getPostalCode());
			orderForm.setAddress(user.getAddress());
			orderForm.setName(user.getName());
			orderForm.setPhoneNumber(user.getPhoneNumber());
			orderForm.setUserId(user.getId());

		}

		return "order/regist/order_address_input";
	}

	/**
	 * ページ遷移の真偽による届け先入力画面への処理
	 *
	 * @param orderForm　注文情報
	 * @param result　入力チェックの結果
	 * @param backFlg　ページ遷移の真偽
 	*/
	@RequestMapping(path = "/payment/input", method = RequestMethod.POST)
	public String inputPayment(@Valid @ModelAttribute OrderForm orderForm, BindingResult result, boolean backFlg) {

		if(backFlg) {

			return "order/regist/order_payment_input";
		}

		if(result.hasErrors()) {

			return inputAddress(orderForm, backFlg);
		}
		else {

			return "order/regist/order_payment_input";
		}
	}

	/**
	 * 支払選択処理
	 *
	 * @param orderForm
	 * @param model
	 * @return"order/regist/order_check"注文確認画面へ
	 */
	@RequestMapping(path = "/order/check", method = RequestMethod.POST)
	public String checkOrder(@ModelAttribute OrderForm orderForm, Model model) {

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

		//買い物かご内商品の合計額を算出
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
		model.addAttribute("order", orderForm);

		return "order/regist/order_check";

	}


	/**
	 * 注文登録確認処理
	 *
	 * @param orderForm　ユーザー情報
	 * @param model　viewとの値渡し
	 * @return"redirect:/order/complete"注文登録完了画面へ
	 */
	@RequestMapping(path = "/order/complete", method = RequestMethod.POST)
	public String completeOrder(OrderForm orderForm, Model model) {

		//ダブルクリック対策機能の確認のため、2秒経過後に画面遷移するよう設定
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}
		Order order = new Order();
		User user = new User();
		user.setId(orderForm.getUserId());

		//過去に同じ注文がされているか確認
		List<Order> orderList = orderRepository.findByToken(orderForm.getToken());
		if(orderList.isEmpty()) {

			// ordersテーブルに登録
			order.setPostalCode(orderForm.getPostalCode());
			order.setAddress(orderForm.getAddress());
			order.setName(orderForm.getName());
			order.setPhoneNumber(orderForm.getPhoneNumber());
			order.setPayMethod(orderForm.getPayMethod());
			order.setUser(user);
			order.setInsertDate(new Date(new java.util.Date().getTime()));
			order.setToken(orderForm.getToken());

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

				//購入した商品数を在庫数に反映
				itemRepository.updateStockById(item.getStock() - item.getQuantityInBasket(), item.getId());

				orderItemRepository.save(orderItem);
			}

			//買い物かごの中身を空にする
			basket.clear();

			return "redirect:/order/complete";
		}
		else {

			model.addAttribute("duplicatedOrder", true);
			return checkOrder(orderForm, model);

		}


	}

	/**
	 * リダイレクト用注文確認処理
	 *
	 * @return"order/regist/order_complete"注文登録完了画面へ
	 */
	@RequestMapping(path = "/order/complete", method = RequestMethod.GET)
	public String showOrderComp() {
		return "order/regist/order_complete";
	}
}

