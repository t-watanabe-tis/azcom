package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	// 商品情報を新着順で検索
	public Page<Item> findByDeleteFlagOrderByInsertDateDesc(int deleteFlag, Pageable pageable);

	public Page<Item> findByDeleteFlagAndCategory(int deleteFlag, Category category, Pageable pageable);

	public Page<Item> findByDeleteFlagAndNameLike(int deleteFlag, String name, Pageable pageable);

	//	List<Item> findByCategory(Category category);
	//
	//	List<Item> findByNameLike(String name);

	List<Item> findByCategoryId(int categoryId);

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

	@Query("SELECT new Item(i.id, i.name, i.price, i.description, i.image, c.name) "
			+ "FROM Item i INNER JOIN i.category c INNER JOIN i.orderItemList o "
			+ "GROUP BY i.id, i.name, i.price, i.description, i.image, c.name, o.quantity "
			+ "ORDER BY SUM(o.quantity) DESC")
	public List<Item> findBySaleItemsQuery();

}
