package jp.co.sss.shop.repository;

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
	public Page<Item> findByDeleteFlagOrderByInsertDateDescIdAsc(int deleteFlag, Pageable pageable);

	public Page<Item> findByDeleteFlagAndCategory(int deleteFlag, Category category, Pageable pageable);

	public Page<Item> findByDeleteFlagAndNameLike(int deleteFlag, String name, Pageable pageable);

	@Query("SELECT i FROM Item i WHERE i.deleteFlag = 0 ORDER BY i.insertDate Desc")
	public Page<Item> findByDeleteFlagOrderByInsertDateDesc(Pageable pageable);

	@Query("SELECT i FROM Item i WHERE i.deleteFlag = 0 ORDER BY i.insertDate Desc, i.id Asc")
	public Page<Item> findByDeleteFlagOrderByInsertDateDescIdAsc(Pageable pageable);

	//価格の低い順
	public Page<Item> findByDeleteFlagOrderByPriceAscIdAsc(int deleteFlag, Pageable pageable);

	//価格の高い順
	public Page<Item> findByDeleteFlagOrderByPriceDescIdAsc(int deleteFlag, Pageable pageable);

}
