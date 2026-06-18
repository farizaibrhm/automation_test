import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {

        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");

        login();
        sortProducts();
        List<String> selectedItems = addItemsToCart();
        checkout(selectedItems);
        verifySuccessMessage();

        driver.quit();
    }

    public static void login() {
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();
    }

    public static void sortProducts() {
        String[] options = {
                "Name (Z to A)",
                "Price (low to high)",
                "Price (high to low)"
        };

        for (String option : options) {
            Select dropdown = new Select(
                    wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.className("product_sort_container")
                    ))
            );
            dropdown.selectByVisibleText(option);
        }
    }

    public static List<String> addItemsToCart() {
        List<String> items = new ArrayList<>();

        String item1 = driver.findElement(By.xpath("//div[text()='Test.allTheThings() T-Shirt (Red)']")).getText();
        String item2 = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']")).getText();

        items.add(item1);
        items.add(item2);

        driver.findElement(By.id("add-to-cart-test.allthethings()-t-shirt-(red)")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();

        return items;
    }

    public static void checkout(List<String> selectedItems) {

        driver.findElement(By.className("shopping_cart_link")).click();
        driver.findElement(By.id("checkout")).click();

        driver.findElement(By.id("first-name")).sendKeys("Intan");
        driver.findElement(By.id("last-name")).sendKeys("Ibrahim");
        driver.findElement(By.id("postal-code")).sendKeys("47301");
        driver.findElement(By.id("continue")).click();

        List<WebElement> overviewItems =
                driver.findElements(By.className("inventory_item_name"));

        List<String> checkoutItems = new ArrayList<>();

        for (WebElement item : overviewItems) {
            checkoutItems.add(item.getText());
        }

        if (selectedItems.equals(checkoutItems)) {
            System.out.println("PASS - Items match");
        } else {
            System.out.println("FAIL - Items mismatch");
        }

        driver.findElement(By.id("finish")).click();
    }

    public static void verifySuccessMessage() {
        String message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.className("complete-header")
        )).getText();

        if (message.contains("Thank you for your order!")) {
            System.out.println("PASS - Checkout success");
        } else {
            System.out.println("FAIL - Checkout failed");
        }
    }
}