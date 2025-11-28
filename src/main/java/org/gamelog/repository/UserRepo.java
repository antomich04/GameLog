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

}
