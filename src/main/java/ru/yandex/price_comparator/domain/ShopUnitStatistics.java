package ru.yandex.price_comparator.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name="SHOP_UNITS_STATISTICS")
public class ShopUnitStatistics {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private	Long rowNumber;
	
	@Column(name="ID", nullable=false)
	private String id;
	
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

	public ShopUnitStatistics(ShopUnitType type, String uuid, String name, String date, Long price, String parentId) {
		this.type = type;
		this.id = uuid;
		this.name = name;
		this.date = date;
		this.price = price;
		this.parentId = parentId;
	}
	
	public ShopUnitStatistics() {}
	
	public String getId() {
		return id;
	}

	public void setId(String uuid) {
		this.id = uuid;
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
