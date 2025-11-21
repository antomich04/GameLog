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

}
