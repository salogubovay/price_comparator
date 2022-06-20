package ru.yandex.price_comparator.repository;

import ru.yandex.price_comparator.domain.ShopUnit;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopUnitRepository extends JpaRepository<ShopUnit, String> {

	List<ShopUnit> findByParentId(String parentId);
	
	@Query(
			value = "SELECT ID FROM SHOP_UNITS WHERE TYPE = ?1", 
			nativeQuery = true)
	Set<String> getSetOfIdsByType(Integer type);
	
}
