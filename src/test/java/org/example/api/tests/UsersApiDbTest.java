package org.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.api.config.Config;
import org.example.api.db.DbClient;
import org.example.api.db.UserRow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

class UsersApiDbTest {

    @BeforeEach
    void setup() {
        RestAssured.baseURI = Config.get("base.url");
        DbClient.execute("DELETE FROM users");
    }

    @Test
    void createUserViaApiThenVerifyInDb() {
        String email = "api_" + UUID.randomUUID() + "@example.com";

        long id =
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(Map.of("email", email))
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract().jsonPath().getLong("id");

        assertThat(id).isPositive();

        UserRow row = DbClient.queryOne(
                "SELECT id, email, status, created_at FROM users WHERE id = ?",
                UserRow.MAPPER, id
        ).orElseThrow();

        assertThat(row.email()).isEqualTo(email);
        assertThat(row.status()).isEqualTo("ACTIVE");
    }

    @Test
    void updateStatusViaApiThenVerifyInDb() {
        String email = "upd_" + UUID.randomUUID() + "@example.com";

        long id =
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(Map.of("email", email))
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract().jsonPath().getLong("id");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("status", "BLOCKED"))
                .when()
                .put("/users/" + id)
                .then()
                .statusCode(200);

        UserRow row = DbClient.queryOne(
                "SELECT id, email, status, created_at FROM users WHERE id = ?",
                UserRow.MAPPER, id
        ).orElseThrow();

        assertThat(row.status()).isEqualTo("BLOCKED");
    }

    @Test
    void deleteViaApiThenNotFoundInDb() {
        String email = "del_" + UUID.randomUUID() + "@example.com";

        long id =
                RestAssured.given()
                        .contentType(ContentType.JSON)
                        .body(Map.of("email", email))
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract().jsonPath().getLong("id");

        RestAssured.when()
                .delete("/users/" + id)
                .then()
                .statusCode(204);

        assertThat(DbClient.queryOne(
                "SELECT id, email, status, created_at FROM users WHERE id = ?",
                UserRow.MAPPER, id
        )).isEmpty();
    }
}