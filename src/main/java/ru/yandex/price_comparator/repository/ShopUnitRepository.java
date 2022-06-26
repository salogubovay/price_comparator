package ru.yandex.price_comparator.repository;

import ru.yandex.price_comparator.domain.ShopUnit;
import ru.yandex.price_comparator.dto.ShopUnitStatisticUnit;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopUnitRepository extends JpaRepository<ShopUnit, String> {

	List<ShopUnit> findByParentId(String parentId);
	
	/**
	 * Метод возвращает либо все категории, либо все товары из базы данных
	 * @param type
	 * @return
	 */
	@Query(
			value = "SELECT ID FROM SHOP_UNITS WHERE TYPE = :type", 
			nativeQuery = true)
	Set<String> getSetOfIdsByType(@Param("type") Integer type);
	
	
	/**
	 * Рекурсивный запрос для получения всех нижестоящих элементов определённого типа (для узла с id из параметра)
	 * @param id
	 * @param type
	 * @return
	 */
	@Query(value = "WITH RECURSIVE result (id, parent_id, type) AS\r\n"
			+ "        (SELECT id, parent_id, type \r\n"
			+ "        FROM SHOP_UNITS\r\n"
			+ "        WHERE parent_id = :id\r\n"
			+ "        UNION ALL\r\n"
			+ "        SELECT tree.id, tree.parent_id, tree.type \r\n"
			+ "        FROM result INNER JOIN SHOP_UNITS tree\r\n"
			+ "        ON result.id = tree.parent_id"
			+ "        )\r\n"
			+ "        SELECT id FROM result"
			+ "        WHERE type = :type", 
			nativeQuery = true)
	List<String> getAllChildrenByType(@Param("id") String id, @Param("type") Integer type);
	
	
	/**
	 * Рекурсивный запрос для получения всех нижестоящих элементов всех типов (для узла с id из параметра)
	 * используется для удаления категории и всех её элементов
	 * @param id
	 * @return
	 */
	@Query(value = "WITH RECURSIVE result (id, parent_id, type) AS\r\n"
			+ "        (SELECT id, parent_id, type \r\n"
			+ "        FROM SHOP_UNITS\r\n"
			+ "        WHERE parent_id = :id\r\n"
			+ "        UNION ALL\r\n"
			+ "        -- Recursive Subquery\r\n"
			+ "        SELECT tree.id, tree.parent_id, tree.type \r\n"
			+ "        FROM result INNER JOIN SHOP_UNITS tree\r\n"
			+ "        ON result.id = tree.parent_id"
			+ "        )\r\n"
			+ "        SELECT id FROM result", 
			nativeQuery = true)
	List<String> getAllChildren(@Param("id") String id);
	
	
	/**
	 * Рекурсивый запрос для получения всех родительских элементов определённого типа (значение ordinal() типа ShopUnitType)
	 * Запроc, в том числе, возвращает сам элемент (если его тип совпадает с типом, указанным в параметре) - 
	 * - это используется для обновления цен после удаления элемента (request: /delete/{id})
	 * @param id
	 * @param type
	 * @return
	 */
	@Query(value = "WITH RECURSIVE result (id, parent_id, type) AS\r\n"
			+ "        (SELECT id, parent_id, type \r\n"
			+ "        FROM SHOP_UNITS\r\n"
			+ "        WHERE id = :id\r\n"
			+ "        UNION ALL\r\n"
			+ "        SELECT tree.id, tree.parent_id, tree.type \r\n"
			+ "        FROM result INNER JOIN SHOP_UNITS tree\r\n"
			+ "        ON result.parent_id = tree.id"
			+ "        )\r\n"
			+ "        SELECT id FROM result"
			+ "        WHERE type = :type", 
			nativeQuery = true)
	List<String> getAllParentByType(@Param("id") String id, @Param("type") Integer type);
	 
	@Query(value = "SELECT type, id, name, date, price, PARENT_ID as parentid FROM SHOP_UNITS WHERE ID IN :ids", 
			nativeQuery = true)
	List<ShopUnitStatisticUnit> findAllById(@Param("ids") List<String> ids);
	
}
