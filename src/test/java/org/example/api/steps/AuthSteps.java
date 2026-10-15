package org.example.api.steps;

import io.qameta.allure.Step;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class AuthSteps {

    @Step("Логин пользователя {username}")
    public String login(RequestSpecification spec, String username, String password) {
        return given()
                .spec(spec)
                .body(Map.of("username", username, "password", password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().path("accessToken");
    }
}