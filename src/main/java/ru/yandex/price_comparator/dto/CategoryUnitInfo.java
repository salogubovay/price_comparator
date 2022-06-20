package ru.yandex.price_comparator.dto;

public class CategoryUnitInfo {
	private long totalPrice;
	private long totalChildrenOffers;
	
	public Long getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(long totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Long getTotalChildren() {
		return totalChildrenOffers;
	}
	public void setTotalChildren(long totalChildren) {
		this.totalChildrenOffers = totalChildren;
	}
}
