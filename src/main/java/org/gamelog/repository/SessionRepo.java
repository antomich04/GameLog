package org.gamelog.repository;

import org.gamelog.model.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionRepo {

    public String createSession(String username){
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30); //Session is valid for 1 month

        // First gets the uid for the username
        String getUidStatement = "SELECT get_uid(?)";
        String createSessionStatement = "SELECT create_session(?, ?, ?, ?)";

        try{
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Gets uid
            int uid;
            try (PreparedStatement uidStmt = conn.prepareStatement(getUidStatement)) {

                uidStmt.setString(1, username);
                ResultSet rs = uidStmt.executeQuery();

                if (rs.next()) {
                    uid = rs.getInt(1);
                } else {
                    return null; // User not found
                }
            }

            // Creates session
            try (PreparedStatement statement = conn.prepareStatement(createSessionStatement)) {
                Timestamp expiration = Timestamp.valueOf(expiresAt);
                Timestamp now = Timestamp.valueOf(LocalDateTime.now());

                statement.setInt(1, uid);
                statement.setString(2, token);
                statement.setTimestamp(3, expiration);
                statement.setTimestamp(4, now);

                statement.execute();
                return token;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Session getActiveSession(){
        String getSessionStatement = "SELECT * FROM get_active_session()";

        try{
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement(getSessionStatement);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                String username = rs.getString("username");
                String token = rs.getString("token");
                LocalDateTime expiresAt = rs.getTimestamp("expires_at").toLocalDateTime();

                // Checks if session is still valid
                if (expiresAt.isAfter(LocalDateTime.now())) {
                    return new Session(username, token);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSession(String token) {
        String deleteStatement = "SELECT delete_session(?)";

        try{
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement(deleteStatement);

            statement.setString(1, token);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
