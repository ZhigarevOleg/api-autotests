package org.example.api.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;

class UserDbTest {

    @BeforeEach
    void createTable() {
        DbClient.execute("DROP TABLE IF EXISTS users");
        DbClient.execute("""
            CREATE TABLE users (
              id BIGSERIAL PRIMARY KEY,
              email VARCHAR(255) NOT NULL UNIQUE,
              status VARCHAR(50) NOT NULL,
              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """);
    }

    @Test
    void insertAndQueryOne() {
        String email = "test_" + UUID.randomUUID() + "@example.com";

        DbClient.execute(
                "INSERT INTO users (email, status) VALUES (?, ?)",
                email, "ACTIVE"
        );

        UserRow row = DbClient.queryOne(
                "SELECT id, email, status, created_at FROM users WHERE email = ?",
                UserRow.MAPPER,
                email
        ).orElseThrow();

        assertThat(row.email()).isEqualTo(email);
        assertThat(row.status()).isEqualTo("ACTIVE");
        assertThat(row.id()).isPositive();
    }

    @Test
    void queryListReturnsMultiple() {
        DbClient.execute("INSERT INTO users (email, status) VALUES (?, ?)", "a@test.com", "ACTIVE");
        DbClient.execute("INSERT INTO users (email, status) VALUES (?, ?)", "b@test.com", "BLOCKED");

        var rows = DbClient.queryList("SELECT id, email, status, created_at FROM users", UserRow.MAPPER);

        assertThat(rows).hasSize(2);
        assertThat(rows).extracting(UserRow::email).containsExactlyInAnyOrder("a@test.com", "b@test.com");
    }
}