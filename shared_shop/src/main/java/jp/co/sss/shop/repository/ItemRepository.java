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

	//注文確定商品を在庫数に反映
	@Transactional
	@Modifying
	@Query("UPDATE Item i SET i.stock = :quantityInBasket WHERE i.id = :id")
	public Integer updateStockById(@Param("quantityInBasket") Integer stock, @Param("id") Integer id);

	// 商品情報を新着順で検索
	public Page<Item> findByDeleteFlagOrderByInsertDateDescIdAsc(int deleteFlag, Pageable pageable);

	//カテゴリー検索
	public Page<Item> findByDeleteFlagAndCategory(int deleteFlag, Category category, Pageable pageable);

	//商品名曖昧検索
	public Page<Item> findByDeleteFlagAndNameLike(int deleteFlag, String name, Pageable pageable);

	//価格の低い順
	public Page<Item> findByDeleteFlagOrderByPriceAscIdAsc(int deleteFlag, Pageable pageable);

	//価格の高い順
	public Page<Item> findByDeleteFlagOrderByPriceDescIdAsc(int deleteFlag, Pageable pageable);

	//売れ筋順の表示

	/*
	 *SELECT items.id, items.name, items.image, items.price, items.stock, SUM(order_items.quantity)
	FROM items
	INNER JOIN categories
	ON items.category_id = categories.id
	INNER JOIN order_items
	ON items.id = order_items.item_id
	WHERE categories.id = items.category_id
	GROUP BY items.id, items.name, items.image, items.price, items.stock,
	order_items.quantity
	ORDER BY SUM(order_items.quantity) DESC;
	 */


	//売れ筋順(トップ画面用)
	@Query("SELECT new Item(i.id, i.name, i.price, i.description, i.image, c.name) "
			+ "FROM Item i INNER JOIN i.category c INNER JOIN i.orderItemList o "
			+ "GROUP BY i.id, i.name, i.price, i.description, i.image, c.name, o.quantity "
			+ "ORDER BY SUM(o.quantity) DESC")
	public Page<Item> findBySaleItemsQuery(Pageable pageable);


	@Query("SELECT new Item(i.id, i.name, i.price, i.description, i.image, c.name) "
			+ "FROM Item i INNER JOIN i.category c INNER JOIN i.orderItemList o "
			+ "WHERE c.id = :categoryId "
			+ "GROUP BY i.id, i.name, i.price, i.description, i.image, c.name, o.quantity "
			+ "ORDER BY SUM(o.quantity) DESC")
public Page<Item> findBySumQuantityQuery(@Param("categoryId") Integer categoryId,
			Pageable pageable);


	/**SELECT items.id, items.name, items.image, items.price, items.stock
	FROM items
	INNER JOIN categories
	ON items.category_id = categories.id
	INNER JOIN order_items
	ON items.id = order_items.item_id
	WHERE categories.id = items.category_id
	GROUP BY items.id, items.name, items.image, items.price, items.stock,
	order_items.quantity
	ORDER BY items.price ASC; */

	public List<Item> findByCategoryId(Integer tergetCategoryid);
}
