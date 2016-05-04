package utility;

import java.util.NoSuchElementException;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Element {
	
	
	  
    public void waitForElementDisplayed(WebDriver driver, final WebElement we){
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
//                    System.err.println("web element " + arg[0] +" not displayed on " + se.browser().getCurrentUrl());
//                    fail("web element " + arg[0] +" not displayed on " + se.browser().getCurrentUrl());
                }
            }
            catch(NoSuchElementException ex){
            	
            	System.err.println("NoSuchElement exists");
//                String error = ex.toString();
//                String err[] = error.split("Command");
//                System.err.println("No such element on " + se.browser().getCurrentUrl());
//                fail(err[0] + " on " + se.browser().getCurrentUrl());
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
      
      
     public void waitForElementLoading(WebDriver driver,WebElement we)
     {
        try{
            int i = 0;
            while (i < 10){
                Thread.sleep(1000);
                waitForElementDisplayed(driver, we);                
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
//     public void scrollDownToElement(WebElement we) 
//     {
//        Locatable element = (Locatable) we;
//        Point p = null;
//        try{ p = element.getCoordinates().onPage();}catch(Exception e){e.printStackTrace();}
//        JavascriptExecutor js = (JavascriptExecutor) se.driver(); //getDriver();
//        if (p == null){js.executeScript("window.scrollTo(" + 0 + "," + 500 + ");");}else{
//        js.executeScript("window.scrollTo(" + p.getX() + "," + (p.getY()-150) + ");");}
//      }
//      
//     public void scrollToElement(WebElement we, double move, String direction) 
//     {
//            Locatable element = (Locatable) we;
//            Point p = null;
//            try{ p = element.getCoordinates().onPage();}catch(Exception e){e.printStackTrace();}
//            JavascriptExecutor js = (JavascriptExecutor) se.driver(); //getDriver();
//            if (p != null){
//                if(direction.equalsIgnoreCase("Up")){
//                    double y = p.getY()+move;
//                    js.executeScript("window.scrollTo("+ p.getX() + "," + y + ");");}else{
//                        double y = p.getY()-move;
//                        js.executeScript("window.scrollTo(" + p.getX() + "," + y + ");");}
//            }
//     }
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

}
