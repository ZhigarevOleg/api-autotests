package org.example.api.products;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.api.BaseApiTest;
import org.example.api.models.Product;
import org.example.api.models.ProductsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.*;


@Tag("regression")
@Epic("DummyJSON API")
@Feature("Regression tests")
public class ProductModelTest extends BaseApiTest {

    @Test
    @DisplayName("GET /products/1 → 200 и корректная десериализация в Product")
    @Severity(SeverityLevel.NORMAL)
    void searchProduct() {
        Product productMapTest =
                given()
                        .spec(REQ)
                        .when()
                        .get("/products/1")
                        .then()
                        .spec(RESP_200)
                        .extract().as(Product.class);

        assertAll(
                () -> assertEquals(1, productMapTest.id()),
                () -> assertNotNull(productMapTest.title()),
                () -> assertFalse(productMapTest.title().isEmpty()),
                () -> assertTrue(productMapTest.price() > 0)
        );
    }

    @Test
    @DisplayName("Получение списка продуктов и его валидация")
    @Severity(SeverityLevel.NORMAL)
    void getProductsList() {
        ProductsResponse pr = given()
                .spec(REQ)
                .queryParam("limit", 3)
                .when()
                .get("/products")
                .then()
                .spec(RESP_200)
                .extract().as(ProductsResponse.class);

        assertAll(
                () -> assertEquals(3, pr.products().size()),
                () -> assertTrue((pr.total() > 0)),
                () -> assertEquals(3, pr.limit()),
                () -> assertTrue(pr.products().get(0).id() > 0)
        );
    }

    @Test
    @DisplayName("GET /products/1 -> соответствует JSON Schema")
    @Severity(SeverityLevel.NORMAL)
    void shouldMatchProductSchema() {
        given()
                .spec(REQ)
                .when()
                .get("/products/1")
                .then()
                .spec(RESP_200)
                .body(matchesJsonSchemaInClasspath("schemas/product-schema.json"));
    }
}