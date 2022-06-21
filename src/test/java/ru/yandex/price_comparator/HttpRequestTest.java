package ru.yandex.price_comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.skyscreamer.jsonassert.JSONAssert;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class HttpRequestTest {
	@LocalServerPort
	private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_whenRequestBodyIsCorrect() throws Exception {
		String addingCategory = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Товары\",\r\n"
				+ "                \"id\": \"069cb8d7-bbdd-47d3-ad8f-82ef4c269df1\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> httpEntity = new HttpEntity<String>(addingCategory,headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity, String.class);
		assertEquals(responseEntity.getStatusCode().value(), 200);
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyDoesNotContainName() throws Exception {
		String addingCategory = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"id\": \"069cb8d7-bbdd-47d3-ad8f-82ef4c269df1\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> httpEntity = new HttpEntity<String>(addingCategory,headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity, String.class);
		assertEquals(responseEntity.getStatusCode().value(), 400);
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyContainsDuplicateIds() throws Exception {
		String addingCategory = "{\r\n"
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
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> httpEntity = new HttpEntity<String>(addingCategory,headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity, String.class);
		assertEquals(responseEntity.getStatusCode().value(), 400);
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyChangesTypeOfRecordInDb() throws Exception {
		String addingCategory = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Товары\",\r\n"
				+ "                \"id\": \"category-1.0\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		String changingType = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"OFFER\",\r\n"
				+ "                \"name\": \"Товары\",\r\n"
				+ "                \"id\": \"category-1.0\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(addingCategory,headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(changingType,headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		assertEquals(responseEntity.getStatusCode().value(), 400);
		assertEquals(responseEntity.getStatusCode().value(), 400);
	}
	
	@Test
	public void importShopUnits_shouldReturnStatusCode_BAD_REQUEST_whenRequestBodyContainsWrongDateFormat() throws Exception {
		String addingCategory = "{\r\n"
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
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(addingCategory,headers);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		assertEquals(responseEntity.getStatusCode().value(), 400);
	}
	
	@Test
	public void getShopUnits_shouldReturnStatusCodeOKAndCorrectTree() throws Exception {
		String addingCategory = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Товары\",\r\n"
				+ "                \"id\": \"category_1\",\r\n"
				+ "                \"parentId\": null\r\n"
				+ "            }\r\n"
				+ "        ],\r\n"
				+ "        \"updateDate\": \"2022-02-01T12:00:00.000Z\"\r\n"
				+ "    }";
		String addingCategoryAndOffers1 = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Смартфоны\",\r\n"
				+ "                \"id\": \"category_1_1\",\r\n"
				+ "                \"parentId\": \"category_1\"\r\n"
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
		String addingCategoryAndOffers2 = "{\r\n"
				+ "        \"items\": [\r\n"
				+ "            {\r\n"
				+ "                \"type\": \"CATEGORY\",\r\n"
				+ "                \"name\": \"Телевизоры\",\r\n"
				+ "                \"id\": \"category_1_2\",\r\n"
				+ "                \"parentId\": \"category_1\"\r\n"
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
				
		String addingOffers = "{\r\n"
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
		
		String expectedJsonTree = "{\r\n"
				+ "	\"type\": \"CATEGORY\",\r\n"
				+ "	\"id\": \"category_1\",\r\n"
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
				+ "			\"parentId\": \"category_1\",\r\n"
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
				+ "			\"parentId\": \"category_1\",\r\n"
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
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<?> httpEntity1 = new HttpEntity<String>(addingCategory,headers);
		HttpEntity<?> httpEntity2 = new HttpEntity<String>(addingCategoryAndOffers1,headers);
		HttpEntity<?> httpEntity3 = new HttpEntity<String>(addingCategoryAndOffers2,headers);
		HttpEntity<?> httpEntity4 = new HttpEntity<String>(addingOffers,headers);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity1, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity2, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity3, String.class);
		this.restTemplate.exchange("http://localhost:" + port + "/imports", HttpMethod.POST, httpEntity4, String.class);
		ResponseEntity<?> responseEntity = this.restTemplate.exchange("http://localhost:" + port + "/nodes/category_1", HttpMethod.GET, null, String.class);
		String actualJsonTree = (String) responseEntity.getBody();
		System.out.println(actualJsonTree);
		JSONAssert.assertEquals(expectedJsonTree, actualJsonTree, false);
	}
}
