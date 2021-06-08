package jp.co.sss.shop.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import jp.co.sss.shop.bean.UserBean;
import jp.co.sss.shop.util.URLCheck;

/**
 * 管理者向けアクセス制限用フィルタ
 * 
 * @author System Shared
 */
@Component
public class AdminAccountCheckFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// リクエスト情報を取得
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (checkRequestURL(httpRequest)) {
			// セッション情報を取得
			HttpSession session = httpRequest.getSession();

			if (session.getAttribute("user") != null) {
				UserBean user = (UserBean) session.getAttribute("user");

				if (user.getAuthority() == 1) {
					// セッション情報を削除
					session.invalidate();
					
					// レスポンス情報を取得
					HttpServletResponse httpResponse = (HttpServletResponse) response;

					// ログイン画面にリダイレクト
					httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
				} else {
					chain.doFilter(request, response);
				}
			} else {
				chain.doFilter(request, response);
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	/**
	 * リクエストURLがチェック対象であるかを判定
	 * 
	 * @param requestURL リクエストURL
	 * @return true：チェック対象、false：チェック対象外
	 */
	private boolean checkRequestURL(HttpServletRequest httpRequest) {
		// リクエストURLを取得
		String requestURL = httpRequest.getRequestURI();

		if (!URLCheck.checkURLForStaticFile(requestURL)
				&& requestURL.indexOf("/login") == -1
				&& requestURL.indexOf("/admin") == -1
				&& requestURL.indexOf("/category") == -1
				&& requestURL.indexOf("/user/list") == -1
				&& requestURL.indexOf("/item/regist") == -1
				&& requestURL.indexOf("/item/update") == -1
				&& requestURL.indexOf("/item/delete") == -1
				&& requestURL.indexOf("/logout") == -1) {
			// URLのリクエスト先がフィルタ実行対象である場合
			return true;
		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			return false;
		}
	}
}
