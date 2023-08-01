package com.udacity.pricing;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {
	private static final String BASE_URL = "http://localhost";

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void getAllPrices() {
		ResponseEntity<String> response =
				restTemplate.getForEntity(BASE_URL + ":" + port + "/prices/", String.class);
		DocumentContext document = JsonPath.parse(response.getBody());

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(document.read("$._embedded.prices[0].currency"), equalTo("USD"));
	}

	@Test
	public void getPriceById() {
		ResponseEntity<String> response =
				restTemplate.getForEntity(BASE_URL + ":" + port + "/prices/1", String.class);
		DocumentContext document = JsonPath.parse(response.getBody());

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(document.read("$.currency"), equalTo("USD"));
	}

}
