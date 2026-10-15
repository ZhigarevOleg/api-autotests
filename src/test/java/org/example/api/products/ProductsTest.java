package org.example.api.products;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.api.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@Tag("smoke")
@Epic("DummyJSON API")
@Feature("Smoke tests")
public class ProductsTest extends BaseApiTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 5, 10})
    @DisplayName("GET /products/{id} -> 200 и поля не пустые")
    @Severity(SeverityLevel.NORMAL)
    void shouldGetProductById(int id) {
        given()
                .spec(REQ)
                .pathParam("id", id)
                .when()
                .get("/products/{id}")
                .then()
                .spec(RESP_200)
                .body("id", equalTo(id))
                .body("title", notNullValue())
                .body("price", greaterThan(0F));
    }

    @Test
    @DisplayName("GET /products с параметром limit=5")
    @Severity(SeverityLevel.NORMAL)
    void shouldGetProductWithLimit() {
        given()
                .spec(REQ)
                .queryParam("limit", 5)
                .when()
                .get("/products")
                .then()
                .spec(RESP_200)
                .body("products.size()", equalTo(5))
                .body("total", greaterThan(0))
                .body("limit", equalTo(5));
    }

    @ParameterizedTest
    @CsvSource({
            "phone, 1",
            "laptop, 1",
            "watch, 1"
    })
    @DisplayName("GET /products/search?q={0} -> есть результаты")
    @Severity(SeverityLevel.NORMAL)
    void shouldSearchProducts(String q, int minCount) {
        given()
                .spec(REQ)
                .queryParam("q", q)
                .when()
                .get("/products/search")
                .then()
                .spec(RESP_200)
                .body("products.size()", greaterThanOrEqualTo(minCount));
    }

    @ParameterizedTest
    @MethodSource("invalidIds")
    @DisplayName("GET /products/{id} -> 404")
    @Severity(SeverityLevel.NORMAL)
    void shouldReturn404ForUnknownProduct(int id) {
        given()
                .spec(REQ)
                .pathParam("id", id)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(404);
    }

    static Stream<Integer> invalidIds() {
        return Stream.of(999998, 999999, 1000000);
    }

    @Test
    @DisplayName("GET /products?limit=10 -> проверка через GPath")
    @Severity(SeverityLevel.NORMAL)
    void shouldValidateProductsListViaGPath() {
        given()
                .spec(REQ)
                .queryParam("limit", 10)
                .when()
                .get("/products")
                .then()
                .spec(RESP_200)
                .body("products.size()", equalTo(10))
                .body("products[0].id", greaterThan(0))
                .body("products.id", hasItem(1))
                .body("products.findAll { it.price > 0 }.size()", equalTo(10));
    }
}