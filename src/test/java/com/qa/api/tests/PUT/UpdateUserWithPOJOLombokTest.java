package com.qa.api.tests.PUT;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import com.qa.api.data.Users;

public class UpdateUserWithPOJOLombokTest {

	// 1. POST - user id = 123

	// 2. PUT - user id = 123

	// 3. GET - user id = 123

	Playwright playwright;
	APIRequest request;
	APIRequestContext requestContext;

	static String emailId;

	@BeforeTest
	public void setup() {

		playwright = Playwright.create();
		request = playwright.request();
		requestContext = request.newContext();
	}

	@AfterTest
	public void tearDown() {

		playwright.close();

	}

	public static String getRandomEmail() {

		emailId = "testerQA" + System.currentTimeMillis() + "@gmail.com";
		return emailId;

	}

	@Test
	public void createUserTest() throws IOException {
	
		// create users Object: using builder pattern:

		Users users = Users.builder()
				.name("Anes Automation QA")
				.email(getRandomEmail())
				.gender("male")
				.status("active")
				.build();

		//1. POST Call - create a user
		APIResponse apiPostResponse = requestContext.post("https://gorest.co.in/public/v2/users",
				RequestOptions.create().setHeader("Content-Type", "application/json")
						.setHeader("Authorization",
								"Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")
						.setData(users));

		System.out.println(apiPostResponse.status());
		Assert.assertEquals(apiPostResponse.status(), 201);
		Assert.assertEquals(apiPostResponse.statusText(), "Created");

		String responseText = apiPostResponse.text();
		System.out.println(responseText);

		// convert response text/json to POJO -- deserialization
		ObjectMapper objectMapper = new ObjectMapper();
		Users actualUser = objectMapper.readValue(responseText, Users.class);

		System.out.println("actual user from the response ------>");
		System.out.println(actualUser);

		Assert.assertEquals(actualUser.getName(), users.getName());
		Assert.assertEquals(actualUser.getEmail(), users.getEmail());
		Assert.assertEquals(actualUser.getStatus(), users.getStatus());
		Assert.assertEquals(actualUser.getGender(), users.getGender());
		Assert.assertNotNull(actualUser.getId());
		
		String userId = actualUser.getId();
		System.out.println("new user ID is: " + userId);
		
		//update status active to inactive
		users.setStatus("inactive");
		users.setName("Anes Automation Playwright");
		
		System.out.println("-----------------< PUT CALL >--------------------------");
		
		//2. PUT Call - update a user
				APIResponse apiPutResponse = requestContext.put("https://gorest.co.in/public/v2/users/" +  userId,
						RequestOptions.create().setHeader("Content-Type", "application/json")
								.setHeader("Authorization",
										"Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")
								.setData(users));
		
		System.out.println(apiPutResponse.status() + " : " + apiPutResponse.statusText());
		Assert.assertEquals(apiPutResponse.status(), 200);
		
		String putResponseText = apiPutResponse.text();
		System.out.println("update user: " + putResponseText);
		
		Users actualPutUser = objectMapper.readValue(putResponseText, Users.class);
		Assert.assertEquals(actualPutUser.getId(), userId);
		Assert.assertEquals(actualPutUser.getStatus(), users.getStatus());
		Assert.assertEquals(actualPutUser.getName(), users.getName());
		
		System.out.println("-----------------< GET CALL >--------------------------");
		
		//3. Get the updated user with GET Call
		
		APIResponse apiGETResponse = requestContext.get("https://gorest.co.in/public/v2/users/" + userId, 
				RequestOptions.create().setHeader("Authorization",
						"Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")
				);
		
		System.out.println(apiGETResponse.url());

		int statusCode = apiGETResponse.status();
		System.out.println("response status code : " + statusCode);

		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(apiGETResponse.ok(), true);

		String statusGETStatusText = apiGETResponse.statusText();
		System.out.println(statusGETStatusText);
		
		String getResponseText = apiGETResponse.text();
		
		Users actualGETUser = objectMapper.readValue(getResponseText, Users.class);
		Assert.assertEquals(actualGETUser.getId(), userId);
		Assert.assertEquals(actualGETUser.getStatus(), users.getStatus());
		Assert.assertEquals(actualGETUser .getName(), users.getName());

	}
}
