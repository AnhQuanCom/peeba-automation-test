package com.peeba.test.e2etests.checkout;

import com.peeba.test.e2etests.SpringBaseTestNGTest;
import com.peeba.test.e2etests.listener.AllureReportListener;
import com.peeba.test.e2etests.pages.checkout.CheckoutPage;
import com.peeba.test.e2etests.pages.home.HomePage;
import com.peeba.test.e2etests.pages.login.LoginPopup;
import com.peeba.test.e2etests.pages.product.ProductPage;
import com.peeba.test.e2etests.processor.UserData;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;

@Log4j2
@Listeners(AllureReportListener.class)
public class TC002_CheckoutTest extends SpringBaseTestNGTest {

    String productName;
    float threshold;
    private UserData userData;
    @Autowired
    private HomePage homePage;
    @Autowired
    private LoginPopup popup;
    @Autowired
    private ProductPage productPage;
    @Autowired
    private CheckoutPage checkoutPage;

    @BeforeClass
    @Parameters({"user-registration-data-file"})
    public void setUp(String userDataFile) throws IOException {
        log.info("Setup the test...");
        this.homePage.goTo(url);
        this.homePage.isAt();
        userData = UserData.get(System.getProperty("user.dir") + userDataFile).get(1);

    }

    @Test(priority = 0, description = "Verify that we logged in successfully to the web")
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Login to the app")
    @Description("Test case description: Login to the page with valid credentials")
    public void login() {
        log.info("Step 1 : Login to the app...");
        this.homePage.getHeaderComponent().clickOnLoginLink();
        this.popup.login(userData.getEmail(), userData.getPassword());
        this.productPage.getSuggestBrandsComponent().isAt();
    }

    @Test(priority = 1, description = "Verify that we can go to branch by clicking on brand name")
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Click on brand appeared in suggested brands")
    @Description("Test case description: Click on the branch and verify we can see brand products")
    public void clickOnSuggestedBrandWithName() {
        log.info("Step 2: Click on the brand name in Suggest Brands list");
        this.productPage.getSuggestBrandsComponent().clickOnSuggestBrandWithName("exampleG");
        this.productPage.getProductBrandComponent().isAt();
        this.productPage.getProductBrandComponent().verifyBrandNameDisplayedCorrectly("exampleG");
        threshold = this.productPage.getProductBrandComponent().getThresholdOfBrand();
    }


    @Test(priority = 2, description = "Verify that we can go to product detail to add product to cart")
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Click on any product")
    @Description("Test case description: Click on the product to go to product detail page")
    public void clickOnRandomProduct() {
        log.info("Step 3: Click on any product of brand");
        this.productPage.getProductBrandComponent().clickOnRandomProductOfBrand();
        this.productPage.getProductDetailComponent().isAt();
    }

    @Test(priority = 3, description = "Verify that we can select quantity for product to checkout")
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Select the quantity then click on Add to Cart button")
    @Description("Test case description: User selects the quantity then click on Add to Cart button")
    public void addProductToCart() {
        log.info("Step 4: Click on Add to Cart button");
        this.productPage.getProductDetailComponent().selectQuantityOptionForProduct(threshold);
        this.productPage.getProductDetailComponent().clickOnAddToCartButton();
        this.productPage.getCheckoutComponent().isAt();
    }

    @Test(priority = 4, description = "Verify that we can checkout", dependsOnMethods = {"addProductToCart"})
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Click on Checkout")
    @Description("Test case description: Click on Checkout")
    public void checkoutCart() {
        log.info("Step 5: Click on Checkout button");
        this.productPage.getCheckoutComponent().clickOnCheckoutButton();
        this.checkoutPage.isAt();
    }

    @Test(priority = 5, description = "Verify that we can place the order", dependsOnMethods = {"checkoutCart"})
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Fullfil shipping form then choose card for placing order")
    @Description("Test case description: Fill form then click on Place order")
    public void placeOrder() {
        // Note: This user already had an address and payment option
        log.info("Step 6: Fullfill shipping form then click on Place Order");
        this.checkoutPage.getShippingAddressComponent().fullfilShippingInformationForm();
        this.checkoutPage.getPaymentComponent().fullfilCardInformationForm();
        this.checkoutPage.getConfirmationComponent().clickOnPlaceOrder();
    }

    @Test(priority = 6, description = "Verify that we can see the confirmation message after placing order the order", dependsOnMethods = {"placeOrder"})
    @Severity(SeverityLevel.NORMAL)
    @Step("User: Check that we can see confirmation message")
    @Description("Test case description: Check that we can see confirmation message")
    public void checkConfirmationMessage() {
        log.info("Step 7: Check that confirmation message is shown");
        this.checkoutPage.getCheckoutConfirmationComponent().isAt();
        this.checkoutPage.getCheckoutConfirmationComponent().verifyConfirmationMessagePresent();
    }

}
