package jp.co.sss.shop.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.entity.Item;

/**
 * itemsテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

	/**
	 * 注文確定商品を在庫数に反映させる処理
	 *
	 * @param stock　在庫数
	 * @param id　商品ID
	 */
	@Transactional
	@Modifying
	@Query("UPDATE Item i SET i.stock = :quantityInBasket WHERE i.id = :id")
	public Integer updateStockById(@Param("quantityInBasket") Integer stock, @Param("id") Integer id);

	/**
	 * 商品情報を新着順で検索
	 *
	 * @param deleteFlag　在庫数確認
	 * @param pageable　ページング機能
	 */
	public Page<Item> findByDeleteFlagOrderByInsertDateDescIdAsc(int deleteFlag, Pageable pageable);

	/**
	 * カテゴリー検索
	 *
	 * @param deleteFlag　在庫数確認
	 * @param category　カテゴリー情報
	 * @param pageable　ページング機能
	 */
	public Page<Item> findByDeleteFlagAndCategory(int deleteFlag, Category category, Pageable pageable);

	/**
	 * 商品検索（曖昧検索）
	 *
	 * @param deleteFlag　在庫数確認
	 * @param name　商品名
	 * @param pageable　ページング機能
	 */
	public Page<Item> findByDeleteFlagAndNameLike(int deleteFlag, String name, Pageable pageable);

	/**
	 * 価格の低い順で表示
	 *
	 * @param deleteFlag　在庫数確認
	 * @param pageable　ページング機能
	 */
	public Page<Item> findByDeleteFlagOrderByPriceAscIdAsc(int deleteFlag, Pageable pageable);

	/**
	 * 価格の高い順で表示
	 *
	 * @param deleteFlag　在庫数確認
	 * @param pageable　ページング機能
	 */
	public Page<Item> findByDeleteFlagOrderByPriceDescIdAsc(int deleteFlag, Pageable pageable);

	/**
	 * 売れ筋順で表示
	 *
	 * @param pageable　ページング機能
	 */
	//売れ筋順（トップ画面）
	@Query("SELECT new Item(i.id, i.name, i.price, i.description, i.image, c.name) "
			+ "FROM Item i INNER JOIN i.category c INNER JOIN i.orderItemList o "
			+ "GROUP BY i.id, i.name, i.price, i.description, i.image, c.name, o.quantity "
			+ "ORDER BY SUM(o.quantity) DESC")
	public Page<Item> findBySaleItemsQuery(Pageable pageable);

	/**
	 * 売れ筋順で表示
	 *
	 * @param categoryId　カテゴリーID
	 * @param pageable　ページング機能
	 */
	//売れ筋順（リンク用）
	@Query("SELECT new Item(i.id, i.name, i.price, i.description, i.image, c.name) "
			+ "FROM Item i INNER JOIN i.category c INNER JOIN i.orderItemList o "
			+ "WHERE c.id = :categoryId "
			+ "GROUP BY i.id, i.name, i.price, i.description, i.image, c.name, o.quantity "
			+ "ORDER BY SUM(o.quantity) DESC")
	public Page<Item> findBySumQuantityQuery(@Param("categoryId") Integer categoryId,
			Pageable pageable);

	/**
	 * 関連商品表示
	 *
	 * @param tergetCategoryid　カテゴリーIDで絞り込み
	 */
	public List<Item> findByCategoryId(Integer tergetCategoryid);

	/**
	 * 関連商品の重複対策
	 *
	 * @param id　商品ID
	 * @param categoryId　カテゴリーID
	 */
	public List<Item> findByIdNotAndCategoryId(Integer id, Integer categoryId);
}
