package ru.yandex.price_comparator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.price_comparator.domain.ShopUnit;
import ru.yandex.price_comparator.domain.ShopUnitImport;
import ru.yandex.price_comparator.domain.ShopUnitImportRequest;
import ru.yandex.price_comparator.repository.ShopUnitRepository;
import ru.yandex.price_comparator.validation.RequestValidator;

@RestController
@Validated
public class ShopUnitController {
	@Autowired
	private ShopUnitRepository shopUnitRepository;
	@Autowired
	private RequestValidator requestValidator;
	
	@RequestMapping(value="/imports", method=RequestMethod.POST)
	public ResponseEntity<?> importShopUnits(@RequestBody ShopUnitImportRequest requestBody) {
		requestValidator.validatePostRequestBody(requestBody);
//		List<ShopUnit> shopUnits = shopUnitRepository.findByParentId("d515e43f-f3f6-4471-bb77-6b455017a2d2");
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			ShopUnit shopUnit = createShopUnit(shopUnitImport, requestBody.getUpdateDate());
			requestValidator.validateShopUnit(shopUnit);
 			shopUnit = shopUnitRepository.save(shopUnit); 
		}

		return new ResponseEntity(null,HttpStatus.OK);
	}
	
	private ShopUnit createShopUnit(ShopUnitImport shopUnitImport, String date) {
		return new ShopUnit(shopUnitImport.getType(), shopUnitImport.getId(), shopUnitImport.getName(), date,
				shopUnitImport.getPrice(), shopUnitImport.getParentId());
	}
	

}
