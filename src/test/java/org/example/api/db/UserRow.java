package org.example.api.db;

import java.time.Instant;

public record UserRow(
        long id,
        String email,
        String status,
        Instant createdAt
) {
    public static final RowMapper<UserRow> MAPPER = rs -> new UserRow(
            rs.getLong("id"),
            rs.getString("email"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toInstant()
    );
}