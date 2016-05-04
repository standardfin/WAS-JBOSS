package testCases;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;

import pageObjects.SigninPage;

public class GmailSigninTest {
	WebDriver driver=new FirefoxDriver();
	
	SigninPage signpage=PageFactory.initElements(driver, SigninPage.class);
	

	

	
	

}
