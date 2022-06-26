package ru.yandex.price_comparator.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="SHOP_UNITS_STATISTICS")
@IdClass(ShopUnitStatisticsId.class)
public class ShopUnitStatistics {
	@Id
	@Column(name="ID", nullable=false)
	private String id;
	
	@Id
	@Column(name="DATE", nullable=false)
	private String date;
	
	@Column(name="NAME", nullable=false)
	private String name;
	
	@Column(name="TYPE", nullable=false)
	private ShopUnitType type;
	
	@Column(name="PARENT_ID")
	private String parentId;
	
	@Column(name="PRICE")
	private Long price;

	public ShopUnitStatistics(ShopUnitType type, String id, String name, String date, Long price, String parentId) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.date = date;
		this.price = price;
		this.parentId = parentId;
	}
	
	public ShopUnitStatistics() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ShopUnitType getType() {
		return type;
	}

	public void setType(ShopUnitType type) {
		this.type = type;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}
	
	
}
