package org.example.api.users;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.api.BaseApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Tag("smoke")
@Epic("DummyJSON API")
@Feature("Users")
public class UsersTest extends BaseApiTest {

    @Test
    @DisplayName("GET /users/1 -> статус 200, id равно 1, firstName не пустое, email содержит @")
    @Severity(SeverityLevel.NORMAL)
    void getUsers() {
        given()
                .spec(REQ)
                .when()
                .get("/users/1")
                .then()
                .spec(RESP_200)
                .body("id", equalTo(1))
                .body("firstName", notNullValue())
                .body("email", containsString("@"));
    }
}