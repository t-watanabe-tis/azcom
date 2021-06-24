package jp.co.sss.shop.controller.user;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.repository.UserRepository;
/**
 * 一般会員の詳細情報表示するコントローラクラス
 * @author test
 *
 */
@Controller
public class UserShowCustomerController {

	/**
	 * セッション
	 */
	@Autowired
	HttpSession session;

	/**
	 * 会員情報を取得
	 */
	@Autowired
	UserRepository userRepository;

	/**
	 * URLを隠すためにIDを格納するフィールドを用意
	 */
	private int hiddenId;

	/**
	 * URLに表示されるIDを隠すメソッド
	 * @param id URLからIDを取得します。
	 * @return 会員詳細画面を表示するメソッドにリダイレクトすることでIDをURLに表示しないようにします。
	 */
	@RequestMapping("/user/detail/{id}")
	public String hiddenId(@PathVariable int id) {
		this.hiddenId = id;

		return "redirect:/user/detail";
	}

	/**
	 * 一般ユーザーの会員詳細画面を表示するメソッド
	 * @param model 会員の情報を格納するためのモデル
	 * @return 会員詳細画面へ遷移
	 */
	@RequestMapping("/user/detail")
	public String showCustomerController(Model model) {
		this.hiddenId = ((UserBean)session.getAttribute("user")).getId();
		//フィールドのIDから検索して、Viewに渡す。
		model.addAttribute("user",userRepository.getOne(this.hiddenId));
		//ユーザー詳細情報画面に遷移
		return"/user/detail/user_detail";
	}
}