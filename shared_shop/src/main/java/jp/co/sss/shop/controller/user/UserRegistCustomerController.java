package jp.co.sss.shop.controller.user;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;

/**
 * 会員管理 登録機能(会員、非会員)のコントローラクラス
 *
 * @author SystemShared
 */
@Controller
public class UserRegistCustomerController {

	/**
	 * 会員情報
	 */
	@Autowired
	UserRepository userRepository;

	@Autowired
	HttpSession session;

	/**
	 * 会員情報入力画面表示処理
	 *
	 * @param Model Viewとの値受渡し
	 * @return "user/regist/user_regist_input" 会員情報 登録入力画面へ
	 */
	@RequestMapping(path = "/user/regist/input", method = RequestMethod.GET)
	public String registInput(Model model) {

		if (!model.containsAttribute("userForm")) {
			UserForm userForm = new UserForm();
			UserBean user = (UserBean) session.getAttribute("user");

			model.addAttribute("userForm", userForm);
		}
		return "user/regist/user_regist_input";
	}

	/**
	 * POSTメソッドを利用して会員情報入力画面に戻る処理
	 *
	 * @param form 会員情報
	 * @return "user/regist/user_regist_inputn" 会員情報 登録入力画面へ
	 */
	@RequestMapping(path = "/user/regist/input", method = RequestMethod.POST)
	public String registInput(UserForm form) {

		return "user/regist/user_regist_input";
	}

	/**
	 * 会員情報 登録確認処理
	 *
	 * @param form   会員情報フォーム
	 * @param result 入力チェック結果
	 * @return 入力値エラーあり："user/regist/user_regist_input" 会員情報登録画面へ
	 *         入力値エラーなし："user/regist/user_regist_check" 会員情報 登録確認画面へ
	 */
	@RequestMapping(path = "/user/regist/check", method = RequestMethod.POST)
	public String registCheck(@Valid @ModelAttribute UserForm form, BindingResult result) {

		// 入力値にエラーがあった場合、会員情報 入力画面表示処理に戻る
		if (result.hasErrors()) {
			return "user/regist/user_regist_input";
		}

		return "user/regist/user_regist_check";
	}

	/**
	 * 会員情報登録完了処理
	 *
	 * @param form 会員情報
	 * @return "redirect:/user/regist/complete" 会員情報 登録完了画面へ
	 */
	@RequestMapping(path = "/user/regist/complete", method = RequestMethod.POST)
	public String registComplete(@ModelAttribute UserForm form) {
		// 会員情報の生成
		User user = new User();

		// 入力値を会員情報にコピー
		BeanUtils.copyProperties(form, user);

		// 会員情報を保存
		user.setAuthority(2);
		userRepository.save(user);

		return "redirect:/user/regist/complete";
	}

	/**
	 * 会員情報登録完了画面表示
	 *
	 * @param form 会員情報
	 * @return "user/regist/user_regist_complete" 会員情報 登録完了画面へ
	 */
	@RequestMapping(path = "/user/regist/complete", method = RequestMethod.GET)
	public String registCompleteRedirect() {
		return "user/regist/user_regist_complete";
	}

}
