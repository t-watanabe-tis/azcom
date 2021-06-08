package jp.co.sss.shop.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jp.co.sss.shop.filter.AdminAccountCheckFilter;
import jp.co.sss.shop.filter.CategoryListMakeFilter;
import jp.co.sss.shop.filter.CustomerAccountCheckFilter;
import jp.co.sss.shop.filter.LoginCheckFilter;
import jp.co.sss.shop.filter.SystemAdminAccountCheckFilter;

/**
 * フィルタ設定クラス
 * 
 * @author SystemShared
 */
@Configuration
public class FilterConfig {
	/**
	 * 非会員向けアクセス制限用フィルタの設定
	 * 
	 * @return フィルタ設定情報
	 */
	@Bean
	public FilterRegistrationBean<LoginCheckFilter> configAccountCheckFilter() {
		FilterRegistrationBean<LoginCheckFilter> bean = new FilterRegistrationBean<LoginCheckFilter>();

		bean.setFilter(new LoginCheckFilter());
		bean.setOrder(1);
		return bean;
	}
	
	/**
	 * 一般会員向けアクセス制限用フィルタの設定
	 * 
	 * @return フィルタ設定情報
	 */
	@Bean
	public FilterRegistrationBean<CustomerAccountCheckFilter> configCustomerAccountCheckFilter() {
		FilterRegistrationBean<CustomerAccountCheckFilter> bean = new FilterRegistrationBean<CustomerAccountCheckFilter>();

		bean.setFilter(new CustomerAccountCheckFilter());
		bean.setOrder(2);
		return bean;
	}
	
	/**
	 * 管理者向けアクセス制限用フィルタの設定
	 * 
	 * @return フィルタ設定情報
	 */
	@Bean
	public FilterRegistrationBean<AdminAccountCheckFilter> configAdminAccountCheckFilter() {
		FilterRegistrationBean<AdminAccountCheckFilter> bean = new FilterRegistrationBean<AdminAccountCheckFilter>();

		bean.setFilter(new AdminAccountCheckFilter());
		bean.setOrder(3);
		return bean;
	}
	
	/**
	 * システム管理者向けアクセス制限用フィルタの設定
	 * 
	 * @return フィルタ設定情報
	 */
	@Bean
	public FilterRegistrationBean<SystemAdminAccountCheckFilter> configSystemAdminAccountCheckFilter() {
		FilterRegistrationBean<SystemAdminAccountCheckFilter> bean = new FilterRegistrationBean<SystemAdminAccountCheckFilter>();

		bean.setFilter(new SystemAdminAccountCheckFilter());
		bean.setOrder(4);
		return bean;
	}

	/**
	 * カテゴリ一覧取得用フィルタの設定
	 *
	 * @return フィルタ設定情報
	 */
	@Bean
	public FilterRegistrationBean<CategoryListMakeFilter> configCategoryListMakeFilter() {
		FilterRegistrationBean<CategoryListMakeFilter> bean = new FilterRegistrationBean<CategoryListMakeFilter>();

		bean.setFilter(new CategoryListMakeFilter());
		bean.setOrder(5);
		return bean;
	}
}
