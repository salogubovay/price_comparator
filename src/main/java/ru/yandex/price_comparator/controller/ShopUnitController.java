package ru.yandex.price_comparator.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.price_comparator.domain.ShopUnit;
import ru.yandex.price_comparator.domain.ShopUnitImport;
import ru.yandex.price_comparator.domain.ShopUnitImportRequest;
import ru.yandex.price_comparator.domain.ShopUnitType;
import ru.yandex.price_comparator.exception.ItemNotFoundException;
import ru.yandex.price_comparator.repository.ShopUnitRepository;
import ru.yandex.price_comparator.validation.RequestValidator;

@RestController
public class ShopUnitController {
	@Autowired
	private ShopUnitRepository shopUnitRepository;
	@Autowired
	private RequestValidator requestValidator;
	
	@RequestMapping(value="/imports", method=RequestMethod.POST)
	public ResponseEntity<?> importShopUnits(@RequestBody ShopUnitImportRequest requestBody) {
		requestValidator.validatePostRequestBody(requestBody);
		Set<String> categoriesInDb = shopUnitRepository.getSetOfIdsByType(ShopUnitType.CATEGORY.ordinal());
		Set<String> offersInDb = shopUnitRepository.getSetOfIdsByType(ShopUnitType.OFFER.ordinal());
		requestValidator.validateTypeChange(requestBody, categoriesInDb, offersInDb);
		requestValidator.validateParentType(requestBody, categoriesInDb);
		Set<String> shopUnitIdsInRequest = new HashSet<>();
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			ShopUnit shopUnit = createShopUnit(shopUnitImport, requestBody.getUpdateDate());
 			shopUnit = shopUnitRepository.save(shopUnit);
 			shopUnitIdsInRequest.add(shopUnitImport.getId());
		}
		updateCategoriesPrices(getCategoriesToUpdate(shopUnitIdsInRequest), requestBody.getUpdateDate());
		
		return new ResponseEntity(createHttpHeader(),HttpStatus.OK);
	}
	
	private ShopUnit createShopUnit(ShopUnitImport shopUnitImport, String date) {
		return new ShopUnit(shopUnitImport.getType(), shopUnitImport.getId(), shopUnitImport.getName(), date,
				shopUnitImport.getPrice(), shopUnitImport.getParentId());
	}
	
	@RequestMapping(value="/delete/{shopUnitId}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteShopUnits(@PathVariable String shopUnitId) {

		Optional<ShopUnit> shopUnitToDelete = shopUnitRepository.findById(shopUnitId);
		if (!shopUnitToDelete.isPresent()) {
			throw new ItemNotFoundException();
		}
		Set<String> updateUnits = new HashSet<>();
		updateUnits.add(shopUnitToDelete.get().getParentId());
		if (shopUnitToDelete.get().getType() == ShopUnitType.CATEGORY) {
			List<String> childrenShopUnits = shopUnitRepository.getAllChildren(shopUnitId);
			shopUnitRepository.deleteAllById(childrenShopUnits);
		}
		shopUnitRepository.deleteById(shopUnitId);

		updateCategoriesPrices(getCategoriesToUpdate(updateUnits));
		return new ResponseEntity(createHttpHeader(),HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/nodes/{shopUnitId}", method=RequestMethod.GET)
	public ResponseEntity<?> getShopUnits(@PathVariable String shopUnitId) {
		Optional<ShopUnit> shopUnit = shopUnitRepository.findById(shopUnitId);
		if (!shopUnit.isPresent()) {
			throw new ItemNotFoundException();
		}
		ShopUnit gettingShopUnit = shopUnit.get();
		replaceEmptyChildrenListWithNull(gettingShopUnit);
		return new ResponseEntity(gettingShopUnit,createHttpHeader(),HttpStatus.OK);
	}
	
	/**
	 * Проход по дереву shopUnit с заменой пустых массивов дочерних элементов на null
	 * @param shopUnit
	 */
	private void replaceEmptyChildrenListWithNull(ShopUnit shopUnit) {
		List<ShopUnit> children = shopUnit.getChildren();
		if (children.isEmpty()) {
			shopUnit.setChildren(null);
		} else {
			for (ShopUnit child : shopUnit.getChildren()) {
				replaceEmptyChildrenListWithNull(child);
			}
		}
	}
	
	/**
	 * Метод обновляет цену родительских категорий и дату:
	 * 1) если цена не изменилась, то не производится обновления даты
	 * 2) если в узле нет дочерних элементов, то проставляется цена = null
	 * 
	 * @param updatedShopUnits
	 * @param updateDate
	 */
	
	private void updateCategoriesPrices(Set<String> categoriesToUpdate, String updateDate) {
		for (String categoryId : categoriesToUpdate) {
			List<String> children = shopUnitRepository.getAllChildrenByType(categoryId, ShopUnitType.OFFER.ordinal());
			ShopUnit category = shopUnitRepository.findById(categoryId).get();
			if (children.isEmpty()) {
				if (category.getPrice() != null) {
					category.setPrice(null);
					category.setDate(updateDate);
				}
			} else {
				if (recalculateCategoryPrice(category, children)) {
					category.setDate(updateDate);
				}
			}
			shopUnitRepository.save(category);
		}
	}
	
	/**
	 * Метод обновляет цену родительских категорий:
	 * если в узле нет дочерних элементов, то проставляется цена = null
	 * @param categoriesToUpdate
	 */
	private void updateCategoriesPrices(Set<String> categoriesToUpdate) {
		for (String categoryId : categoriesToUpdate) {
			List<String> children = shopUnitRepository.getAllChildrenByType(categoryId, ShopUnitType.OFFER.ordinal());
			ShopUnit category = shopUnitRepository.findById(categoryId).get();
			if (children.isEmpty()) {
				category.setPrice(null);
			} else {
				recalculateCategoryPrice(category, children);
			}
			shopUnitRepository.save(category);
		}
	}
	
	private Set<String> getCategoriesToUpdate(Set<String> updatedShopUnits){
		Set<String> categoriesToUpdate = new HashSet<>();
		for (String shopUnitId : updatedShopUnits) {
			List<String> parentCategoris = shopUnitRepository.getAllParentByType(shopUnitId, ShopUnitType.CATEGORY.ordinal());
			categoriesToUpdate.addAll(parentCategoris);
		}
		return categoriesToUpdate;
	}
	
	private boolean recalculateCategoryPrice(ShopUnit category, List<String> children) {
		Long totalPrice = shopUnitRepository.findAllById(children).stream().map(x -> x.getPrice()).reduce(Long.valueOf(0), Long :: sum);
		Long averagePrice = (long) Math.floor(totalPrice * 1.0 / children.size());
		if (!Objects.equals(category.getPrice(),averagePrice)) {
			category.setPrice(averagePrice);
			return true;
		} else {
			return false;
		}
	}
	
	private HttpHeaders createHttpHeader() {
		HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	    return httpHeaders;
	}
	
}
