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
        try{
            Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement loginStatement = connection.prepareStatement("SELECT * FROM login_user(?)");
            loginStatement.setString(1, username);

            ResultSet rs = loginStatement.executeQuery();


            //User not found
            if(!rs.next()){
                return new LoginResult(false, "*User not found!*");
            }

            String hashedPassword = rs.getString(3);

            if(BCrypt.checkpw(password, hashedPassword)){
                return new LoginResult(true, "*User successfully logged in!");
            }else{  //Wrong password
                return new LoginResult(false, "*Invalid password!");
            }
        }catch(SQLException e){
            e.printStackTrace();
            return new LoginResult(false, "*Database error!*");
        }
    }

    public SignupResult signupUser(String username, String email, String password) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();

            if (usernameExists(username)) {
                return new SignupResult(false, "*Username already taken!*");
            }
            if (emailExists(email)) {
                return new SignupResult(false, "*Email already registered!*");
            }

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            PreparedStatement signupStatement = conn.prepareStatement("SELECT signup_user(?,?,?)");
            signupStatement.setString(1, username);
            signupStatement.setString(2, email);
            signupStatement.setString(3, hashedPassword);
            signupStatement.execute();

            return new SignupResult(true, "*Signup successful!*");

        } catch (SQLException e) {
            e.printStackTrace();
            return new SignupResult(false, "*Database error!*");
        }
    }

    private boolean usernameExists(String username){
        try{
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT username_exists(?)");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return rs.getBoolean(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean emailExists(String email){
        try{
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement statement = conn.prepareStatement("SELECT email_exists(?)");
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if(rs.next()){
                return rs.getBoolean(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
