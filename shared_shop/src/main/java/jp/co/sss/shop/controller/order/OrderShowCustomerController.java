package jp.co.sss.shop.controller.order;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.OrderBean;
import jp.co.sss.shop.entity.Order;
import jp.co.sss.shop.entity.OrderItem;
import jp.co.sss.shop.form.OrderShowForm;
import jp.co.sss.shop.repository.OrderRepository;

/**
 * 注文管理 一覧表示機能(一般会員用)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class OrderShowCustomerController {

	/**
	 * 注文情報
	 */
	@Autowired
	OrderRepository orderRepository;

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 注文情報一覧表示処理
	 *
	 * @param model
	 *            Viewとの値受渡し
	 * @param form
	 *            表示用注文情報
	 * @param session
	 *            セッション情報
	 * @param pageable
	 *            ページング情報
	 * @return "order/list/order_list" 注文情報 一覧画面へ
	 */
	@RequestMapping(path = "/order/list", method = RequestMethod.GET)
	public String showOrderList(Model model, @ModelAttribute OrderShowForm form,
	        Pageable pageable) {

		// すべての注文情報を取得
		Page<Order> orderList = orderRepository.findByUserIdOrderByInsertDateDesc(3, pageable);
		// 注文情報リストを生成
		List<OrderBean> orderBeanList = new ArrayList<OrderBean>();
		for (Order order : orderList) {
			OrderBean orderBean = new OrderBean();
			orderBean.setId(order.getId());

			orderBean.setInsertDate(order.getInsertDate().toString());
			orderBean.setPayMethod(order.getPayMethod());

			//注文時点の単価を合計するための一時変数
			int total = 0;


			//orderレコードから紐づくOrderItemのListを取り出す
			List<OrderItem> orderItemList = order.getOrderItemsList();

			for(OrderItem orderItem :  orderItemList) {

				//購入時単価 * 買った個数をもとめて、合計に加算
				total += ( orderItem.getPrice() * orderItem.getQuantity() );
			}
			//Orderに改めて注文時点の単価をセット
			orderBean.setTotal(total);

			orderBeanList.add(orderBean);
		}

		// 注文情報リストをViewへ渡す
		model.addAttribute("pages", orderList);
		model.addAttribute("orders", orderBeanList);
		model.addAttribute("url", "/order/list");

		return "order/list/order_list";

	}


}
