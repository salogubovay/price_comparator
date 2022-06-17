package ru.yandex.price_comparator.repository;

import ru.yandex.price_comparator.domain.ShopUnit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopUnitRepository extends JpaRepository<ShopUnit, String> {

	List<ShopUnit> findByParentId(String parentId);
}
