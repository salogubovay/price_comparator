package ru.yandex.price_comparator.dto;

import ru.yandex.price_comparator.domain.ShopUnitType;

public interface ShopUnitStatisticUnit {
	 ShopUnitType getType();
	 String getId();
	 String getName();
	 String getDate();
	 Long getPrice();
	 String getParentId();
}
