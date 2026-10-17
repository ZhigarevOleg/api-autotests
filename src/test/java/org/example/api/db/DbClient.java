package org.example.api.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.api.config.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class DbClient {

    private static final HikariDataSource DS;

    static {
        HikariConfig hc = new HikariConfig();
        hc.setJdbcUrl(Config.get("db.url"));
        hc.setUsername(Config.get("db.user"));
        hc.setPassword(Config.get("db.password"));
        hc.setMaximumPoolSize(Integer.parseInt(Config.get("db.pool.size")));
        hc.setPoolName("api-tests-pool");
        hc.setAutoCommit(true);
        DS = new HikariDataSource(hc);
    }

    private DbClient() {}

    public static <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... args) {
        try (Connection c = DS.getConnection();
             PreparedStatement ps = prepare(c, sql, args);
             ResultSet rs = ps.executeQuery()) {
            log(sql, args);
            return rs.next() ? Optional.ofNullable(mapper.map(rs)) : Optional.empty();
        } catch (SQLException e) {
            throw new DbException("queryOne failed: " + sql, e);
        }
    }

    public static <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... args) {
        try (Connection c = DS.getConnection();
             PreparedStatement ps = prepare(c, sql, args);
             ResultSet rs = ps.executeQuery()) {
            log(sql, args);
            List<T> out = new ArrayList<>();
            while (rs.next()) {
                out.add(mapper.map(rs));
            }
            return out;
        } catch (SQLException e) {
            throw new DbException("queryList failed: " + sql, e);
        }
    }

    public static int execute(String sql, Object... args) {
        try (Connection c = DS.getConnection();
             PreparedStatement ps = prepare(c, sql, args)) {
            log(sql, args);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DbException("execute failed: " + sql, e);
        }
    }

    private static PreparedStatement prepare(Connection c, String sql, Object... args) throws SQLException {
        PreparedStatement ps = c.prepareStatement(sql);
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
        return ps;
    }

    private static void log(String sql, Object... args) {
        System.out.println("[SQL] " + sql + " | " + Arrays.toString(args));
    }
}