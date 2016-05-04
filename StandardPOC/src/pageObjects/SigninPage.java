package pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;

public class SigninPage {
	final WebDriver driver;
	
// Constructor  to initialize object variables
	
	public SigninPage(WebDriver driver)
	{
		this.driver=driver;
	}
	
	
	@FindBy(how=How.ID,using="Email")
	private WebElement eMail;
	
	
	public WebElement geteMail()
	{
		return eMail;
	}
	
	@FindBy(how=How.ID,using="next")
	private WebElement next;
	
	
	public WebElement getNextButton()
	{
		return  next;
	}
	
//	
//	@FindBy(how=How.ID,using="")
//	private WebElement eMail;
//	
//	
//	public WebElement geteMail()
//	{
//		return eMail;
//	}
	
	
	
	
	
	

}
