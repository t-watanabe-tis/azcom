package jp.co.sss.shop.util;

/**
 * 独自JPQLを定義するためのクラス
 *
 * @author System Shared
 */
public class JPQLConstant {

	// 注文情報を全件検索(新着順)
	public static final String	FIND_ALL_ORDERS_ORDER_BY_INSERT_DATE
		= "SELECT o FROM Order o ORDER BY o.insertDate DESC";

	// カテゴリ情報を全件検索(新着順)
	public static final String	FIND_ALL_CATEGORIES_ORDER_BY_INSERT_DATE
	 = "SELECT c FROM Category c WHERE c.deleteFlag =:deleteFlag ORDER BY c.insertDate DESC,c.id ASC";
}
