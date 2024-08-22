package demo.wrappers;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Wrappers {
    private WebDriver driver;
    private WebDriverWait wait;

    public Wrappers(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Method to open a URL
    public void openURL(String url) {
        driver.get(url);
    }

    // Method to close the login popup if it appears
    public boolean closeLoginPopup() {
        try {
            // Assume the popup has a close button with this class name
            WebElement closeButton = driver.findElement(By.xpath("//button[contains(text(),'✕')]"));
            closeButton.click();
            return true;  // Popup was present and closed
        } catch (Exception e) {
            // No popup found, or an error occurred in locating/clicking it
            return false;  // Popup was not present
        }
    }

    // Method to send keys to an input field
    public void sendKeys(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear(); // Clear any pre-filled text
        element.sendKeys(text);
    }

    // Method to click an element
    public void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
    }

    public  void radiobutton(WebDriver driver, String radioButtonText) {
        try {
            WebElement element = driver.findElement(By.xpath("//div[@class='_6i1qKy' and contains(text(),'4★ & above')]'" + radioButtonText + "')]/../../..//div[@class='XqNaEv']"));
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to search for an item using the search box
    public void searchForItem(String query) {
        sendKeys(By.name("q"), query);
        sendKeys(By.name("q"), Keys.ENTER.toString());
    }
    // Method to clear the search box
    public void clearSearchBox() {
        WebElement searchBox = driver.findElement(By.xpath("//input[@name='q']"));
        searchBox.sendKeys(Keys.CONTROL + "a"); // Select all text
        searchBox.sendKeys(Keys.DELETE); // Delete selected text
    }
    

// Method to clear the search box
public void clearSearchBoxwashing() {
    try {
        // Locate the search box by its class name and value containing 'washing machine'
        WebElement searchBox = driver.findElement(By.xpath("//input[@class='zDPmFV' and contains(@value,'washing machine')]"));
        
        // Clear the search box
        searchBox.clear();
        
        // Verify that the search box is cleared by checking if its value is empty
        if (searchBox.getAttribute("value").isEmpty()) {
            System.out.println("Search box cleared successfully.");
        } else {
            System.out.println("Failed to clear the search box.");
        }
    } catch (Exception e) {
        System.out.println("Error clearing the search box: " + e.getMessage());
    }
}



   
    


    // Method to count items with rating less than or equal to 4 stars
    public int countItemsWithRatingLessThanOrEqualTo4Stars() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='_5OesEi']")));
        scrollDownPage();

        List<WebElement> ratingElements = driver.findElements(By.xpath("//span[@class='Y1HWO0']"));
        int count = 0;

        for (WebElement ratingElement : ratingElements) {
            try {
                String ratingText = ratingElement.getText().trim();
                double rating = Double.parseDouble(ratingText);
                if (rating <= 4.0) {
                    count++;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }

        return count;
    }

    // Helper method to scroll down the page
    private void scrollDownPage() {
        for (int i = 0; i < 5; i++) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,1000)");
            try {
                Thread.sleep(1000); // Wait for items to load
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to retrieve the titles of items with a specified minimum rating
    public Map<String, String> getItemsWithDiscountGreaterThan(double minDiscount) {
        Map<String, String> itemsWithDiscount = new HashMap<>();
    
        // Wait for the product listings to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='DOjaWF gdgoEp']")));
    
        // Locate all product containers
        List<WebElement> productContainers = driver.findElements(By.xpath("//div[@class='yKfJKb row']"));
    
        for (WebElement product : productContainers) {
            try {
                // Find the discount element within each product container
                WebElement discountElement = product.findElement(By.xpath(".//div[@class='UkUFwK']"));
                String discountText = discountElement.getText().replace("% off", "").trim();
                double discount = Double.parseDouble(discountText);
    
                if (discount > minDiscount) {
                    // Get the title of the product
                    WebElement titleElement = product.findElement(By.xpath(".//*[@class='KzDlHZ']"));
                    String title = titleElement.getText();
    
                    // Store the title and discount in the map
                    itemsWithDiscount.put(title, discountText);
                }
            } catch (Exception e) {
                // Handle cases where elements may not be found or are not structured as expected
                System.out.println("Item does not have a discount or information could not be retrieved.");
                continue;
            }
        }
    
        return itemsWithDiscount;
    }
    

    // Method to retrieve the title and discount percentage of items with a specified minimum discount
    public Map<String, String> getItemsWithMinDiscount(double minDiscount) {
        List<WebElement> discountElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//div[contains(@class, '_3Ay6Sb')]")));
        Map<String, String> itemsWithDiscount = new HashMap<>();

        for (WebElement element : discountElements) {
            String discountText = element.getText().replace("% off", "").trim();
            double discount = Double.parseDouble(discountText);
            if (discount >= minDiscount) {
                WebElement titleElement = element.findElement(By.xpath("./ancestor::div[@class='_2kHMtA']//a[@class='IRpwTa']"));
                itemsWithDiscount.put(titleElement.getText(), discountText);
            }
        }

        return itemsWithDiscount;
    }

    // Method to retrieve the titles and image URLs of the top 5 items with the highest number of reviews
    public List<Map<String, String>> getTop5ItemsWithHighestReviews() {
        List<WebElement> reviewElements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//span[@class='_2_R_DZ']")));
        List<Map<String, String>> top5Items = new ArrayList<>();

        List<Integer> reviewCounts = reviewElements.stream()
                .map(e -> Integer.parseInt(e.getText().replaceAll("[^0-9]", "")))
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        for (int i = 0; i < Math.min(5, reviewCounts.size()); i++) {
            int reviewCount = reviewCounts.get(i);
            WebElement element = reviewElements.get(i);
            WebElement titleElement = element.findElement(By.xpath("./ancestor::div[@class='_2kHMtA']//a[@class='IRpwTa']"));
            WebElement imgElement = element.findElement(By.xpath("./ancestor::div[@class='_2kHMtA']//img"));

            Map<String, String> itemDetails = new HashMap<>();
            itemDetails.put("title", titleElement.getText());
            itemDetails.put("imageUrl", imgElement.getAttribute("src"));
            itemDetails.put("reviews", String.valueOf(reviewCount));

            top5Items.add(itemDetails);
        }

        return top5Items;
    }

    // Placeholder methods
    
}
