package demo;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

// Import your custom Wrappers class
import demo.wrappers.Wrappers;

public class TestCases {

    private ChromeDriver driver;
    private Wrappers wrapper; // Wrappers instance

    @BeforeTest
    public void startBrowser() {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Initialize the wrapper instance here
        wrapper = new Wrappers(driver);
    }


    @Test
    public void testCase01() throws InterruptedException {
        // Navigate to Flipkart
        wrapper.openURL("https://www.flipkart.com");
    
        // Close login popup if present
        boolean isPopupClosed = wrapper.closeLoginPopup();
        if (isPopupClosed) {
            System.out.println("Login popup was displayed and closed.");
        } else {
            System.out.println("Login popup not displayed.");
        }
    
        // Search for "Washing Machine"
        wrapper.searchForItem("Washing Machine");
        wrapper.click(By.xpath("//button[@type='submit']"));
        Thread.sleep(5000);
    
        // Wait and click on the popularity sort option
        driver.get("https://www.flipkart.com/search?q=washing+machine&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=off&as=off&as-pos=1&as-type=HISTORY&sort=popularity");
        // 
        Thread.sleep(3000);
            int count = wrapper.countItemsWithRatingLessThanOrEqualTo4Stars();
            System.out.println("Number of items with rating <= 4 stars: " + count);
            WebElement searchBox = driver.findElement(By.xpath("//input[@class='zDPmFV' and contains(@value,'washing machine')]"));
            wrapper.clearSearchBox();

            //Assert.assertTrue(count > 0, "There should be at least one item with rating <= 4 stars.");
        }
    
        @Test
        public void testCase02() throws InterruptedException {
            // Search for "iPhone"
            wrapper.sendKeys(By.name("q"), "iPhone");

            wrapper.click(By.xpath("//button[@type='submit']"));
            Thread.sleep(5000);
        
            // Get titles and discount percentages for items with more than 17% discount
            Map<String, String> itemsWithDiscount = wrapper.getItemsWithDiscountGreaterThan(17.0);
            if (itemsWithDiscount.isEmpty()) {
                System.out.println("No items with more than 17% discount found.");
            } else {
                for (Map.Entry<String, String> entry : itemsWithDiscount.entrySet()) {
                    System.out.println("Title: " + entry.getKey() + " | Discount: " + entry.getValue() + "%");
                }
            }
        
            // Clear the search box
            WebElement searchBox = driver.findElement(By.name("q"));
            wrapper.clearSearchBox();
        }
        
   

        
    @Test
    public void testCase03() throws InterruptedException {
        wrapper.sendKeys(By.name("q"), "Coffee Mug");
        wrapper.click(By.xpath("//button[@type='submit']"));
        Thread.sleep(3000);

        // Filter for 4 stars and above
        //wrapper.click(By.xpath("//div[@class='_6i1qKy' and contains(text(),'4★ & above')]"));
        wrapper.radiobutton(driver, "4★ & above");
        System.out.println("Selected radiobutton for '4★ & above'.");
        Thread.sleep(3000);

        // Get the titles and image URLs of the 5 items with the highest number of reviews
        List<WebElement> items = driver.findElements(By.xpath("//div[@class='DOjaWF gdgoEp']"));
        items.sort((item1, item2) -> {
            int reviews1 = Integer.parseInt(item1.findElement(By.xpath(".//span[@class='_2_R_DZ']")).getText().split(" ")[0].replace(",", ""));
            int reviews2 = Integer.parseInt(item2.findElement(By.xpath(".//span[@class='_2_R_DZ']")).getText().split(" ")[0].replace(",", ""));
            return reviews2 - reviews1;
        });

        int itemsToProcess = Math.min(items.size(), 5);

    for (int i = 0; i < itemsToProcess; i++) {
        WebElement item = items.get(i);
        String title = item.findElement(By.xpath(".//a[contains(@class,'wjcEIp')]")).getText();
        String imageUrl = item.findElement(By.xpath(".//img[@class='DByuf4']")).getAttribute("src");
        System.out.println("Title: " + title + " | Image URL: " + imageUrl);
    }
    }

   
       
    @AfterTest
    public void endTest()
    {
        driver.close();
        driver.quit();

    }
}