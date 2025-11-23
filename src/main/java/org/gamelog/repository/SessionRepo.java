package org.gamelog.repository;

import org.gamelog.model.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionRepo {

    public String createSession(String username, String deviceId) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        String getUidStatement = "SELECT get_uid(?)";
        String createSessionStatement = "SELECT create_session(?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

            // Get UID
            int uid;
            try (PreparedStatement uidStmt = conn.prepareStatement(getUidStatement)) {
                uidStmt.setString(1, username);
                try (ResultSet rs = uidStmt.executeQuery()) {
                    if (!rs.next()) return null;
                    uid = rs.getInt(1);
                }
            }

            // Create session
            try (PreparedStatement stmt = conn.prepareStatement(createSessionStatement)) {
                stmt.setInt(1, uid);
                stmt.setString(2, token);
                stmt.setTimestamp(3, Timestamp.valueOf(expiresAt));
                stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setString(5, deviceId);
                stmt.execute();
            }

            return token;

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public Session getActiveSession(String deviceId) {
        String activeSessionQuery = "SELECT * FROM get_active_session(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(activeSessionQuery)) {

            stmt.setString(1, deviceId);

            try (ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    LocalDateTime expires = rs.getTimestamp("expires_at").toLocalDateTime();
                    if(expires.isAfter(LocalDateTime.now())){
                        return new Session(rs.getString("username"), rs.getString("token"));
                    }
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public void deleteSession(String token) {
        String deleteStatement = "SELECT delete_session(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteStatement)) {

            stmt.setString(1, token);
            stmt.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
}
