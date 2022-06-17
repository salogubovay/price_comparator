package ru.yandex.price_comparator.validation;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import ru.yandex.price_comparator.domain.ShopUnit;
import ru.yandex.price_comparator.domain.ShopUnitImport;
import ru.yandex.price_comparator.domain.ShopUnitImportRequest;
import ru.yandex.price_comparator.domain.ShopUnitType;
import ru.yandex.price_comparator.exception.ValidationException;

@Component
public class RequestValidator {
	public void validateDateFormat(String date) throws ValidationException{
		try {
			DateTimeFormatter.ISO_DATE_TIME.parse(date);
		} catch (DateTimeParseException e) {
			throw new ValidationException();
		}
	}
	
	public void validateShopUnit(ShopUnit shopUnit) throws ValidationException { 
		if (shopUnit.getType() == null || shopUnit.getId() == null || shopUnit.getName() == null) {
			throw new ValidationException();
		}
		
		if (shopUnit.getType() == ShopUnitType.CATEGORY && shopUnit.getPrice() != null) {
			throw new ValidationException();
		}
		
		if (shopUnit.getType() == ShopUnitType.OFFER && (shopUnit.getPrice() == null || shopUnit.getPrice() < 0)) {
			throw new ValidationException();
		}
	}
	
	public void validatePostRequestBody(ShopUnitImportRequest requestBody) {
		validateDateFormat(requestBody.getUpdateDate());
		checkDuplicatedIds(requestBody.getItems());
		if (requestBody.getItems() == null || requestBody.getUpdateDate() == null) {
			throw new ValidationException();
		}
	}
	
	public void checkDuplicatedIds(ShopUnitImport[] items) {
		Set<String> ids = new HashSet<>();
		for (ShopUnitImport shopUnitImport : items) {
			String id = shopUnitImport.getId();
			if (!ids.contains(id)) {
				ids.add(id);
			} else {
				throw new ValidationException();
			}
		}
	}
}
