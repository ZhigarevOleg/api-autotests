package org.example.api.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.example.api.config.Config;
import org.example.api.db.DbClient;
import org.example.api.db.UserRow;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersApiTest {

    @BeforeAll
    void initConfig() {
        RestAssured.baseURI = Config.get("base.url");
    }

    @BeforeEach
    void cleanDb() {
        DbClient.execute("DELETE FROM users");
    }

    private long createUser(String email) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email))
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    private UserRow selectUser(long id) {
        return DbClient.queryOne(
                "SELECT id, email, status, created_at FROM users WHERE id = ?",
                UserRow.MAPPER, id
        ).orElseThrow();
    }

    @Nested
    class Create {

        @Test
        void success() {
            String email = "create_" + UUID.randomUUID() + "@example.com";

            long id = createUser(email);

            assertThat(selectUser(id).email()).isEqualTo(email);
        }

        @Test
        void duplicateEmailReturns409() {
            String email = "dup_" + UUID.randomUUID() + "@example.com";
            createUser(email);

            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("email", email))
                    .when()
                    .post("/users")
                    .then()
                    .statusCode(409);
        }
    }

    @Nested
    class Update {

        long existingId;

        @BeforeEach
        void prepareUser() {
            existingId = createUser("upd_" + UUID.randomUUID() + "@example.com");
        }

        @Test
        void changeStatus() {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("status", "BLOCKED"))
                    .when()
                    .put("/users/" + existingId)
                    .then()
                    .statusCode(200);

            assertThat(selectUser(existingId).status()).isEqualTo("BLOCKED");
        }

        @Test
        void notFoundReturns404() {
            RestAssured.given()
                    .contentType(ContentType.JSON)
                    .body(Map.of("status", "BLOCKED"))
                    .when()
                    .put("/users/99999")
                    .then()
                    .statusCode(404);
        }
    }

    @Nested
    class Delete {

        @Test
        void success() {
            long id = createUser("del_" + UUID.randomUUID() + "@example.com");

            RestAssured.when()
                    .delete("/users/" + id)
                    .then()
                    .statusCode(204);

            assertThat(DbClient.queryOne(
                    "SELECT id FROM users WHERE id = ?",
                    UserRow.MAPPER, id
            )).isEmpty();
        }
    }
}