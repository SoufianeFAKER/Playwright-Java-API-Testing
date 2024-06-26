package com.qa.api.tests.DELETE;

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

public class DeleteUserAPITest {
	
	//1. create a user -- user id -- 201
	
	//2. delete user -- user id -- 204
	
	//3. get user -- user id -- 404 (user Not Found)
	
	
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
	public void deleteUserTest() throws IOException {
		
		//1. create users Object: using builder pattern:
		
		Users users = Users.builder()
				.name("Anes Automation")
				.email(getRandomEmail())
				.gender("male")
				.status("active")
				.build();
		
		
		//1. create a user -- user id -- 201
		
		APIResponse apiPostResponse = requestContext.post("https://gorest.co.in/public/v2/users",
				RequestOptions.create()
					.setHeader("Content-Type", "application/json")
					.setHeader("Authorization", "Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")
					.setData(users)					
				);
		
		System.out.println(apiPostResponse.url());
		System.out.println(apiPostResponse.status());
		Assert.assertEquals(apiPostResponse.status(), 201);
		
		String responseText = apiPostResponse.text();
		System.out.println(responseText);
		
		//convert response text/json to POJO -- deserialization
		ObjectMapper objectMapper = new ObjectMapper();
		Users actualUser = objectMapper.readValue(responseText, Users.class); 
		
		System.out.println("actual user from the response ------>");
		System.out.println(actualUser);
		
		Assert.assertNotNull(actualUser.getId());
		
		String userId = actualUser.getId();
		System.out.println("new user id is: " + userId);
		
		//2. delete user -- user id -- 204
		
		APIResponse apiDeleteResponse = requestContext.delete("https://gorest.co.in/public/v2/users/" + userId,
				RequestOptions.create()
					.setHeader("Authorization", "Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")				
				);
		
		System.out.println(apiDeleteResponse.status());
		System.out.println(apiDeleteResponse.statusText());
		
		Assert.assertEquals(apiDeleteResponse.status(), 204 );
		
		System.out.println("delete user response body ===" + apiDeleteResponse.text());
		
		//3. get user -- user id -- 404 (user Not Found)
		
		APIResponse apiGetResponse = requestContext.get("https://gorest.co.in/public/v2/users/" + userId,
				RequestOptions.create()
					.setHeader("Authorization", "Bearer b07b6f98caa81ae878fce96eda234bdf598939ef72ba5ab8998b20e500ea2af8")					
				);
		

		System.out.println(apiGetResponse.text());
		
		int statusCode = apiGetResponse.status();
		System.out.println("response status code: " + statusCode);
		Assert.assertEquals(apiGetResponse.status(), 404 );
		Assert.assertEquals(apiGetResponse.statusText(), "Not Found");
		
		Assert.assertTrue(apiGetResponse.text().contains("Resource not found"));
		
	}
	 

}
