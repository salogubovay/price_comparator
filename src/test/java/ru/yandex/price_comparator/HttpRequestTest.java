package ru.yandex.price_comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.skyscreamer.jsonassert.JSONAssert;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class HttpRequestTest {
	@LocalServerPort
	private int port;
	
	private final HttpHeaders headers = new HttpHeaders();

	private final String rootCategory = "{\r\n"
			+ "        \"items\": [\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"CATEGORY\",\r\n"
			+ "                \"name\": \"Товары\",\r\n"
			+ "                \"id\": \"root-category\",\r\n"
			+ "                \"parentId\": null\r\n"
			+ "            }\r\n"
			+ "        ],\r\n"
			+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
			+ "    }";
	
	private final String addingCategoryAndOffers1 = "{\r\n"
			+ "        \"items\": [\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"CATEGORY\",\r\n"
			+ "                \"name\": \"Смартфоны\",\r\n"
			+ "                \"id\": \"category_1_1\",\r\n"
			+ "                \"parentId\": \"root-category\"\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"OFFER\",\r\n"
			+ "                \"name\": \"jPhone 13\",\r\n"
			+ "                \"id\": \"offer_1_1.1\",\r\n"
			+ "                \"parentId\": \"category_1_1\",\r\n"
			+ "                \"price\": 79999\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"OFFER\",\r\n"
			+ "                \"name\": \"Xomiа Readme 10\",\r\n"
			+ "                \"id\": \"offer_1_1.2\",\r\n"
			+ "                \"parentId\": \"category_1_1\",\r\n"
			+ "                \"price\": 59999\r\n"
			+ "            }\r\n"
			+ "        ],\r\n"
			+ "        \"updateDate\": \"2022-02-02T12:00:00.000Z\"\r\n"
			+ "    }";
	
	private final String addingCategoryAndOffers2 = "{\r\n"
			+ "        \"items\": [\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"CATEGORY\",\r\n"
			+ "                \"name\": \"Телевизоры\",\r\n"
			+ "                \"id\": \"category_1_2\",\r\n"
			+ "                \"parentId\": \"root-category\"\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"OFFER\",\r\n"
			+ "                \"name\": \"Samson 70\\\" LED UHD Smart\",\r\n"
			+ "                \"id\": \"offer_1_2.1\",\r\n"
			+ "                \"parentId\": \"category_1_2\",\r\n"
			+ "                \"price\": 32999\r\n"
			+ "            },\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"OFFER\",\r\n"
			+ "                \"name\": \"Phyllis 50\\\" LED UHD Smarter\",\r\n"
			+ "                \"id\": \"offer_1_2.2\",\r\n"
			+ "                \"parentId\": \"category_1_2\",\r\n"
			+ "                \"price\": 49999\r\n"
			+ "            }\r\n"
			+ "        ],\r\n"
			+ "        \"updateDate\": \"2022-02-03T12:00:00.000Z\"\r\n"
			+ "    }";
	
	private final String addingOffers = "{\r\n"
			+ "        \"items\": [\r\n"
			+ "            {\r\n"
			+ "                \"type\": \"OFFER\",\r\n"
			+ "                \"name\": \"Goldstar 65\\\" LED UHD LOL Very Smart\",\r\n"
			+ "                \"id\": \"offer_1_2.3\",\r\n"
			+ "                \"parentId\": \"category_1_2\",\r\n"
			+ "                \"price\": 69999\r\n"
			+ "            }\r\n"
			+ "        ],\r\n"
			+ "        \"updateDate\": \"2022-02-04T15:00:00.000Z\"\r\n"
			+ "    }";
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@BeforeEach
	public void setHeadersContentType() {
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_OK_whenRequestBodyIsCorrect() {
		HttpEntity<?> httpEntity = new HttpEntity<String>(rootCategory, headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity, String.class);
		assertEquals(200, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyDoesNotContainName() {
		String items = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"id\": \"root-category\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpEntity<?> httpEntity = new HttpEntity<String>(items, headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyContainsDuplicateIds() {
		String items = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Смартфоны\",\r\n"
				+ "                \"id\": \"category-1\",\r\n"
				+ "                \"parentId\": \"null\"\r\n"
				+ "            },\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"OFFER\",\r\n"
				+ "                \"name\": \"jPhone 13\",\r\n"
				+ "                \"id\": \"offer-1\",\r\n"
				+ "                \"parentId\": \"category-1\",\r\n"
				+ "                \"price\": 79999\r\n"
				+ "            },\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"OFFER\",\r\n"
				+ "                \"name\": \"Xomiа Readme 10\",\r\n"
				+ "                \"id\": \"offer-1\",\r\n"
				+ "                \"parentId\": \"category-1\",\r\n"
				+ "                \"price\": 59999\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-02T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpEntity<?> httpEntity = new HttpEntity<String>(items, headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyChangesTypeOfRecordInDb() {
		String changingType = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"OFFER\",\r\n"
				+ "                \"name\": \"Товары\",\r\n"
				+ "                \"id\": \"root-category\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(changingType, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyContainsWrongDateFormat() {
		String items = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Товары\",\r\n"
				+ "                \"id\": \"category-1.0\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-13-01T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(items, headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void getShopUnits_shouldReturnStatusCode_OK_AndCorrectTree_whenSuchNodeExists() throws Exception {		
		String expectedJsonTree = "{\r\n"
				+ "	\"type\": \"CATEGORY\",\r\n"
				+ "	\"id\": \"root-category\",\r\n"
				+ "	\"name\": \"Товары\",\r\n"
				+ "	\"date\": \"2022-02-04T15:00:00.000Z\",\r\n"
				+ "	\"parentId\": null,\r\n"
				+ "	\"price\": 58599,\r\n"
				+ "	\"children\": [\r\n"
				+ "		{\r\n"
				+ "			\"type\": \"CATEGORY\",\r\n"
				+ "			\"id\": \"category_1_2\",\r\n"
				+ "			\"name\": \"Телевизоры\",\r\n"
				+ "			\"date\": \"2022-02-04T15:00:00.000Z\",\r\n"
				+ "			\"parentId\": \"root-category\",\r\n"
				+ "			\"price\": 50999,\r\n"
				+ "			\"children\": [\r\n"
				+ "				{\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"id\": \"offer_1_2.1\",\r\n"
				+ "					\"name\": \"Samson 70\\\" LED UHD Smart\",\r\n"
				+ "					\"date\": \"2022-02-03T12:00:00.000Z\",\r\n"
				+ "					\"parentId\": \"category_1_2\",\r\n"
				+ "					\"price\": 32999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				},\r\n"
				+ "				{\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"id\": \"offer_1_2.2\",\r\n"
				+ "					\"name\": \"Phyllis 50\\\" LED UHD Smarter\",\r\n"
				+ "					\"date\": \"2022-02-03T12:00:00.000Z\",\r\n"
				+ "					\"parentId\": \"category_1_2\",\r\n"
				+ "					\"price\": 49999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				},\r\n"
				+ "				{\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"id\": \"offer_1_2.3\",\r\n"
				+ "					\"name\": \"Goldstar 65\\\" LED UHD LOL Very Smart\",\r\n"
				+ "					\"date\": \"2022-02-04T15:00:00.000Z\",\r\n"
				+ "					\"parentId\": \"category_1_2\",\r\n"
				+ "					\"price\": 69999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				}\r\n"
				+ "			]\r\n"
				+ "		},\r\n"
				+ "		{\r\n"
				+ "			\"type\": \"CATEGORY\",\r\n"
				+ "			\"id\": \"category_1_1\",\r\n"
				+ "			\"name\": \"Смартфоны\",\r\n"
				+ "			\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "			\"parentId\": \"root-category\",\r\n"
				+ "			\"price\": 69999,\r\n"
				+ "			\"children\": [\r\n"
				+ "				{\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"id\": \"offer_1_1.1\",\r\n"
				+ "					\"name\": \"jPhone 13\",\r\n"
				+ "					\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "					\"parentId\": \"category_1_1\",\r\n"
				+ "					\"price\": 79999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				},\r\n"
				+ "				{\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"id\": \"offer_1_1.2\",\r\n"
				+ "					\"name\": \"Xomiа Readme 10\",\r\n"
				+ "					\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "					\"parentId\": \"category_1_1\",\r\n"
				+ "					\"price\": 59999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				}\r\n"
				+ "			]\r\n"
				+ "		}\r\n"
				+ "	]\r\n"
				+ "}"; 
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1, headers);
		HttpEntity<?> httpEntity3 = new HttpEntity<String>(addingCategoryAndOffers2, headers);
		HttpEntity<?> httpEntity4 = new HttpEntity<String>(addingOffers, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity3, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity4, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/nodes/root-category", HttpMethod.GET, null, String.class);
		String actualJsonTree = (String) responseEntity.getBody();
		assertEquals(200, responseEntity.getStatusCode().value());
		JSONAssert.assertEquals(expectedJsonTree, actualJsonTree, false);
	}
	
	@Test
	public void deleteShopUnit_shouldReturnStatusCode_NOT_FOUND_whenSuchShopUnitDoesNotExist() {
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/delete/item-does-not-exist", HttpMethod.DELETE, null, String.class);
		assertEquals(404, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void deleteShopUnit_shouldReturnStatusCode_OK_whenShopUnitIsSuccessfullyDeleted() {
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/delete/root-category", HttpMethod.DELETE, null, String.class);
		assertEquals(200, responseEntity.getStatusCode().value());
		//Проверка того, что нижестоящие элементы тоже удалились
		responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/delete/category_1_1", HttpMethod.DELETE, null, String.class);
		assertEquals(404, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void deleteShopUnit_shouldUpdatePriceOfTheParentCategories_whenSuchUnitExist() throws Exception {
		String addingCategoryWithOneChild = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"television category with one child\",\r\n"
				+ "                \"id\": \"category-with-one-child\",\r\n"
				+ "                \"parentId\": \"root-category\"\r\n"
				+ "            },\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"OFFER\",\r\n"
				+ "                \"name\": \"one television child\",\r\n"
				+ "                \"id\": \"one-television-child\",\r\n"
				+ "                \"parentId\": \"category-with-one-child\",\r\n"
				+ "                \"price\": 29999\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-03T16:00:00.000Z\"\r\n"
				+ "    }";
		String expectedJsonTree = "{\r\n"
				+ "	\"name\": \"Товары\",\r\n"
				+ "	\"id\": \"root-category\",\r\n"
				+ "	\"date\": \"2022-02-03T16:00:00.000Z\",\r\n"
				+ "	\"type\": \"CATEGORY\",\r\n"
				+ "	\"parentId\": null,\r\n"
				+ "	\"price\": 69999,\r\n"
				+ "	\"children\": [\r\n"
				+ "		{	\r\n"
				+ "			\"name\": \"Смартфоны\",\r\n"
				+ "			\"id\": \"category_1_1\",\r\n"
				+ "			\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "			\"type\": \"CATEGORY\",\r\n"
				+ "			\"parentId\": \"root-category\",\r\n"
				+ "			\"price\": 69999,\r\n"
				+ "			\"children\": [\r\n"
				+ "				{\r\n"
				+ "					\"name\": \"jPhone 13\",\r\n"
				+ "					\"id\": \"offer_1_1.1\",\r\n"
				+ "					\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"parentId\": \"category_1_1\",\r\n"
				+ "					\"price\": 79999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				},\r\n"
				+ "				{\r\n"
				+ "					\"name\": \"Xomiа Readme 10\",\r\n"
				+ "					\"id\": \"offer_1_1.2\",\r\n"
				+ "					\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"parentId\": \"category_1_1\",\r\n"
				+ "					\"price\": 59999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				}\r\n"
				+ "			]\r\n"
				+ "		},\r\n"
				+ "		{\r\n"
				+ "			\"name\": \"television category with one child\",\r\n"
				+ "			\"id\": \"category-with-one-child\",\r\n"
				+ "			\"date\": \"2022-02-03T16:00:00.000Z\",\r\n"
				+ "			\"type\": \"CATEGORY\",\r\n"
				+ "			\"parentId\": \"root-category\",\r\n"
				+ "			\"price\": null,\r\n"
				+ "			\"children\": null\r\n"
				+ "		}\r\n"
				+ "	]\r\n"
				+ "}";
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1, headers);
		HttpEntity<?> httpEntity3 = new HttpEntity<String>(addingCategoryWithOneChild, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity3, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/delete/one-television-child", HttpMethod.DELETE, null, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/nodes/root-category", HttpMethod.GET, null, String.class);
		String actualJsonTree = (String) responseEntity.getBody();
		JSONAssert.assertEquals(expectedJsonTree, actualJsonTree, false);
	}
	
	@Test
	public void importShopUnits_shouldRetainParentId_whenCategoryIsUpdated() throws Exception {
		String expectedJsonTree = "{\r\n"
				+ "	\"id\": \"root-category\",\r\n"
				+ "	\"name\": \"Товары\",\r\n"
				+ "	\"date\": \"2022-02-01T12:00:00.000Z\",\r\n"
				+ "	\"type\": \"CATEGORY\",\r\n"
				+ "	\"parentId\": null,\r\n"
				+ "	\"price\": 69999,\r\n"
				+ "	\"children\": [\r\n"
				+ "		{\r\n"
				+ "			\"id\": \"category_1_1\",\r\n"
				+ "			\"name\": \"Смартфоны\",\r\n"
				+ "			\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "			\"type\": \"CATEGORY\",\r\n"
				+ "			\"parentId\": \"root-category\",\r\n"
				+ "			\"price\": 69999,\r\n"
				+ "			\"children\": [\r\n"
				+ "				{\r\n"
				+ "					\"id\": \"offer_1_1.1\",\r\n"
				+ "					\"name\": \"jPhone 13\",\r\n"
				+ "					\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"parentId\": \"category_1_1\",\r\n"
				+ "					\"price\": 79999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				},\r\n"
				+ "				{\r\n"
				+ "					\"id\": \"offer_1_1.2\",\r\n"
				+ "					\"name\": \"Xomiа Readme 10\",\r\n"
				+ "					\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "					\"type\": \"OFFER\",\r\n"
				+ "					\"parentId\": \"category_1_1\",\r\n"
				+ "					\"price\": 59999,\r\n"
				+ "					\"children\": null\r\n"
				+ "				}\r\n"
				+ "			]\r\n"
				+ "		}\r\n"
				+ "	]\r\n"
				+ "}";
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/nodes/root-category", HttpMethod.GET, null, String.class);
		String actualJsonTree = (String) responseEntity.getBody();
		JSONAssert.assertEquals(expectedJsonTree, actualJsonTree, false);
	}
	
	@Test
	public void getStatistics_shouldReturnStatusCode_OK_AndArrayOfElements() throws Exception {		
		String expectedJsonTree = "[\r\n"
				+ "	{\r\n"
				+ "		\"name\": \"Xomiа Readme 10\",\r\n"
				+ "		\"id\": \"offer_1_1.2\",\r\n"
				+ "		\"type\": \"OFFER\",\r\n"
				+ "		\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "		\"parentId\": \"category_1_1\",\r\n"
				+ "		\"price\": 59999\r\n"
				+ "	},\r\n"
				+ "	{\r\n"
				+ "		\"name\": \"Samson 70\\\" LED UHD Smart\",\r\n"
				+ "		\"id\": \"offer_1_2.1\",\r\n"
				+ "		\"type\": \"OFFER\",\r\n"
				+ "		\"date\": \"2022-02-03T12:00:00.000Z\",\r\n"
				+ "		\"parentId\": \"category_1_2\",\r\n"
				+ "		\"price\": 32999\r\n"
				+ "	},\r\n"
				+ "	{\r\n"
				+ "		\"name\": \"Phyllis 50\\\" LED UHD Smarter\",\r\n"
				+ "		\"id\": \"offer_1_2.2\",\r\n"
				+ "		\"type\": \"OFFER\",\r\n"
				+ "		\"date\": \"2022-02-03T12:00:00.000Z\",\r\n"
				+ "		\"parentId\": \"category_1_2\",\r\n"
				+ "		\"price\": 49999\r\n"
				+ "	},\r\n"
				+ "	{\r\n"
				+ "		\"name\": \"jPhone 13\",\r\n"
				+ "		\"id\": \"offer_1_1.1\",\r\n"
				+ "		\"type\": \"OFFER\",\r\n"
				+ "		\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "		\"parentId\": \"category_1_1\",\r\n"
				+ "		\"price\": 79999\r\n"
				+ "	}\r\n"
				+ "]";
		String date = "2022-02-03T12:00:00.000Z";
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1, headers);
		HttpEntity<?> httpEntity3 = new HttpEntity<String>(addingCategoryAndOffers2, headers);
		HttpEntity<?> httpEntity4 = new HttpEntity<String>(addingOffers, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity3, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity4, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/sales?date=" + date, HttpMethod.GET, null, String.class);
		String actualJsonTree = (String) responseEntity.getBody();
		assertEquals(200, responseEntity.getStatusCode().value());
		JSONAssert.assertEquals(expectedJsonTree, actualJsonTree, false);
	}
	
	@Test
	public void getStatistics_shouldReturnStatusCode_BAD_REQUEST_whenParameterDateIsAbsent() {
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/sales", HttpMethod.GET, null, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void getStatistics_shouldReturnStatusCode_BAD_REQUEST_whenParameterDateFormatIsWrong() {
		String wrongDateFormat = "2022-02-03T12:00.000Z";
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/sales?date=" + wrongDateFormat, HttpMethod.GET, null, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void getShopUnitStatistics_shouldReturnStatusCode_BAD_REQUEST_whenSomeOfDateParametersIsAbsent() {
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/node/root-category/statistic", HttpMethod.GET, null, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void getShopUnitStatistics_shouldReturnStatusCode_BAD_REQUEST_whenSomeOfDateParametersHasWrongFormat() {
		String wrongDateStartFormat = "2022-02-03T12:00.000Z";
		String dateEnd = "2022-02-03T12:00:30.000Z";
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/node/root-category/statistic?dateStart=" + wrongDateStartFormat + "&dateEnd = " + dateEnd, HttpMethod.GET, null, String.class);
		assertEquals(400, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void getShopUnitStatistics_shouldReturnStatusCode_NOT_FOUND_whenShopUnitDoesNotExist() {
		String dateStart = "2022-02-03T12:00:00.000Z";
		String dateEnd = "2022-02-03T12:00:30.000Z";
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/node/item-does-not-exist/statistic?dateStart=" + dateStart + "&dateEnd=" + dateEnd, HttpMethod.GET, null, String.class);
		assertEquals(404, responseEntity.getStatusCode().value());
	}
	
	@Test
	public void getShopUnitStatistics_shouldReturnStatusCode_OK_AndArrayOfElements() throws Exception {	
		String expectedJsonTree = "[\r\n"
				+ "	{\r\n"
				+ "		\"id\": \"root-category\",\r\n"
				+ "		\"date\": \"2022-02-03T12:00:00.000Z\",\r\n"
				+ "		\"name\": \"Товары\",\r\n"
				+ "		\"type\": \"CATEGORY\",\r\n"
				+ "		\"parentId\": null,\r\n"
				+ "		\"price\": 55749\r\n"
				+ "	},\r\n"
				+ "	{\r\n"
				+ "		\"id\": \"root-category\",\r\n"
				+ "		\"date\": \"2022-02-01T12:00:00.000Z\",\r\n"
				+ "		\"name\": \"Товары\",\r\n"
				+ "		\"type\": \"CATEGORY\",\r\n"
				+ "		\"parentId\": null,\r\n"
				+ "		\"price\": null\r\n"
				+ "	},\r\n"
				+ "	{\r\n"
				+ "		\"id\": \"root-category\",\r\n"
				+ "		\"date\": \"2022-02-02T12:00:00.000Z\",\r\n"
				+ "		\"name\": \"Товары\",\r\n"
				+ "		\"type\": \"CATEGORY\",\r\n"
				+ "		\"parentId\": null,\r\n"
				+ "		\"price\": 69999\r\n"
				+ "	}\r\n"
				+ "]";
		String dateStart = "2022-02-01T12:00:00.000Z";
		String dateEnd = "2022-02-04T15:00:00.000Z";
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(rootCategory, headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1, headers);
		HttpEntity<?> httpEntity3 = new HttpEntity<String>(addingCategoryAndOffers2, headers);
		HttpEntity<?> httpEntity4 = new HttpEntity<String>(addingOffers, headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity3, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity4, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/node/root-category/statistic?dateStart=" + dateStart + "&dateEnd=" + dateEnd, HttpMethod.GET, null, String.class);
		String actualJsonTree = (String) responseEntity.getBody();
		System.out.println(actualJsonTree);
		assertEquals(200, responseEntity.getStatusCode().value());
		JSONAssert.assertEquals(expectedJsonTree, actualJsonTree, false);
	}
}
