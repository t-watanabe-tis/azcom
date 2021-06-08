package jp.co.sss.shop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.Category;
import jp.co.sss.shop.util.JPQLConstant;

/**
 * categoriesテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	Category findByNameAndDeleteFlag(String name, int deleteFlag);

	Category findByIdAndDeleteFlag(Integer id, int deleteFlag);

	// カテゴリ情報を登録日付順に取得
	List<Category> findByDeleteFlagOrderByInsertDateDescIdAsc(int deleteFlag);

	// カテゴリ情報を登録日付順に取得
	@Query(JPQLConstant.FIND_ALL_CATEGORIES_ORDER_BY_INSERT_DATE)
	Page<Category> findByDeleteFlagOrderByInsertDateDescPage(
	        @Param(value = "deleteFlag") int deleteFlag, Pageable pageable);

}
