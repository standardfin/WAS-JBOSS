package utility;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Utils {
		public static WebDriver driver = null;
	public static WebDriver OpenBrowser(int iTestCaseRow) throws Exception{
		String sBrowserName;
		try{
		sBrowserName = ExcelUtils.getCellData(iTestCaseRow, Constant.Col_Browser);
		if(sBrowserName.equals("Mozilla")){
			driver = new FirefoxDriver();
			Log.info("New driver instantiated");
		    driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
		    Log.info("Implicit wait applied on the driver for 10 seconds");
		    driver.get(Constant.URL);
		    Log.info("Web application launched successfully");
			}
		}catch (Exception e){
			Log.error("Class Utils | Method OpenBrowser | Exception desc : "+e.getMessage());
		}
		return driver;
	}
	
	public static String getTestCaseName(String sTestCase)throws Exception{
		String value = sTestCase;
		try{
			int posi = value.indexOf("@");
			value = value.substring(0, posi);
			posi = value.lastIndexOf(".");	
			value = value.substring(posi + 1);
			return value;
				}catch (Exception e){
			Log.error("Class Utils | Method getTestCaseName | Exception desc : "+e.getMessage());
			throw (e);
					}
			}
	
	 public static void mouseHoverAction(WebElement mainElement, String subElement){
		
		 Actions action = new Actions(driver);
         action.moveToElement(mainElement).perform();
         if(subElement.equals("Accessories")){
        	 action.moveToElement(driver.findElement(By.linkText("Accessories")));
        	 Log.info("Accessories link is found under Product Category");
         }
         if(subElement.equals("iMacs")){
        	 action.moveToElement(driver.findElement(By.linkText("iMacs")));
        	 Log.info("iMacs link is found under Product Category");
         }
         if(subElement.equals("iPads")){
        	 action.moveToElement(driver.findElement(By.linkText("iPads")));
        	 Log.info("iPads link is found under Product Category");
         }
         if(subElement.equals("iPhones")){
        	 action.moveToElement(driver.findElement(By.linkText("iPhones")));
        	 Log.info("iPhones link is found under Product Category");
         }
         action.click();
         action.perform();
         Log.info("Click action is performed on the selected Product Type");
	 }
	 
	 //Use this waitElelement for Clickble elements only like buttons, check boxes
	 public static void waitForElement(WebElement element){
		 
		 WebDriverWait wait = new WebDriverWait(driver, 10);
	     wait.until(ExpectedConditions.elementToBeClickable(element));
	 	}
	 
	 
	 
	 
	  
	    public void waitForElementDisplayed(final WebElement we){
	        try
	        {
	            new WebDriverWait(driver,10).until(new ExpectedCondition<Boolean>() {
	                public Boolean apply(WebDriver webDriver) {
	                    return we.isDisplayed();
	                }
	            });
	        }
	        catch(TimeoutException e)
	        {
	 
	            try{
	                String name = we.toString();
	                String arg[] = name.split(">");
	                 
	                if(arg[1]!= null){
	                	
	                	System.err.println("Web element not found");
	                   // System.err.println("web element " + arg[1] +" not displayed on " + se.browser().getCurrentUrl());
	                    //fail("web element " + arg[1] +" not displayed on " + se.browser().getCurrentUrl());
	                }else{
	                	System.err.println("Web element not displayed");
//	                	
//	                    System.err.println("web element " + arg[0] +" not displayed on " + se.browser().getCurrentUrl());
//	                    fail("web element " + arg[0] +" not displayed on " + se.browser().getCurrentUrl());
	                }
	            }
	            catch(NoSuchElementException ex){
	            	
	            	System.err.println("NoSuchElement exists");
//	                String error = ex.toString();
//	                String err[] = error.split("Command");
//	                System.err.println("No such element on " + se.browser().getCurrentUrl());
//	                fail(err[0] + " on " + se.browser().getCurrentUrl());
	            }
	        }
	 
	    }   
	     
	     // This method is used only for those exceptional cases where we need to identify whether one button/link is displayed or not.
	     //If it is not displayed,exception is thrown,only then we come to know that object is not displayed.So, we need to capture the exception and use try-catch
	    public boolean isElementPresent(WebElement we) 
	     {
	         boolean flag=false;
	            try {
	                flag = we.isDisplayed();
	                return flag; // Success!
	            } catch (Exception e) {
	                return flag;
	            } 
	     }
	      
	      
	     public void waitForElementLoading(WebElement we)
	     {
	        try{
	            int i = 0;
	            while (i < 10){
	                Thread.sleep(1000);
	                waitForElementDisplayed(we);                
	                    if (isElementPresent(we))
	                            break;                      
	                i = i + 1;
	            }
	             
	        }catch(Exception e){
	        	Log.info("Not able to locate the element on the page OR the element did not load on the page in less than 10 seconds during the test run: " + e.getMessage());
	        }       
	        //og().logTestStep("Not able to locate the element on the page OR the element did not load on the page in less than 10 seconds during the test run: " + e.getMessage());
	                
	     }
	      
	     // To scroll down on the page
//	     public void scrollDownToElement(WebElement we) 
//	     {
//	        Locatable element = (Locatable) we;
//	        Point p = null;
//	        try{ p = element.getCoordinates().onPage();}catch(Exception e){e.printStackTrace();}
//	        JavascriptExecutor js = (JavascriptExecutor) se.driver(); //getDriver();
//	        if (p == null){js.executeScript("window.scrollTo(" + 0 + "," + 500 + ");");}else{
//	        js.executeScript("window.scrollTo(" + p.getX() + "," + (p.getY()-150) + ");");}
//	      }
//	      
//	     public void scrollToElement(WebElement we, double move, String direction) 
//	     {
//	            Locatable element = (Locatable) we;
//	            Point p = null;
//	            try{ p = element.getCoordinates().onPage();}catch(Exception e){e.printStackTrace();}
//	            JavascriptExecutor js = (JavascriptExecutor) se.driver(); //getDriver();
//	            if (p != null){
//	                if(direction.equalsIgnoreCase("Up")){
//	                    double y = p.getY()+move;
//	                    js.executeScript("window.scrollTo("+ p.getX() + "," + y + ");");}else{
//	                        double y = p.getY()-move;
//	                        js.executeScript("window.scrollTo(" + p.getX() + "," + y + ");");}
//	            }
//	     }
//	      
	    // Clearing Cookies for IE explicitly
	    public void clearIECookies()
	    {
	        try
	        {
	            // Deletes Cookies
	            Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 2");
	            Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 8");
	            Runtime.getRuntime().exec("RunDll32.exe InetCpl.cpl,ClearMyTracksByProcess 1");
	            Thread.sleep(3000);
	            Runtime.getRuntime().exec("taskkill /f /im RunDll32.exe");
	            
	            Log.info("Cleared IE cookies");
	            //se.log().logTestStep("Cleared IE cookies");
	        } 
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	    }//end method clearIECookies

	 
	 //Need to add gettime to the 
		
	 public static void takeScreenshot(String sTestCaseName) throws Exception{
			try{
				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, new File(Constant.Path_ScreenShot + sTestCaseName + "+" + ".jpg"));	
			} catch (Exception e){
				Log.error("Class Utils | Method takeScreenshot | Exception occured while capturing ScreenShot : "+e.getMessage());
				throw new Exception();
			}
		}
	 
	 //need to update for failure
	 public static void takeScreenshotUponFail(String sTestCaseName) throws Exception{
			try{
				File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
				FileUtils.copyFile(scrFile, new File(Constant.Path_ScreenShot + sTestCaseName +".jpg"));	
			} catch (Exception e){
				Log.error("Class Utils | Method takeScreenshot | Exception occured while capturing ScreenShot : "+e.getMessage());
				throw new Exception();
			}
		}
	 
	 
	}
