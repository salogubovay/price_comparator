package ru.yandex.price_comparator.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import ru.yandex.price_comparator.domain.ShopUnitType;

public class ShopUnitImport {
	@NotNull
	private String id;
	@NotNull
	@NotEmpty
	private String name;
	@NotNull
	private ShopUnitType type;
	private Long price;
	private String parentId;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
