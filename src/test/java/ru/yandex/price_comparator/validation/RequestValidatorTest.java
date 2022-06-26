package ru.yandex.price_comparator.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.yandex.price_comparator.domain.*;
import ru.yandex.price_comparator.dto.ShopUnitImport;
import ru.yandex.price_comparator.dto.ShopUnitImportRequest;
import ru.yandex.price_comparator.exception.ValidationException;

class RequestValidatorTest {
	private RequestValidator testValidator = new RequestValidator();
	
	@Test()
	void validateDateFormat_shouldThrowValidationException_whenDateFormatIsNotISO() {
		String wrongDateFormat = "2022-13-02T12:00:00.000Z";
		assertThrows(ValidationException.class, () -> testValidator.validateDateFormat(wrongDateFormat));
	}
	
	@Test()
	void validateShopUnitImport_shouldThrowValidationException_whenShopUnitImportHasNullRequiredFields() {
		ShopUnitType offerType = ShopUnitType.OFFER;
		String name = "Notebook";
		String id = "1";
		ShopUnitImport shopUnitWithNullType = new ShopUnitImport();
		ShopUnitImport shopUnitWithNullId = new ShopUnitImport();
		ShopUnitImport shopUnitWithNullName = new ShopUnitImport();
		shopUnitWithNullType.setName(name);
		shopUnitWithNullType.setId(id);
		shopUnitWithNullId.setName(name);
		shopUnitWithNullId.setType(offerType);
		shopUnitWithNullName.setId(id);
		shopUnitWithNullName.setType(offerType);
		assertThrows(ValidationException.class, () -> testValidator.validateShopUnitImport(shopUnitWithNullType));
		assertThrows(ValidationException.class, () -> testValidator.validateShopUnitImport(shopUnitWithNullId));
		assertThrows(ValidationException.class, () -> testValidator.validateShopUnitImport(shopUnitWithNullName));
	}
	
	@Test
	void validatePostRequestBody_shouldThrowValidationException_whenShopUnitImportRequestHasNullRequiredFields() {
		String date = "2022-12-02T12:00:00.000Z";
		ShopUnitImportRequest requestWithNullItems = new ShopUnitImportRequest();
		ShopUnitImportRequest requestWithNullDate = new ShopUnitImportRequest();
		requestWithNullItems.setUpdateDate(date);
		requestWithNullDate.setItems(new ShopUnitImport[] {new ShopUnitImport()});
		assertThrows(ValidationException.class, () -> testValidator.validatePostRequestBody(requestWithNullItems));
		assertThrows(ValidationException.class, () -> testValidator.validatePostRequestBody(requestWithNullDate));
	}
	
	@Test
	void checkDuplicatedIds_shouldThrowValidationException_whenShopImportRequestContainsItemsWithDuplicateIds() {
		ShopUnitImport item1 = new ShopUnitImport();
		ShopUnitImport item2 = new ShopUnitImport();
		String id = "id-1";
		item1.setId(id);
		item2.setId(id);
		ShopUnitImport[] items = new ShopUnitImport[] {item1, item2};
		assertThrows(ValidationException.class, () -> testValidator.checkDuplicatedIds(items));
	}
	
	@Test
	void validateParentType_shouldThrowValidationException_whenShopImportRequestContainsShopUnitsWithOffersIdAsParentId() {
		ShopUnitImportRequest request = new ShopUnitImportRequest();
		ShopUnitImport category = new ShopUnitImport();
		ShopUnitImport offer1 = new ShopUnitImport();
		ShopUnitImport offer2 = new ShopUnitImport();
		category.setId("category-1");
		category.setType(ShopUnitType.CATEGORY);
		offer1.setId("offer-1");
		offer1.setType(ShopUnitType.OFFER);
		offer1.setParentId("category-1");
		offer2.setId("offer-2");
		offer2.setType(ShopUnitType.OFFER);
		offer2.setParentId("offer-1");
		ShopUnitImport[] items = new ShopUnitImport[] {category, offer1, offer2};
		request.setItems(items);
		assertThrows(ValidationException.class, () -> testValidator.validateParentType(request,new HashSet<String>()));
	}
	
	@Test
	void validateTypeChange_shouldThrowValidationException_whenShopImportRequestContainsShopUnitsWhichChangesShopUnitsTypesInDb() {
		Set<String> categoriesInDb = new HashSet<>();
		Set<String> offersInDb = new HashSet<>();
		categoriesInDb.add("category-1");
		offersInDb.add("offer-1");
		ShopUnitImportRequest requestWithCategoryType = new ShopUnitImportRequest();
		ShopUnitImportRequest requestWithOfferType = new ShopUnitImportRequest();
		ShopUnitImport category = new ShopUnitImport();
		ShopUnitImport offer1 = new ShopUnitImport();
		category.setId("category-1");
		category.setType(ShopUnitType.OFFER);
		offer1.setId("offer-1");
		offer1.setType(ShopUnitType.CATEGORY);
		offer1.setParentId("category-1");
		requestWithCategoryType.setItems(new ShopUnitImport[] {offer1});
		requestWithOfferType.setItems(new ShopUnitImport[] {category});
		assertThrows(ValidationException.class, () -> testValidator.validateTypeChange(requestWithCategoryType, categoriesInDb, offersInDb));
		assertThrows(ValidationException.class, () -> testValidator.validateTypeChange(requestWithOfferType, categoriesInDb, offersInDb));
	}

}
