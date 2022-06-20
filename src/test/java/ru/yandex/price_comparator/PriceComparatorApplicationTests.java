package ru.yandex.price_comparator;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import ru.yandex.price_comparator.controller.ShopUnitController;

@SpringBootTest
class PriceComparatorApplicationTests {
	@Autowired ShopUnitController controller;
	
	@Test
	void contextLoads() throws Exception{
		assertNotNull(controller);
	}

}
