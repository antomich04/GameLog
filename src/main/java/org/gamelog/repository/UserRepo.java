package org.gamelog.repository;

import java.sql.*;

public class UserRepo {

    public static Timestamp getCreationDate(String username) {
        String dateQuery = "SELECT get_creation_date(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(dateQuery)) {

            stmt.setString(1, username);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getTimestamp(1);
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public static boolean deleteUser(String username) {
        String deleteQuery = "SELECT delete_user(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setString(1, username);
            stmt.execute();
            return true;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static String getEmail(String username){
        String emailQuery = "SELECT get_email(?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(emailQuery)){
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getPassword(String username){
        String passwordQuery = "SELECT get_password(?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(passwordQuery)){
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getPasswordByEmail(String email){
        String passwordQuery = "SELECT get_password_by_email(?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(passwordQuery)){
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString(1);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUsername(String currentUsername, String newUsername){
        String updateQuery = "SELECT update_username(?, ?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateQuery)){
            stmt.setString(1, currentUsername);
            stmt.setString(2, newUsername);

            stmt.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void updatePassword(String username, String newPassword){
        String updateQuery = "SELECT update_password(?, ?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateQuery)){
            stmt.setString(1, username);
            stmt.setString(2, newPassword);

            stmt.execute();

        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static Boolean updatePasswordByEmail(String email, String newPassword) throws SQLException {
        String updateQuery = "SELECT update_password_by_email(?, ?)";

        try(Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateQuery)){
            stmt.setString(1, email);
            stmt.setString(2, newPassword);

            stmt.execute();

            return true;

        }catch(SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public static boolean isNotificationsEnabled(String username) {
        // Assumes a stored function named 'get_notifications_enabled' exists
        String query = "SELECT get_notifications_enabled(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // getBoolean returns false if the value is NULL, which is safe here
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Default to false if user not found or error occurs
        return false;
    }

    public static void updateNotificationStatus(String username, boolean isEnabled) {
        String updateQuery = "SELECT update_notification_status(?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, username);
            stmt.setBoolean(2, isEnabled);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDarkModeEnabled(String username) {
        String query = "SELECT dark_mode_enabled FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("dark_mode_enabled");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to light mode if error or not found
    }

    public static void setDarkMode(String username, boolean enabled) {
        String query = "UPDATE users SET dark_mode_enabled = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, enabled);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
