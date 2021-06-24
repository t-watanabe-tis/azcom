package jp.co.sss.shop.controller.user;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.entity.User;
import jp.co.sss.shop.form.UserForm;
import jp.co.sss.shop.repository.UserRepository;
import jp.co.sss.shop.util.Constant;
/**
 * 一般ユーザーの削除処理を行うコントローラクラス
 * @author SystemShared
 *
 */
@Controller
public class UserDeleteCustomerController {

   /**
    * 会員情報を取得
    */
   @Autowired
   UserRepository userRepository;

   /**
    * 削除する会員情報を入手するメソッド
    *
    * @param model formを格納するためのモデル
    * @param form ユーザ情報を得るためのform
    * @return 削除確認画面へ遷移
    */
   @RequestMapping(path = "/user/delete/check", method = RequestMethod.POST)
   public String deleteCheck(Model model, @ModelAttribute UserForm form) {
      // 削除対象の会員情報を取得
      User user = userRepository.findById(form.getId()).orElse(null);
      UserBean userBean = new UserBean();
      // Userエンティティの各フィールドの値をUserBeanにコピー
      BeanUtils.copyProperties(user, userBean);
      // 会員情報をViewに渡す
      model.addAttribute("user", userBean);
      return "user/delete/user_delete_check";
   }

   /**
    * 論理削除を行うメソッド
    *
    * @param form 削除対象の情報を取得するためのform
    * @return 削除完了画面へ遷移するメソッドにリクエスト
    */
   @RequestMapping(path = "/user/delete/complete", method = RequestMethod.POST)
   public String deleteComplete(@ModelAttribute UserForm form) {
      // 削除対象の会員情報を取得
      User user = userRepository.findById(form.getId()).orElse(null);
      // 削除フラグを立てる
      user.setDeleteFlag(Constant.DELETED);
      // 会員情報を保存
      userRepository.save(user);
      return "redirect:/user/delete/complete";
   }

   /**
    * 削除完了画面へ遷移するためのメソッド
    *
    * @param session ユーザが削除されたのでセッションを切るためにスコープを用意
    * @return 削除完了画面へ
    */
   @RequestMapping(path = "/user/delete/complete", method = RequestMethod.GET)
   public String deleteCompleteRedirect(HttpSession session) {

      //セッションを切る
      session.invalidate();
      return "user/delete/user_delete_complete";
   }
}