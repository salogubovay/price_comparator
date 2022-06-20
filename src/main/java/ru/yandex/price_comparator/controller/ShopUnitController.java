package ru.yandex.price_comparator.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import ru.yandex.price_comparator.dto.CategoryUnitInfo;
import ru.yandex.price_comparator.exception.ItemNotFoundException;
import ru.yandex.price_comparator.exception.ValidationException;
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
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			ShopUnit shopUnit = createShopUnit(shopUnitImport, requestBody.getUpdateDate());
 			shopUnit = shopUnitRepository.save(shopUnit); 
		}

		return new ResponseEntity(null,HttpStatus.OK);
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
		shopUnitRepository.deleteById(shopUnitId);
		LinkedList<ShopUnit> children = new LinkedList<>();
		List<ShopUnit> shopUnitstoDelete = new LinkedList<>();
		children.addAll(shopUnitRepository.findByParentId(shopUnitId));
		while (children.size() > 0) {
			ShopUnit shopUnit = children.poll();
			shopUnitstoDelete.add(shopUnit);
			if (shopUnit.getType() == ShopUnitType.CATEGORY) {
				children.addAll(shopUnitRepository.findByParentId(shopUnit.getId()));
			}
		}
		shopUnitRepository.deleteAll(shopUnitstoDelete);
		return new ResponseEntity(null,HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/nodes/{shopUnitId}", method=RequestMethod.GET)
	public ResponseEntity<?> getShopUnits(@PathVariable String shopUnitId) {
		Optional<ShopUnit> shopUnit = shopUnitRepository.findById(shopUnitId);
		if (!shopUnit.isPresent()) {
			throw new ItemNotFoundException();
		}
		ShopUnit gettingShopUnit = shopUnit.get();
		if (gettingShopUnit.getType() == ShopUnitType.CATEGORY) {
			assembleShopUnit(gettingShopUnit);
			calculateCategoryPrice(gettingShopUnit);
		}
		return new ResponseEntity(gettingShopUnit,null,HttpStatus.OK);
	}
	
	private void assembleShopUnit(ShopUnit shopUnit) {
		List<ShopUnit> children = shopUnitRepository.findByParentId(shopUnit.getId());
		shopUnit.setChildren(children);
		for (ShopUnit child : children) {
			if (child.getType() == ShopUnitType.CATEGORY) {
				assembleShopUnit(child);
			}
		}
	}
	
	private CategoryUnitInfo calculateCategoryPrice(ShopUnit shopUnit) {
		CategoryUnitInfo currentCategoryInfo = new CategoryUnitInfo();
		int directChildrenOffers = 0;
		long directChildrenTotalPrice = 0;
		for (ShopUnit child : shopUnit.getChildren()) {
			if (child.getType() == ShopUnitType.CATEGORY) {
				CategoryUnitInfo childCategoryInfo = calculateCategoryPrice(child);
				directChildrenOffers += childCategoryInfo.getTotalChildren();
				directChildrenTotalPrice += childCategoryInfo.getTotalPrice();
			} else {
				directChildrenOffers++;
				directChildrenTotalPrice += child.getPrice();
			}
		}
		currentCategoryInfo.setTotalChildren(directChildrenOffers);
		currentCategoryInfo.setTotalPrice(directChildrenTotalPrice);
		shopUnit.setPrice((long) Math.floor(directChildrenTotalPrice * 1.0 / directChildrenOffers));
		return currentCategoryInfo;
	}
}
