package org.example.api.auth;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.example.api.BaseApiTest;
import org.example.api.steps.AuthSteps;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@Tag("auth")
@Epic("DummyJSON API")
@Feature("Auth tests")
public class AuthTest extends BaseApiTest {

    @Test
    @DisplayName("Authorization POST /auth/login and GET /auth/me")
    @Severity(SeverityLevel.CRITICAL)
    void authorization() {
        String token = new AuthSteps().login(REQ, "emilys", "emilyspass");

        given()
                .spec(REQ)
                .auth().oauth2(token)
                .when()
                .get("/auth/me")
                .then()
                .spec(RESP_200)
                .body("username", equalTo("emilys"));
    }
}