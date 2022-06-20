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
	
	public void validateShopUnitImport(ShopUnitImport shopUnitImport) throws ValidationException { 
		if (shopUnitImport.getType() == null || shopUnitImport.getId() == null || shopUnitImport.getName() == null) {
			throw new ValidationException();
		}
		
		if (shopUnitImport.getType() == ShopUnitType.CATEGORY && shopUnitImport.getPrice() != null) {
			throw new ValidationException();
		}
		
		if (shopUnitImport.getType() == ShopUnitType.OFFER && (shopUnitImport.getPrice() == null || shopUnitImport.getPrice() < 0)) {
			throw new ValidationException();
		}
	}
	
	public void validatePostRequestBody(ShopUnitImportRequest requestBody) {
		if (requestBody.getItems() == null || requestBody.getUpdateDate() == null) {
			throw new ValidationException();
		}
		validateDateFormat(requestBody.getUpdateDate());
		checkDuplicatedIds(requestBody.getItems());
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			validateShopUnitImport(shopUnitImport);
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
	
	public void validateTypeChange(ShopUnitImportRequest requestBody, Set<String> categoriesInDb, Set<String> offersInDb) {
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			if (shopUnitImport.getType() == ShopUnitType.CATEGORY && offersInDb.contains(shopUnitImport.getId())) {
				throw new ValidationException();
			}
			
			if (shopUnitImport.getType() == ShopUnitType.OFFER && categoriesInDb.contains(shopUnitImport.getId())) {
				throw new ValidationException();
			}
		}
	}
	
	public void validateParentType(ShopUnitImportRequest requestBody, Set<String> categoriesInDb) {
		Set<String> categoriesInRequest = new HashSet<>();
		
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			if (shopUnitImport.getType() == ShopUnitType.CATEGORY) {
				categoriesInRequest.add(shopUnitImport.getId());
			}
		}
		
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			String parentId = shopUnitImport.getParentId();
			if (parentId != null &&!categoriesInDb.contains(parentId) && !categoriesInRequest.contains(parentId)) {
				throw new ValidationException();
			}
		}
	}
}
