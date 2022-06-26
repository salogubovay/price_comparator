package ru.yandex.price_comparator.controller;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.price_comparator.domain.ShopUnit;
import ru.yandex.price_comparator.domain.ShopUnitStatistics;
import ru.yandex.price_comparator.domain.ShopUnitType;
import ru.yandex.price_comparator.dto.ShopUnitImport;
import ru.yandex.price_comparator.dto.ShopUnitImportRequest;
import ru.yandex.price_comparator.dto.ShopUnitStatisticUnit;
import ru.yandex.price_comparator.exception.ItemNotFoundException;
import ru.yandex.price_comparator.exception.ValidationException;
import ru.yandex.price_comparator.repository.ShopUnitRepository;
import ru.yandex.price_comparator.repository.ShopUnitStatisticsRepository;
import ru.yandex.price_comparator.validation.RequestValidator;

@RestController
public class ShopUnitController {
	@Autowired
	private ShopUnitRepository shopUnitRepository;
	@Autowired
	private ShopUnitStatisticsRepository shopUnitStatisticsRepository;
	@Autowired
	private RequestValidator requestValidator;
	
	@RequestMapping(value="/imports", method=RequestMethod.POST)
	public ResponseEntity<?> importShopUnits(@RequestBody ShopUnitImportRequest requestBody) {
		validateImport(requestBody);
		Set<String> shopUnitIdsInRequest = new HashSet<>();
		List<ShopUnit> shopUnitsToSave = new LinkedList<>();
		List<ShopUnitStatistics> shopUnitsStatisticsToSave = new LinkedList<>();
		for (ShopUnitImport shopUnitImport : requestBody.getItems()) {
			ShopUnit shopUnit = createShopUnit(shopUnitImport, requestBody.getUpdateDate());
			ShopUnitStatistics shopUnitStatistics = createShopUnitStatistics(shopUnitImport, requestBody.getUpdateDate());
			shopUnitsToSave.add(shopUnit);
 			shopUnitIdsInRequest.add(shopUnitImport.getId());
 			//Категории сохранятся при пересчёте средней цены (если сохранить здесь, то будут задвоены записи в таблице SHOP_UNITS_STATISTICS)
 			if (shopUnitStatistics.getType() != ShopUnitType.CATEGORY) {
 				shopUnitsStatisticsToSave.add(shopUnitStatistics);
 			}
		}
		shopUnitRepository.saveAll(shopUnitsToSave);
		shopUnitStatisticsRepository.saveAll(shopUnitsStatisticsToSave);
		updateCategoriesPrices(getCategoriesToUpdate(shopUnitIdsInRequest), requestBody.getUpdateDate());
		return new ResponseEntity(createHttpHeader(), HttpStatus.OK);
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
			shopUnitStatisticsRepository.deleteAllByUuid(childrenShopUnits);
		}
		shopUnitRepository.deleteById(shopUnitId);
		shopUnitStatisticsRepository.deleteByUuid(shopUnitId);
		updateCategoriesPrices(getCategoriesToUpdate(updateUnits), null);
		return new ResponseEntity(createHttpHeader(), HttpStatus.OK);
	}
	
	
	@RequestMapping(value="/nodes/{shopUnitId}", method=RequestMethod.GET)
	public ResponseEntity<?> getShopUnits(@PathVariable String shopUnitId) {
		Optional<ShopUnit> shopUnit = shopUnitRepository.findById(shopUnitId);
		if (!shopUnit.isPresent()) {
			throw new ItemNotFoundException();
		}
		ShopUnit gettingShopUnit = shopUnit.get();
		replaceEmptyChildrenListWithNull(gettingShopUnit);
		return new ResponseEntity(gettingShopUnit,createHttpHeader(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/sales", method=RequestMethod.GET)
	public ResponseEntity<?> getStatistics(@RequestParam (value = "date") Optional<String> requestDate) {
		if (!requestDate.isPresent()) {
			throw new ValidationException();
		}
		requestValidator.validateDateFormat(requestDate.get());
		String dateLow = getTimeStringWithOffset(requestDate.get(), 24);
		List<String> shopUnitsUpdated = shopUnitStatisticsRepository.getShopUnitsUpdatedWithinDatesByType(dateLow, requestDate.get(), ShopUnitType.OFFER.ordinal());
		List<ShopUnitStatisticUnit> shopUnitStatistics = shopUnitRepository.findAllById(shopUnitsUpdated);
		return new ResponseEntity(shopUnitStatistics, createHttpHeader(), HttpStatus.OK);
	}
	
	@RequestMapping(value="/node/{shopUnitId}/statistic", method=RequestMethod.GET)
	public ResponseEntity<?> getShopUnitStatistics(@RequestParam (value = "dateStart") Optional<String> dateStart, 
													@RequestParam (value = "dateEnd") Optional<String> dateEnd, 
													@PathVariable String shopUnitId) {
		if (!dateStart.isPresent() || !dateEnd.isPresent()) {
			throw new ValidationException();
		}
		Optional<ShopUnit> shopUnit = shopUnitRepository.findById(shopUnitId);
		if (!shopUnit.isPresent()) {
			throw new ItemNotFoundException();
		}
		requestValidator.validateDateFormat(dateStart.get());
		requestValidator.validateDateFormat(dateEnd.get());
		List<ShopUnitStatisticUnit> shopUnitStatistics = shopUnitStatisticsRepository.getShopUnitsUpdateHistoryWhithinDates(shopUnitId, dateStart.get(), dateEnd.get());
		return new ResponseEntity(shopUnitStatistics, createHttpHeader(), HttpStatus.OK);
	}
	
	/**
	 * Метод проверяет корректность запроса, загружающего элементы (товары / категории)
	 * @param requestBody
	 */
	private void validateImport(ShopUnitImportRequest requestBody) {
		requestValidator.validatePostRequestBody(requestBody);
		Set<String> categoriesInDb = shopUnitRepository.getSetOfIdsByType(ShopUnitType.CATEGORY.ordinal());
		Set<String> offersInDb = shopUnitRepository.getSetOfIdsByType(ShopUnitType.OFFER.ordinal());
		requestValidator.validateTypeChange(requestBody, categoriesInDb, offersInDb);
		requestValidator.validateParentType(requestBody, categoriesInDb);
	}
	
	private ShopUnit createShopUnit(ShopUnitImport shopUnitImport, String date) {
		Optional<ShopUnit> shopUnitToUpdate = shopUnitRepository.findById(shopUnitImport.getId());
		ShopUnit shopUnit = new ShopUnit(shopUnitImport.getType(), shopUnitImport.getId(), shopUnitImport.getName(), date,
				shopUnitImport.getPrice(), shopUnitImport.getParentId());
		if (shopUnitToUpdate.isPresent()) {
			shopUnit.setChildren(shopUnitToUpdate.get().getChildren()); 
		}
		return shopUnit;
	}
	
	private ShopUnitStatistics createShopUnitStatistics(ShopUnitImport shopUnitImport, String date) {
		return new ShopUnitStatistics(shopUnitImport.getType(), shopUnitImport.getId(), shopUnitImport.getName(), date,
				shopUnitImport.getPrice(), shopUnitImport.getParentId());
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
		List<ShopUnit> categoriesToSave = new LinkedList<>();
		List<ShopUnitStatistics> categoriesStatisticsToSave = new LinkedList<>();
		for (String categoryId : categoriesToUpdate) {
			List<String> children = shopUnitRepository.getAllChildrenByType(categoryId, ShopUnitType.OFFER.ordinal());
			ShopUnit category = shopUnitRepository.findById(categoryId).get();
			if (children.isEmpty()) {
				category.setPrice(null);
			} else {
				recalculateCategoryPrice(category, children);
			}
			if (updateDate != null) {
				category.setDate(updateDate);
			}
			categoriesToSave.add(category);
			categoriesStatisticsToSave.add(new ShopUnitStatistics(category.getType(), category.getId(), category.getName(), category.getDate(),
					category.getPrice(), category.getParentId()));
		}
		shopUnitRepository.saveAll(categoriesToSave);
		shopUnitStatisticsRepository.saveAll(categoriesStatisticsToSave);
	}	
	
	/**
	 * Метод возвращает список id категорий, у которых нужно пересчитать среднюю цену
	 * @param updatedShopUnits
	 * @return
	 */
	private Set<String> getCategoriesToUpdate(Set<String> updatedShopUnits){
		Set<String> categoriesToUpdate = new HashSet<>();
		for (String shopUnitId : updatedShopUnits) {
			List<String> parentCategoris = shopUnitRepository.getAllParentByType(shopUnitId, ShopUnitType.CATEGORY.ordinal());
			categoriesToUpdate.addAll(parentCategoris);
		}
		return categoriesToUpdate;
	}
	
	private void recalculateCategoryPrice(ShopUnit category, List<String> children) {
		Long totalPrice = shopUnitRepository.findAllById(children).stream().map(x -> x.getPrice()).reduce(Long.valueOf(0), Long :: sum);
		Long averagePrice = (long) Math.floor(totalPrice * 1.0 / children.size());
		category.setPrice(averagePrice);
	}
	
	private HttpHeaders createHttpHeader() {
		HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	    return httpHeaders;
	}
	
	
	/**
	 * Метод возвращает строковое представление даты, полученной из даты аргумента (date) минус смещение в часах (hoursOffset)
	 * @param date
	 * @param hoursOffset
	 * @return
	 */
	private String getTimeStringWithOffset(String date, int hoursOffset) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		ZonedDateTime dateHigh = ZonedDateTime.parse(date, formatter);
		return dateHigh.minusHours(hoursOffset).format(formatter).replace("Z", ".000Z");
	}
}
