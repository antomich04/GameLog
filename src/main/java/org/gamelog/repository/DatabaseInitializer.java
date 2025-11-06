package org.gamelog.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            try(PreparedStatement stmt = conn.prepareStatement("SET search_path TO gamelog, public")) {
                stmt.execute();
            }

            try (PreparedStatement stmt = conn.prepareStatement("SELECT create_db()")) {
                stmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
