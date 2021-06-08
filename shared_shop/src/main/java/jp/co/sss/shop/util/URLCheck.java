package jp.co.sss.shop.util;

public class URLCheck {
	/**
	 * 静的ファイルのリクエストURLであるかを判定[
	 * 
	 * @param requestURL リクエストURL
	 * @return true：静的ファイルへのリクエストURLである、false：静的ファイルへのリクエストURLではない
	 */
	public static boolean checkURLForStaticFile(String requestURL) {
		if (requestURL.indexOf(Constant.CSS_FOLDER) != -1 || requestURL.indexOf(Constant.IMAGE_FOLDER) != -1) {
			return true;
		} else {
			return false;
		}
	}
}
