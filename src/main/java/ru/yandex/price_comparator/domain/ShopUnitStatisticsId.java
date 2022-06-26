package ru.yandex.price_comparator.domain;

import java.io.Serializable;

public class ShopUnitStatisticsId implements Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String date;
	
	public ShopUnitStatisticsId(String id, String date) {
		this.id = id;
		this.date = date;
	}
	
	private ShopUnitStatisticsId() {};
	
	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || !(object instanceof ShopUnitStatisticsId)) {
			return false;
		}
		ShopUnitStatisticsId that = (ShopUnitStatisticsId) object;
		return this.id.equals(that.id) && this.date.equals(that.date);
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
		hash = 43 * hash + id.hashCode();
		hash = 43 * hash + date.hashCode();
		return hash;
	}

	public String getId() {
		return id;
	}

	public String getDate() {
		return date;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDate(String date) {
		this.date = date;
	}
}
