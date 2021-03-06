package jp.co.sss.shop.bean;

import java.util.Date;

/**
 * 商品情報クラス
 *
 * @author SystemShared
 */
public class ItemBean {

	/**
	 * 商品ID
	 */
	private Integer id;

	/**
	 * 商品名
	 */
	private String name;

	/**
	 * 価格
	 */
	private Integer price;

	/**
	 * 商品説明
	 */
	private String description;

	/**
	 * 在庫数
	 */
	private Integer stock;

	/**
	 * 商品画像ファイル名
	 */
	private String image;

	/**
	 * カテゴリID
	 */
	private Integer categoryId;

	/**
	 * カテゴリ名
	 */
	private String categoryName;

	/**
	 * 登録日
	 */

	private Date InsertDate;

	/**
	 * 買い物かごに入っている情報
	 */
	private Integer quantityInBasket;

	public Integer getQuantityInBasket() {
		return quantityInBasket;
	}

	public void setQuantityInBasket(Integer quantityInBasket) {
		this.quantityInBasket = quantityInBasket;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Date getInsertDate() {
		return InsertDate;
	}

	public void setInsertDate(Date insertDate) {
		InsertDate = insertDate;
	}

	/**
	 * 商品IDと商品名が一致していれば等価
	 *
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj instanceof ItemBean) {
			ItemBean other = (ItemBean) obj;
			return other.getId() == this.getId() && other.getName().equals(this.getName());
		}
		return false;
	}

}
