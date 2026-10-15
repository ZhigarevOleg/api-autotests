package org.example.api.products;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.api.BaseApiTest;
import org.example.api.models.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Tag("crud")
@Epic("DummyJSON API")
@Feature("Crud tests")
public class ProductsCrudTest extends BaseApiTest {

    @Test
    @DisplayName("POST /products/add -> 201")
    @Severity(SeverityLevel.CRITICAL)
    void createProduct() {
        Product newProduct = new Product(10, "Apple", 15.00);

        Product product =
                given()
                        .spec(REQ)
                        .body(newProduct)
                        .when()
                        .post("/products/add")
                        .then()
                        .statusCode(201)
                        .extract().as(Product.class);

        assertAll(
                () -> assertTrue(product.id() > 0),
                () -> assertEquals(newProduct.title(), product.title()),
                () -> assertEquals(newProduct.price(), product.price())
        );
    }

    @Test
    @DisplayName("PUT /products/1 -> 200")
    @Severity(SeverityLevel.CRITICAL)
    void updateProduct() {
        given()
                .spec(REQ)
                .body(Map.of("title", "Updated Product Title"))
                .when()
                .put("/products/1")
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Product Title"));
    }

    @Test
    @DisplayName("DELETE /products/1 -> 200")
    @Severity(SeverityLevel.CRITICAL)
    void deleteProduct() {
        given()
                .spec(REQ)
                .when()
                .delete("/products/1")
                .then()
                .statusCode(200)
                .body("isDeleted", equalTo(true));
    }
}