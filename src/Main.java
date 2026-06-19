import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static WebDriver driver;
    static WebDriverWait wait;

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        // Newer Chrome-specific flags
        options.addArguments("--disable-features=PasswordManagerOnboarding,PasswordLeakDetection,AutofillKeyboardAccessory,PasswordImport");
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-notifications");
        options.addArguments("--guest"); // forces a guest profile, no sync, no saved-password prompts at all

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        login();
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

    public static List<String> addItemsToCart() {
        List<String> items = new ArrayList<>();
        String item1 = driver.findElement(By.xpath("//div[text()='Test.allTheThings() T-Shirt (Red)']")).getText();
        String item2 = driver.findElement(By.xpath("//div[text()='Sauce Labs Bike Light']")).getText();
        String item3 = driver.findElement(By.xpath("//div[text()='Sauce Labs Backpack']")).getText();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        driver.findElement(By.id("add-to-cart-test.allthethings()-t-shirt-(red)")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-bike-light")).click();
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
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
