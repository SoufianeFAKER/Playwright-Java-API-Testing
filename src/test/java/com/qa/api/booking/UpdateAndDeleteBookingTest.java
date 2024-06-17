package com.qa.api.booking;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;

public class UpdateAndDeleteBookingTest {
	
	Playwright playwright;
	APIRequest request;
	APIRequestContext requestContext;
	
	private static String TOKEN_ID = null;

	@BeforeTest
	public void setup() throws IOException {

		playwright = Playwright.create();
		request = playwright.request();
		requestContext = request.newContext();
		
		//get the token
		String reqTokenJsonBody = "{\r\n"
				+ "    \"username\" : \"admin\",\r\n"
				+ "    \"password\" : \"password123\"\r\n"
				+ "}";
		
		//POST Call: create a token
		APIResponse apiPostTokenResponse = requestContext.post("https://restful-booker.herokuapp.com/auth",
				RequestOptions.create()
					.setHeader("Content-Type", "application/json")
					.setData(reqTokenJsonBody)					
				);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode postJsonResponse = objectMapper.readTree(apiPostTokenResponse.body());
		
		System.out.println(postJsonResponse.toPrettyString());
		
		//capture token from the post json response
		TOKEN_ID = postJsonResponse.get("token").asText();
		System.out.println("token is: " + TOKEN_ID);
	}

	@AfterTest
	public void tearDown() {

		playwright.close();

	}
	
	@Test
	public void updateBookingTest() {
		
		String bookingJson = "{\r\n"
				+ "    \"firstname\": \"Sally\",\r\n"
				+ "    \"lastname\": \"Smith\",\r\n"
				+ "    \"totalprice\": 333,\r\n"
				+ "    \"depositpaid\": true,\r\n"
				+ "    \"bookingdates\": {\r\n"
				+ "        \"checkin\": \"2024-10-15\",\r\n"
				+ "        \"checkout\": \"2024-09-29\"\r\n"
				+ "    },\r\n"
				+ "    \"additionalneeds\": \"Breakfast\"\r\n"
				+ "}";	
		
		APIResponse apiPutResponse = requestContext.put("https://restful-booker.herokuapp.com/booking/1",
				RequestOptions.create()
					.setHeader("Content-Type", "application/json")
					.setHeader("Cookie", "token=" + TOKEN_ID)
					.setData(bookingJson)					
				);
		
		System.out.println(apiPutResponse.url());
		
		System.out.println(apiPutResponse.status() + " : " + apiPutResponse.statusText());
		Assert.assertEquals(apiPutResponse.status(), 200);
		
		System.out.println(apiPutResponse.text());
		
	}
	
	@Test
	public void deleteBookingTest() {
		
		APIResponse apiDeleteResponse = requestContext.delete("https://restful-booker.herokuapp.com/booking/2",
				RequestOptions.create()
					.setHeader("Content-Type", "application/json")
					.setHeader("Cookie", "token=" + TOKEN_ID)					
				);
		
		System.out.println(apiDeleteResponse.status() + " : " + apiDeleteResponse.statusText());
		Assert.assertEquals(apiDeleteResponse.status(), 201);
		
		System.out.println(apiDeleteResponse.url());
		System.out.println(apiDeleteResponse.text());
		
		Assert.assertTrue(apiDeleteResponse.text().contains("Created"));
		
	}

}
