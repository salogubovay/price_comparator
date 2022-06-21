package ru.yandex.price_comparator.repository;

import ru.yandex.price_comparator.domain.ShopUnit;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ShopUnitRepository extends JpaRepository<ShopUnit, String> {

	List<ShopUnit> findByParentId(String parentId);
	
	/**
	 * Метод возвращает либо все категории, либо все товары из базы данных
	 * @param type
	 * @return
	 */
	@Query(
			value = "SELECT ID FROM SHOP_UNITS WHERE TYPE = ?1", 
			nativeQuery = true)
	Set<String> getSetOfIdsByType(Integer type);
	
	
	/**
	 * Рекурсивный запрос для получения всех нижестоящих элементов определённого типа (для узла с id из параметра)
	 * @param id
	 * @param type
	 * @return
	 */
	@Query(value = "WITH RECURSIVE result (id, parent_id, type) AS\r\n"
			+ "        (SELECT id, parent_id, type \r\n"
			+ "        FROM SHOP_UNITS\r\n"
			+ "        WHERE parent_id = ?1\r\n"
			+ "        UNION ALL\r\n"
			+ "        SELECT tree.id, tree.parent_id, tree.type \r\n"
			+ "        FROM result INNER JOIN SHOP_UNITS tree\r\n"
			+ "        ON result.id = tree.parent_id"
			+ "        )\r\n"
			+ "        SELECT id FROM result"
			+ "        WHERE type = ?2", 
			nativeQuery = true)
	List<String> getAllChildrenByType(String id, Integer type);
	
	
	/**
	 * Рекурсивный запрос для получения всех нижестоящих элементов всех типов (для узла с id из параметра)
	 * используется для удаления категории и всех её элементов
	 * @param id
	 * @return
	 */
	@Query(value = "WITH RECURSIVE result (id, parent_id, type) AS\r\n"
			+ "        (SELECT id, parent_id, type \r\n"
			+ "        FROM SHOP_UNITS\r\n"
			+ "        WHERE parent_id = ?1\r\n"
			+ "        UNION ALL\r\n"
			+ "        -- Recursive Subquery\r\n"
			+ "        SELECT tree.id, tree.parent_id, tree.type \r\n"
			+ "        FROM result INNER JOIN SHOP_UNITS tree\r\n"
			+ "        ON result.id = tree.parent_id"
			+ "        )\r\n"
			+ "        SELECT id FROM result", 
			nativeQuery = true)
	List<String> getAllChildren(String id);
	
	
	/**
	 * Рекурсивый запрос для получения всех родительских элементов определённого типа (значение ordinal() типа ShopUnitType)
	 * Запром в том числе возвращает сам элемент (если его тип совпадает с типом, указанным в параметре) - 
	 * - это используется для обновления цен после удаления элемента (request: /delete/{id})
	 * @param id
	 * @param type
	 * @return
	 */
	@Query(value = "WITH RECURSIVE result (id, parent_id, type) AS\r\n"
			+ "        (SELECT id, parent_id, type \r\n"
			+ "        FROM SHOP_UNITS\r\n"
			+ "        WHERE id = ?1\r\n"
			+ "        UNION ALL\r\n"
			+ "        SELECT tree.id, tree.parent_id, tree.type \r\n"
			+ "        FROM result INNER JOIN SHOP_UNITS tree\r\n"
			+ "        ON result.parent_id = tree.id"
			+ "        )\r\n"
			+ "        SELECT id FROM result"
			+ "        WHERE type = ?2", 
			nativeQuery = true)
	List<String> getAllParentByType(String id, Integer type);
	  
}
