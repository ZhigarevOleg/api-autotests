package org.example.api.products;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.api.BaseApiTest;
import org.example.api.steps.AuthSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("regression")
@Epic("DummyJSON API")
@Feature("Negative tests")
public class ProductsNegativeTest extends BaseApiTest{


    @Test
    @DisplayName("POST /auth/login с неверным паролем -> 400")
    @Severity(SeverityLevel.NORMAL)
    void loginWithWrongPassword() {
        given()
                .spec(REQ)
                .body(Map.of("username", "emilys", "password", "wrong"))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(400)
                .body("message", containsString("Invalid"));
    }


    @Test
    @DisplayName("GET /auth/me без auth() -> 401")
    @Severity(SeverityLevel.NORMAL)
    void getMeWithoutToken(){
        given()
                .when()
                .spec(REQ)
                .get("/auth/me")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("GET /products/{id} с pathParam(999999) -> 404 + message isNotEmpty")
    @Severity(SeverityLevel.NORMAL)
    void getUnknownProductMessage(){
        given()
                .spec(REQ)
                .pathParam("id", 999999)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(404)
                .body("message", not(emptyOrNullString()));
    }

    @Test
    @DisplayName("GET /products/search с queryParam(\"q\", \"\") -> 200 + products not null")
    @Severity(SeverityLevel.NORMAL)
    void searchWithEmptyQuery(){
        given()
                .spec(REQ)
                .queryParam("q", "")
                .when()
                .get("/products/search")
                .then()
                .statusCode(200)
                .body("products", notNullValue());
    }
}
