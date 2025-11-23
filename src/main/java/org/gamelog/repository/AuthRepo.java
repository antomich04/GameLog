package org.gamelog.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.gamelog.model.LoginResult;
import org.gamelog.model.SignupResult;
import org.mindrot.jbcrypt.BCrypt;

public class AuthRepo {

    public LoginResult loginUser(String username, String password) {
        String loginQuery = "SELECT * FROM login_user(?)";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(loginQuery)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {

                if(!rs.next()){
                    return new LoginResult(false, "*User not found!*");
                }

                String hashedPassword = rs.getString(3);

                if(BCrypt.checkpw(password, hashedPassword)){
                    return new LoginResult(true, "*User successfully logged in!*");
                }else{
                    return new LoginResult(false, "*Invalid password!*");
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
            return new LoginResult(false, "*Database error!*");
        }
    }


    public SignupResult signupUser(String username, String email, String password) {

        if(usernameExists(username)){
            return new SignupResult(false, "*Username already taken!*");
        }

        if(emailExists(email)){
            return new SignupResult(false, "*Email already registered!*");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String signupQuery = "SELECT signup_user(?,?,?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(signupQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.execute();

            return new SignupResult(true, "*Signup successful!*");

        }catch (SQLException e){
            e.printStackTrace();
            return new SignupResult(false, "*Database error!*");
        }
    }


    public boolean usernameExists(String username) {
        String usernameExistsQuery = "SELECT username_exists(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(usernameExistsQuery)) {

            stmt.setString(1, username);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getBoolean(1);
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean emailExists(String email) {
        String emailExistsQuery = "SELECT email_exists(?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(emailExistsQuery)) {

            stmt.setString(1, email);

            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return rs.getBoolean(1);
                }
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }


}
