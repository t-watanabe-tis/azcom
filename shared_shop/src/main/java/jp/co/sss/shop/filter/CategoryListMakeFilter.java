package jp.co.sss.shop.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import jp.co.sss.shop.bean.CategoryBean;
import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.repository.CategoryRepository;
import jp.co.sss.shop.util.BeanCopy;
import jp.co.sss.shop.util.Constant;
import jp.co.sss.shop.util.URLCheck;

/**
 *  カテゴリ一覧取得用フィルター
 *
 * @author System Shared
 */
@Component
public class CategoryListMakeFilter implements Filter {

	@Autowired
	CategoryRepository categoryRepository;

	/**
	 * フィルタの初期化時にこのフィルタ内のAutowiredを実行する
	 *
	 * @param filterConfig フィルタの初期化時、コンテナーからフィルターに情報を渡すためのフィルター構成オブジェクト
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// このフィルタークラスの@Autowiredインジェクションを処理
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// リクエスト情報を取得
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		if (checkRequestURL(httpRequest)) {

			// カテゴリ情報を全件検索
			List<Category> categoryList = categoryRepository
					.findByDeleteFlagOrderByInsertDateDescIdAsc(Constant.NOT_DELETED);

			// エンティティ内の検索結果をJavaBeansにコピー
			List<CategoryBean> categoryBeanList = BeanCopy.copyEntityToCategoryBean(categoryList);

			//リクエストスコープに検索結果を保存
			httpRequest.setAttribute("categories", categoryBeanList);
		}
		chain.doFilter(request, response);
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

		if ((!URLCheck.checkURLForStaticFile(requestURL)
				&& requestURL.indexOf("/admin") == -1)
				&& (requestURL.endsWith("/")
						|| requestURL.indexOf("/item/list") != -1
						|| requestURL.indexOf("/item/detail") != -1
						|| requestURL.indexOf("/item/regist/input") != -1
						|| requestURL.indexOf("/item/update/input") != -1
						|| requestURL.indexOf("/basket") != -1
						|| requestURL.indexOf("/address") != -1
						|| requestURL.indexOf("/payment/input") != -1
						|| requestURL.indexOf("/order/list") != -1
						|| requestURL.indexOf("/order/check") != -1
						|| requestURL.indexOf("/order/detail") != -1
						|| requestURL.indexOf("/order/complete") != -1
						|| requestURL.indexOf("/user/detail") != -1
						|| requestURL.indexOf("/user/regist") != -1
						|| requestURL.indexOf("/user/update") != -1
						|| requestURL.indexOf("/user/delete") != -1)) {

			// URLのリクエスト先がフィルタ実行対象である場合
			return true;

		} else {
			// URLのリクエスト先がフィルタ実行対象ではない場合
			return false;
		}
	}
}
