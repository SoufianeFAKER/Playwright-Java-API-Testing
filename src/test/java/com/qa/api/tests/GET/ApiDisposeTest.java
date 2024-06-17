package com.qa.api.tests.GET;

import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.PlaywrightException;

public class ApiDisposeTest {
	
	Playwright playwright;
	APIRequest request;
	APIRequestContext requestContext;

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
	
	@Test
	public void disposeResponseTest() {
		
		// Request-1:
		APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");

		int statusCode = apiResponse.status();
		System.out.println("response status code : " + statusCode);

		Assert.assertEquals(statusCode, 200);
		Assert.assertEquals(apiResponse.ok(), true);

		String statusResText = apiResponse.statusText();
		System.out.println(statusResText);

		System.out.println("------ print API response with plain text ------");
		System.out.println(apiResponse.text());
		
		apiResponse.dispose();
		//will dispose only response body but status code, url, status text will remain same
		System.out.println("------ print API response after dispose with plain text ------");
		
		try {

			System.out.println(apiResponse.text());
			
		} catch (PlaywrightException e) {

			System.out.println("api response body is disposed");
		}
		
		int statusCodeAd = apiResponse.status();
		System.out.println("response status code after dispose : " + statusCodeAd);
		
		String statusResTextAd = apiResponse.statusText();
		System.out.println(statusResTextAd);
		
		System.out.println("response url: " + apiResponse.url());
		
		
		// Request-2:
		
		APIResponse apiResponse2 = requestContext.get("https://reqres.in/api/users/2");
		
		System.out.println("********** get response body for 2nd request: **********");
		System.out.println("status code: " +apiResponse2.status());
		System.out.println("response body: " +apiResponse2.text());
		
		// request context dispose
		requestContext.dispose();
		
		// --> generate error because the dispose method (empty body)
//		System.out.println("response1 body: " + apiResponse.text());
//		System.out.println("response2 body: " + apiResponse2.text());
		
	}
	
	

	

}
