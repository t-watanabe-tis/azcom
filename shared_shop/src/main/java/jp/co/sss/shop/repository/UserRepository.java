package jp.co.sss.shop.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jp.co.sss.shop.entity.User;

/**
 * usersテーブル用リポジトリ
 *
 * @author System Shared
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	// メールアドレスに該当する会員情報を検索（メールアドレスのみ）
	User findByEmail(String email);

	// メールアドレスに該当する会員情報を検索
	User findByEmailAndDeleteFlag(String email, int deleteFlag);

	// 削除フラグに合った会員情報をすべて検索
	Page<User> findByDeleteFlagOrderByInsertDateDesc(int deleteFlag, Pageable pageable);

/**
 * システム管理者のユーザー
 */
@Query
 ("SELECT COUNT (u.authority) FROM User u WHERE u.authority=0 AND deleteFlag=0   GROUP BY u.authority ")
 public Long findByUserWithAuthority0();

/**
 * 運用管理者のユーザー
 */
@Query
("SELECT COUNT (u1.authority) FROM User u1 WHERE u1.authority=1 AND deleteFlag=0  GROUP BY u1.authority ")
public Long findByUserWithAuthority1();

/**
 * 一般会員のユーザー
 */
@Query
("SELECT COUNT (u2.authority) FROM User u2 WHERE u2.authority=2 AND deleteFlag=0 GROUP BY u2.authority ")
public Long findByUserWithAuthority2();
}

