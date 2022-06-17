package ru.yandex.price_comparator.domain;

import javax.validation.constraints.NotNull;

public class ShopUnitImportRequest {
	@NotNull
	private ShopUnitImport[] items;
	@NotNull
	private String	updateDate;
	
	public ShopUnitImport[] getItems() {
		return items;
	}
	public void setItems(ShopUnitImport[] items) {
		this.items = items;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
}
