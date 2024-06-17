package com.qa.api.tests.POST;

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
import com.qa.api.data.User;

public class CreateUserWithPOJOTest {
	
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
		
		//create user Object:
		User user = new User("Hiba", getRandomEmail(), "female", "active");
		
		//POST Call: create a user
		APIResponse apiPostResponse = requestContext.post("https://gorest.co.in/public/v2/users",
				RequestOptions.create()
					.setHeader("Content-Type", "application/json")
					.setHeader("Authorization", "Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")
					.setData(user)					
				);
		
		System.out.println(apiPostResponse.status());
		Assert.assertEquals(apiPostResponse.status(), 201);
		Assert.assertEquals(apiPostResponse.statusText(), "Created");
		
		String responseText = apiPostResponse.text();
		System.out.println(responseText);
		
		//convert response text/json to POJO -- deserialization
		ObjectMapper objectMapper = new ObjectMapper();
		User actualUser = objectMapper.readValue(responseText, User.class); 
		
		System.out.println("actual user from the response ------>");
		System.out.println(actualUser);
		
		Assert.assertEquals(actualUser.getName(), user.getName());
		Assert.assertEquals(actualUser.getEmail(), user.getEmail());
		Assert.assertEquals(actualUser.getStatus(), user.getStatus());
		Assert.assertEquals(actualUser.getGender(), user.getGender());
		
		Assert.assertNotNull(actualUser.getId());
		
	}

}
