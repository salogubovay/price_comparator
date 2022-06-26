package ru.yandex.price_comparator.repository;

import ru.yandex.price_comparator.domain.ShopUnitStatistics;
import ru.yandex.price_comparator.domain.ShopUnitStatisticsId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ShopUnitStatisticsRepository extends JpaRepository<ShopUnitStatistics, ShopUnitStatisticsId> {
	/**
	 * Метод удаляет из базы данных все элементы, id которых входят в список, переданный в качестве аргумента
	 * @param ids
	 * @return
	 */
	@Modifying
	@Transactional
	@Query(
			value = "DELETE FROM SHOP_UNITS_STATISTICS WHERE ID IN :ids", 
			nativeQuery = true)
	void deleteAllByUuid(@Param("ids") List<String> ids);
	
	/**
	 * Метод удаляет из базы данных элемент, id которого совпадает с параметром
	 * @param id
	 * @return
	 */
	@Modifying
	@Transactional
	@Query(
			value = "DELETE FROM SHOP_UNITS_STATISTICS WHERE ID = :id", 
			nativeQuery = true)
	void deleteByUuid(@Param("id") String id);
	
	/**
	 * Метод возвращает список id элементов определённого типа, по которым производилось обновление
	 * в периоде [dateStart; dateEnd]
	 * @param dateStart
	 * @param dateEnd
	 * @param type
	 * @return
	 */
	@Query(
			value = "SELECT DISTINCT id FROM SHOP_UNITS_STATISTICS WHERE (date >= :dateStart AND date <= :dateEnd AND type = :unitType)", 
			nativeQuery = true)
	List<String> getShopUnitsUpdatedWithinDatesByType(@Param("dateStart") String dateStart, @Param("dateEnd")String dateEnd, 
													@Param("unitType") int type);
	
	/**
	 * Метод возвращает историю обновления элемента в интервале [dateStart; dateEnd)
	 * @param dateStart
	 * @param dateEnd
	 * @return
	 */
	@Query(
			value = "SELECT * FROM SHOP_UNITS_STATISTICS WHERE (date >= :dateStart AND date < :dateEnd AND id = :id)", 
			nativeQuery = true)
	List<ShopUnitStatistics> getShopUnitsUpdateHistoryWhithinDates(@Param("id") String id, @Param("dateStart") String dateStart, @Param("dateEnd")String dateEnd);
}
