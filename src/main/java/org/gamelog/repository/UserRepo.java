package org.gamelog.repository;

import org.gamelog.model.SessionManager;

import java.sql.*;

public class UserRepo {

    public static Timestamp getCreationDate(String username){
        try(Connection conn = DatabaseConnection.getInstance().getConnection()){
            String dateQuery = "SELECT get_creation_date(?)";

            PreparedStatement stmt = conn.prepareStatement(dateQuery);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getTimestamp(1);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean deleteUser(String username){
        try(Connection conn = DatabaseConnection.getInstance().getConnection()){
            String deleteQuery = "SELECT delete_user(?)";

            PreparedStatement stmt = conn.prepareStatement(deleteQuery);
            stmt.setString(1, username);
            stmt.execute();

            return true;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
