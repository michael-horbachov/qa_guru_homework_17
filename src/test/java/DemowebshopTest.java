import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

public class DemowebshopTest {

    private static final String BASE_URL = "http://demowebshop.tricentis.com";
    private static final int ITEMS_QUANTITY_TO_ADD = 11;

    @Test
    void verifyCartItemsIncreased() {

        Response response = given()
                .when()
                .get(BASE_URL + "/cart")
                .then()
                .statusCode(200)
                .extract().response();

        String cookie = response.getCookie("NOP.CUSTOMER");

        String itemsQuantityBeforeAdding = response.htmlPath().getString("**.find { it.@class == 'cart-qty' }")
                                                   .replaceAll("\\p{P}", "");
        int itemsBeforeAdding = Integer.parseInt(itemsQuantityBeforeAdding);

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("addtocart_13.EnteredQuantity=" + ITEMS_QUANTITY_TO_ADD)
                .cookie(String.valueOf(new Cookie("NOP.CUSTOMER", cookie)))
                .when()
                .post(BASE_URL + "/addproducttocart/details/13/1")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(" + (itemsBeforeAdding + ITEMS_QUANTITY_TO_ADD) + ")"));

        open(BASE_URL + "/Themes/DefaultClean/Content/images/logo.png");
        getWebDriver().manage().addCookie(new Cookie("NOP.CUSTOMER", cookie));
        open(BASE_URL);
        $(".cart-qty").shouldHave(exactText("(" + (itemsBeforeAdding + ITEMS_QUANTITY_TO_ADD) + ")"));
    }
}